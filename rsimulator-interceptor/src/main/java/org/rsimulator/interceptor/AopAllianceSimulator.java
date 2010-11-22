package org.rsimulator.interceptor;

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
public class AopAllianceSimulator extends AbstractAopSimulator implements Advice, MethodInterceptor {

    /**
     * Returns some simulation response if found.
     * 
     * @param mi the MethodInvocation
     * @return some simulation response
     * @throws IOException if something goes wrong
     */
    public Object invoke(MethodInvocation mi) throws IOException {
        Method method = mi.getMethod();
        return call(method.getDeclaringClass().getCanonicalName(), method.getName(), mi.getArguments());
    }
}
