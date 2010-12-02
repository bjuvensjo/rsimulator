package org.rsimulator.interceptor;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SpringAopAllianceSimulatorTest {
    @Autowired
    private AopAllianceSimulator aopAllianceSimulator;

    @Autowired
    private Foo foo;
    
    @Before
    public void init() {
        aopAllianceSimulator.setRootPath(this.getClass());
    }
    
    @Test
    public void test() {        
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AopAllianceSimulator", msg);
    }
}
