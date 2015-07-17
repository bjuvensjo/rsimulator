package com.github.bjuvensjo.rsimulator.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import com.google.inject.ImplementedBy;

/**
 * The AspectJSimulatorAdapter is used to simulate java interface method invocations by means of method AspectJ AOP. No
 * interface implementation is needed, since the interface implementation is provided by the AspectJSimulatorAdapter
 * together with simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(AspectJSimulatorAdapterImpl.class)
public interface AspectJSimulatorAdapter {
    
    /**
     * Returns some simulation response if found.
     * 
     * @param pjp the ProceedingJoinPoint
     * @param rootPath the rootPath
     * @param useRootRelativePath the useRootRelativePath
     * @return some simulation response
     * @throws Exception if something goes wrong
     */
    Object invoke(ProceedingJoinPoint pjp, String rootPath, boolean useRootRelativePath) throws Exception;
    
    /**
     * Returns some simulation response if found.
     * 
     * @param pjp the ProceedingJoinPoint
     * @param testClass used to set the rootPath to the folder of the specified testClass class file folder
     * @param useRootRelativePath the useRootRelativePath
     * @return some simulation response
     * @throws Exception if something goes wrong
     */
    Object invoke(ProceedingJoinPoint pjp, Class<? extends Object> testClass, boolean useRootRelativePath) throws Exception;    
}
