package org.rsimulator.aop;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * The AopAllianceSimulator is used to simulate java interface method invocations by means of method interception. No
 * interface implementation is needed, since the interface implementation is provided by the AopAllianceSimulator
 * together with simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
public class AopAllianceSimulator implements Advice, MethodInterceptor {
    //TODO Inject
    private SimulatorAdapter simulatorAdapter = new SimulatorAdapter();
    private String rootPath;
    private boolean useRootRelativePath = false;

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
     * Returns some simulation response if found.
     * 
     * @param mi the MethodInvocation
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    public Object invoke(MethodInvocation mi) throws IOException {
        Method method = mi.getMethod();
        return simulatorAdapter.service(method.getDeclaringClass().getCanonicalName(), method.getName(), mi.getArguments(), rootPath, useRootRelativePath);
    }
}
