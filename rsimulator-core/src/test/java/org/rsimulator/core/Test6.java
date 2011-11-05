package org.rsimulator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test6.
 *
 * @author Magnus Bjuvensjö
 */
public class Test6 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test6").getPath();
        String rootRelativePath = "/.";
        String request = "Hello Simulator, says Test6!";
        String contentType = "txt";
        try {
            SimulatorResponse simulatorResponse = simulator
                    .service(rootPath, rootRelativePath, request, contentType);
            simulator.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("Hello Test6, says Test.groovy!", simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
