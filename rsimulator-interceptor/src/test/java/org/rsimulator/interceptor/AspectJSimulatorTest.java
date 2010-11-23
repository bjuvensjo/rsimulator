package org.rsimulator.interceptor;

import static org.junit.Assert.assertEquals;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;


public class AspectJSimulatorTest {
    private Foo foo;
    
    @SuppressWarnings("unused")
    @Aspect
    private static class TestAspect extends AspectJSimulator {        
        public TestAspect() {
            setRootPath(AspectJSimulatorTest.class);
        }

        @Pointcut("call(* Foo.*(..)) && within(AspectJSimulatorTest)")
        protected void aspectJSimulatorPointcut() {            
        }
        
    }
    
    @Test
    public void test() {
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AspectJSimulator", msg);
    }
}
