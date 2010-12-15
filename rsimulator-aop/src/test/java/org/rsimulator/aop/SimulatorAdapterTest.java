package org.rsimulator.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class SimulatorAdapterTest {
    @Inject
    private SimulatorAdapter simulatorAdapter;
    
    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);                        
    }

    @Test
    public void testService() {
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
