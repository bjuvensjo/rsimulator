package com.github.bjuvensjo.rsimulator.aop;

import static org.junit.Assert.assertEquals;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;


public class AspectJSimulatorTest {
    private Foo foo;
    
    @Aspect
    private static class SimulatorAspect {   
        private AspectJSimulatorAdapter aspectJSimulatorAdapter = new AspectJSimulatorAdapterImpl();
        
        @Around("call(* Foo.*(..)) && within(AspectJSimulatorTest)")
        public Object invoke(ProceedingJoinPoint pjp) throws Exception {            
            return aspectJSimulatorAdapter.invoke(pjp, AspectJSimulatorTest.class, false);
        }               
    }

    @Test
    public void test() {
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AspectJSimulatorAdapter", msg);
    }
}
