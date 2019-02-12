package com.github.bjuvensjo.rsimulator.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class SimulatorAdapterTest {
    @Inject
    private SimulatorAdapter simulatorAdapter;
    
    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);                        
    }

    @Test
    public void testService() throws URISyntaxException {
        String declaringClassCanonicalName = Foo.class.getCanonicalName();
        String methodName = "sayHello";
        Object[] arguments = new String[] {"Hello from " + getClass().getName()};
        String rootPath = Paths.get(getClass().getResource("/").toURI()).toString() + File.separator;
        boolean useRootRelativePath = true;
        
        try {
            String msg = (String) simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, useRootRelativePath);
            assertEquals("Hello " + getClass().getName() + " from SimulatorAdapter", msg);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
