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
 * Test5.
 *
 * @author Magnus Bjuvensjö
 */
public class Test5 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test5").getPath();
        String rootRelativePath = "/.";
        String request = "Hello Simulator, says Test5!";
        String contentType = "txt";
        try {
            SimulatorResponse simulatorResponse = simulator
                    .service(rootPath, rootRelativePath, request, contentType);
            simulator.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("Hello Test5, says GlobalResponse.groovy!", simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
