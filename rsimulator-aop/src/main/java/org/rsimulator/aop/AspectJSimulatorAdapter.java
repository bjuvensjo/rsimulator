package org.rsimulator.aop;

import java.io.File;
import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * The AspectJSimulatorAdapter is used to simulate java interface method invocations by means of method AspectJ AOP. No
 * interface implementation is needed, since the interface implementation is provided by the AspectJSimulatorAdapter
 * together with simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
//TODO Change javadoc
public class AspectJSimulatorAdapter {
    //TODO Inject
    private SimulatorAdapter simulatorAdapter = new SimulatorAdapter();
    
    /**
     * Returns some simulation response if found.
     * 
     * @param pjp the ProceedingJoinPoint
     * @param rootPath the rootPath
     * @param useRootRelativePath the useRootRelativePath
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    public Object invoke(ProceedingJoinPoint pjp, String rootPath, boolean useRootRelativePath) throws IOException {
        String declaringClassCanonicalName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        Object[] arguments = pjp.getArgs(); 
        return simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath);
    }
    
    /**
     * Returns some simulation response if found.
     * 
     * @param pjp the ProceedingJoinPoint
     * @param testClass used to set the rootPath to the folder of the specified testClass class file folder
     * @param useRootRelativePath the useRootRelativePath
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    public Object invoke(ProceedingJoinPoint pjp, Class<? extends Object> testClass, boolean useRootRelativePath) throws IOException {
        String resource = new StringBuilder().append(testClass.getSimpleName()).append(".class").toString(); 
        return invoke(pjp, new File(testClass.getResource(resource).getPath()).getParentFile().getPath(), useRootRelativePath);
    }    
}
