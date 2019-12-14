package com.github.bjuvensjo.rsimulator.aop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
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
    public void testWithRootRelativePath() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/").toURI()) + File.separator;
        aopAllianceSimulator.setRootPath(rootPath);
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

    @Test
    public void testDoNotReturn() {
        aopAllianceSimulator.setRootPath(this.getClass());
        aopAllianceSimulator.setUseRootRelativePath(false);
        try {
            foo.doNotReturn("Do not return");
        } catch (Exception exception) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }
    }
}
