package com.github.bjuvensjo.rsimulator.aop;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

public class GuiceAopAllianceSimulatorTest {
    @Inject
    private Foo foo;
    
    public static class DummyFoo implements Foo {
        @Override
        public String sayHello(String msg) {
            return null;
        }

		@Override
		public String doThrow(String msg) throws BarException {
			return null;
		}

        @Override
        public void doNotReturn(String msg) {
            
        }
    }
 
    @Before
    public void init() { 
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Foo.class).to(DummyFoo.class);
                AopAllianceSimulator aopAllianceSimulator = new AopAllianceSimulatorImpl();
                aopAllianceSimulator.setRootPath(GuiceAopAllianceSimulatorTest.class);
                bindInterceptor(Matchers.subclassesOf(Foo.class), Matchers.any(), aopAllianceSimulator);
            }
        });
        injector.injectMembers(this);
    }
    
    @Test
    public void test() {        
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AopAllianceSimulator", msg);
    }
}
