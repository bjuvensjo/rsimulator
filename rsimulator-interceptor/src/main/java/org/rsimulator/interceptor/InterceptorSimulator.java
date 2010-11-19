package org.rsimulator.interceptor;

import java.io.File;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.rsimulator.core.Simulator;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.core.config.DIModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.thoughtworks.xstream.XStream;

/**
 * The InterceptorSimulator is used to simulate java interface method invocations by means of interception. No interface
 * implementation is needed, since the interface implementation is provided by the InterceptorSimulator together with
 * simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
public class InterceptorSimulator implements Advice, MethodInterceptor {
    private static final String CONTENT_TYPE = "xml";
    private static final String REQUEST_BEGIN = "<request>";
    private static final String REQUEST_END = "</request>";
    private static final String RESPONSE_BEGIN = "<response>";
    private static final int RESPONSE_BEGIN_LENGTH = RESPONSE_BEGIN.length();
    private static final String RESPONSE_END = "</response>";
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private Logger log = LoggerFactory.getLogger(InterceptorSimulator.class);
    private Simulator simulator;
    private String rootPath;
    private boolean useRootRelativePath = false;

    /**
     * Creates an InterceptorSimulator.
     */
    public InterceptorSimulator() {
        Injector injector = Guice.createInjector(new DIModule());
        simulator = injector.getInstance(Simulator.class);
    }

    /**
     * Sets the rootPath to the folder of the specified testClass class file folder.
     * 
     * @param testClass the testClass
     */
    public void setRootPath(Class<? extends Object> testClass) {
        String resource = new StringBuilder().append(testClass.getSimpleName()).append(".class").toString();
        setRootPath(new File(testClass.getResource(resource).getPath()).getParentFile().getPath());
    }

    /**
     * Sets the rootPath to the specified aRootPath.
     * 
     * @param aRootPath the rootPath
     */
    public void setRootPath(String aRootPath) {
        this.rootPath = aRootPath;
    }

    /**
     * Sets if a root relative path should be used. If true, the relative path is constructed by the package name of the
     * intercepted class.
     * 
     * @param aUseRootRelativePath the useRelativeRootPath
     */
    public void setUseRootRelativePath(boolean aUseRootRelativePath) {
        this.useRootRelativePath = aUseRootRelativePath;
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation mi) throws Throwable {
        XStream xstream = new XStream();

        StringBuilder request = new StringBuilder();
        request.append(XML_VERSION);
        request.append(REQUEST_BEGIN);
        Object[] arguments = mi.getArguments();
        for (Object argument : arguments) {
            request.append(xstream.toXML(argument));
        }
        request.append(REQUEST_END);
        log.debug("request: {}", request);

        SimulatorResponse simulatorResponse = simulator.service(rootPath,
                useRootRelativePath ? getRootRelativePath(mi.getMethod()) : "", request.toString(), CONTENT_TYPE);
        String response = simulatorResponse.getResponse();
        int startResponseIndex = response.indexOf(RESPONSE_BEGIN);
        int stopResponseIndex = response.lastIndexOf(RESPONSE_END);
        response = response.substring(startResponseIndex + RESPONSE_BEGIN_LENGTH, stopResponseIndex);
        log.debug("response: {}", response);
        return xstream.fromXML(response);
    }

    private String getRootRelativePath(Method method) {
        return new StringBuilder().append(method.getDeclaringClass().getCanonicalName().replace('.', '/')).append("/")
                .append(method.getName()).toString();
    }
}
