package org.rsimulator.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        
    @Test
    public void testWithoutRootRelativePath() {        
        aopAllianceSimulator.setRootPath(this.getClass());
        aopAllianceSimulator.setUseRootRelativePath(false);
        String msg = foo.sayHello("Hello from " + getClass().getName());
        assertEquals("Hello " + getClass().getName() + " from AopAllianceSimulator", msg);
    }
    
    @Test
    public void testWithRootRelativePath() {        
        aopAllianceSimulator.setRootPath(this.getClass().getResource("/").getPath());
        aopAllianceSimulator.setUseRootRelativePath(true);
        String msg = foo.sayHello("Hi from " + getClass().getName());
        assertEquals("Hello " + getClass().getName(), msg);
    }
    
    @Test
    public void testException() {        
        aopAllianceSimulator.setRootPath(this.getClass());
        aopAllianceSimulator.setUseRootRelativePath(false);
        try {			
        	foo.doThrow("Give me an exception");
        	fail("Exception expected");
		} catch (BarException barException) {	
			assertEquals("1", barException.getCode());
			assertEquals("msg", barException.getMessage());
		}
    }
    
    
}
