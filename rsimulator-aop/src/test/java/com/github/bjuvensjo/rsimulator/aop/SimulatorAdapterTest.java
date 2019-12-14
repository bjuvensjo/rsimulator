package com.github.bjuvensjo.rsimulator.aop;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SimulatorAdapterTest {
    @Inject
    private SimulatorAdapter simulatorAdapter;

    @BeforeEach
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);
    }

    @Test
    public void testService() throws URISyntaxException {
        String declaringClassCanonicalName = Foo.class.getCanonicalName();
        String methodName = "sayHello";
        Object[] arguments = new String[]{"Hello from " + getClass().getName()};
        String rootPath = Paths.get(getClass().getResource("/").toURI()) + File.separator;

        try {
            String msg = (String) simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, true);
            assertEquals("Hello " + getClass().getName() + " from SimulatorAdapter", msg);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
