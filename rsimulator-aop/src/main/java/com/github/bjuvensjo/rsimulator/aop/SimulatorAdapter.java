package com.github.bjuvensjo.rsimulator.aop;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The SimulatorAdapter is used to simulate java interface method invocations by means of AOP.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
class SimulatorAdapter {
    private static final String CONTENT_TYPE = "xml";
    private static final String REQUEST_BEGIN = "<request>";
    private static final String REQUEST_END = "</request>";
    private static final String RESPONSE_BEGIN = "<response>";
    private static final int RESPONSE_BEGIN_LENGTH = RESPONSE_BEGIN.length();
    private static final String RESPONSE_END = "</response>";
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private Logger log = LoggerFactory.getLogger(SimulatorAdapter.class);
    @Inject
    private Simulator simulator;

    /**
     * Returns some simulation response if found.
     *
     * @param declaringClassCanonicalName the class that declares the intercepted method
     * @param methodName                  the name of the intercepted method
     * @param arguments                   the arguments to the intercepted method
     * @param rootPath                    the root path in which to (recursively) find simulator test data
     * @param useRootRelativePath         true if the declaringClassCanonicalName and methodName should be used as an relative path extension of rootPath, otherwise false
     * @return some simulation response
     * @throws Exception
     */
    public Object service(String declaringClassCanonicalName, String methodName, Object[] arguments, String rootPath, boolean useRootRelativePath)
            throws Exception {
        log.debug("declaringClassCanonicalName: {}, methodName: {}, arguments: {}, rootPath: {}, useRootRelativePath: {}",
                new Object[]{declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath});

        String rootRelativePath = useRootRelativePath ? getRootRelativePath(declaringClassCanonicalName, methodName) : "";
        String simulatorRequest = createRequest(arguments);
        
        Optional<SimulatorResponse> simulatorResponseOptional = simulator.service(rootPath, rootRelativePath, simulatorRequest, CONTENT_TYPE);
        SimulatorResponse simulatorResponse = simulatorResponseOptional.get();

        Object response = createResponse(simulatorResponse.getResponse());
        if (response instanceof Throwable) {
            throw (Exception) response;
        }
        return response;
    }

    private String createRequest(Object[] arguments) {
        XStream xstream = new XStream();
        StringBuilder request = new StringBuilder();
        request.append(XML_VERSION);
        request.append(REQUEST_BEGIN);
        for (Object argument : arguments) {
            request.append(xstream.toXML(argument));
        }
        request.append(REQUEST_END);
        log.debug("request: {}", request);
        return request.toString();
    }

    private Object createResponse(String theResponse) {
        int startResponseIndex = theResponse.indexOf(RESPONSE_BEGIN);
        int stopResponseIndex = theResponse.lastIndexOf(RESPONSE_END);
        String responseElement = theResponse.substring(startResponseIndex + RESPONSE_BEGIN_LENGTH, stopResponseIndex);
        log.debug("responseElement: {}", responseElement);

        Object response = null;
        // An empty response should mean void
        if (responseElement.trim().length() > 0) {
            response = new XStream().fromXML(responseElement);
        }

        log.debug("response: {}", response);
        return response;
    }

    private String getRootRelativePath(String declaringClassCanonicalName, String methodName) {
        return new StringBuilder().append(declaringClassCanonicalName.replace('.', '/')).append("/").append(methodName)
                .toString();
    }
}
