package org.rsimulator.interceptor;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * The AspectJSimulator is used to simulate java interface method invocations by means of method AspectJ AOP. No
 * interface implementation is needed, since the interface implementation is provided by the AspectJSimulator
 * together with simulation test data.
 * 
 * @author Magnus Bjuvensjö
 */
@Aspect
public abstract class AspectJSimulator extends AbstractAopSimulator {

    @Pointcut("")
    protected abstract void aspectJSimulatorPointcut();

    /**
     * Returns some simulation response if found.
     * 
     * @param pjp the ProceedingJoinPoint
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    @Around("aspectJSimulatorPointcut()")
    public Object invoke(ProceedingJoinPoint pjp) throws IOException {
        String declaringClassCanonicalName = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();
        Object[] arguments = pjp.getArgs();
        return call(declaringClassCanonicalName, methodName, arguments);
    }
}
