package com.github.bjuvensjo.rsimulator.aop;

import java.io.File;

import org.aspectj.lang.ProceedingJoinPoint;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * AspectJSimulatorAdapterImpl.
 * 
 * @author Magnus Bjuvensjö
 */
public class AspectJSimulatorAdapterImpl implements AspectJSimulatorAdapter {
    @Inject
    private SimulatorAdapter simulatorAdapter;
    
    /**
     * Creates an AspectJSimulatorAdapterImpl
     */
    public AspectJSimulatorAdapterImpl() {
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);                
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(ProceedingJoinPoint pjp, String rootPath, boolean useRootRelativePath) throws Exception {
        String declaringClassCanonicalName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        Object[] arguments = pjp.getArgs(); 
        return simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(ProceedingJoinPoint pjp, Class<? extends Object> testClass, boolean useRootRelativePath) throws Exception {
        String resource = new StringBuilder().append(testClass.getSimpleName()).append(".class").toString(); 
        return invoke(pjp, new File(testClass.getResource(resource).getPath()).getParentFile().getPath(), useRootRelativePath);
    }    
}
