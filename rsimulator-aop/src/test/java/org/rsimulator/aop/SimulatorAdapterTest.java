package org.rsimulator.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

public class SimulatorAdapterTest {

    @Test
    public void testService() {
        //TODO DI
        SimulatorAdapter simulatorAdapter = new SimulatorAdapter();
        String declaringClassCanonicalName = Foo.class.getCanonicalName();
        String methodName = "sayHello";
        Object[] arguments = new String[] {"Hello from " + getClass().getName()};
        String rootPath = getClass().getResource("/").getPath();
        boolean useRootRelativePath = true;
        
        try {
            String msg = (String) simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath);
            assertEquals("Hello " + getClass().getName() + " from SimulatorAdapter", msg);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
