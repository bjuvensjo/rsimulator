package org.rsimulator.aop;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.aop.AopAllianceSimulator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

public class GuiceAopAllianceSimulatorTest {
    private Foo foo;
    
    public static class DummyFoo implements Foo {
        @Override
        public String sayHello(String msg) {
            return null;
        }
    }
 
    @Before
    public void init() { 
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Foo.class).to(DummyFoo.class);
                AopAllianceSimulator aopAllianceSimulator = new AopAllianceSimulator();
                aopAllianceSimulator.setRootPath(GuiceAopAllianceSimulatorTest.class);
                bindInterceptor(Matchers.subclassesOf(Foo.class), Matchers.any(), aopAllianceSimulator);
            }
        });
        foo = injector.getInstance(Foo.class);
    }
    
    @Test
    public void test() {        
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AopAllianceSimulator", msg);
    }
}
