package org.rsimulator.aop;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.rsimulator.aop.AspectJSimulatorAdapter;


public class AspectJSimulatorTest {
    private Foo foo;
    
    @SuppressWarnings("unused")
    @Aspect
    private static class SimulatorAspect {   
        //TODO Inject
        private AspectJSimulatorAdapter aspectJSimulatorAdapter = new AspectJSimulatorAdapter();
        
        @Around("call(* Foo.*(..)) && within(AspectJSimulatorTest)")
        public Object invoke(ProceedingJoinPoint pjp) throws IOException {            
            return aspectJSimulatorAdapter.invoke(pjp, AspectJSimulatorTest.class, false);
        }               
    }
    
    @Test
    public void test() {
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AspectJSimulatorAdapter", msg);
    }
}
