package com.github.bjuvensjo.rsimulator.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.ImplementedBy;

/**
 * The AopAllianceSimulator is used to simulate java interface method invocations by means of method interception. No
 * interface implementation is needed, since the interface implementation is provided by the AopAllianceSimulator
 * together with simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(AopAllianceSimulatorImpl.class)
public interface AopAllianceSimulator extends Advice, MethodInterceptor {

    /**
     * Sets the rootPath to the folder of the specified testClass class file folder.
     * 
     * @param testClass the testClass
     */
    void setRootPath(Class<? extends Object> testClass);

    /**
     * Sets the rootPath to the specified aRootPath.
     * 
     * @param aRootPath the rootPath
     */
    void setRootPath(String aRootPath);

    /**
     * Sets if a root relative path should be used. If true, the relative path is constructed by the package name of the
     * intercepted class.
     * 
     * @param aUseRootRelativePath the useRelativeRootPath
     */
    void setUseRootRelativePath(boolean aUseRootRelativePath);

}
