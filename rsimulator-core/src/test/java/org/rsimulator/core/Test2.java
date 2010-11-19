package org.rsimulator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.DIModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test2.
 *
 * @author Magnus Bjuvensjö
 */
public class Test2 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new DIModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = "src/test/resources";
        String rootRelativePath = "";
        String request = "Hello Simulator, says Test2!";
        String contentType = "txt";
        try {
            SimulatorResponse simulatorResponse = simulator
                    .service(rootPath, rootRelativePath, request, contentType);
            simulator.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("Hello Test2, says Simulator!", simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
