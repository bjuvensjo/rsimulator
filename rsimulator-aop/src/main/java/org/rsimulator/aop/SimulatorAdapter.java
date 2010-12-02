package org.rsimulator.aop;

import java.io.IOException;

import org.rsimulator.core.Simulator;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.core.config.DIModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.xstream.XStream;

/**
 * The SimulatorAdapter is used to simulate java interface method invocations by means of AOP.
 * 
 * @author Magnus Bjuvensjö
 */
public class SimulatorAdapter {
    private static final String CONTENT_TYPE = "xml";
    private static final String REQUEST_BEGIN = "<request>";
    private static final String REQUEST_END = "</request>";
    private static final String RESPONSE_BEGIN = "<response>";
    private static final int RESPONSE_BEGIN_LENGTH = RESPONSE_BEGIN.length();
    private static final String RESPONSE_END = "</response>";
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private Logger log = LoggerFactory.getLogger(SimulatorAdapter.class);
    private Simulator simulator;

    /**
     * Creates an SimulatorAdapter.
     */
    public SimulatorAdapter() {
        Injector injector = Guice.createInjector(new DIModule());
        simulator = injector.getInstance(Simulator.class);
    }

    /**
     * Returns some simulation response if found.
     * 
     * @param declaringClassCanonicalName the class that declares the intercepted method
     * @param methodName the name of the intercepted method
     * @param arguments the arguments to the intercepted method
     * @param rootPath the root path in which to (recursively) find simulator test data
     * @param useRootRelativePath true if the declaringClassCanonicalName and methodName should be used as an relative path extension of rootPath, otherwise false
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    public Object service(String declaringClassCanonicalName, String methodName, Object[] arguments, String rootPath, boolean useRootRelativePath) 
            throws IOException {
        log.debug(
                "declaringClassCanonicalName: {}, methodName: {}, arguments: {}, rootPath: {}, useRootRelativePath: {}",
                new Object[] {declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath});
        SimulatorResponse simulatorResponse = simulator.service(rootPath,
                useRootRelativePath ? getRootRelativePath(declaringClassCanonicalName, methodName) : "",
                createRequest(arguments), CONTENT_TYPE);
        return createResponse(simulatorResponse.getResponse());
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
        String response = theResponse.substring(startResponseIndex + RESPONSE_BEGIN_LENGTH, stopResponseIndex);
        log.debug("response: {}", response);
        return new XStream().fromXML(response);
    }

    private String getRootRelativePath(String declaringClassCanonicalName, String methodName) {
        return new StringBuilder().append(declaringClassCanonicalName.replace('.', '/')).append("/").append(methodName)
                .toString();
    }
}
