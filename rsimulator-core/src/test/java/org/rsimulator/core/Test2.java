package org.rsimulator.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.CoreModule;

import static org.junit.Assert.assertEquals;

/**
 * Test2.
 *
 * @author Magnus Bjuvensjö
 */
public class Test2 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/").getPath();
        String rootRelativePath = "";
        String request = "Hello Simulator, says Test2!";
        String contentType = "txt";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("Hello Test2, says Simulator!", simulatorResponse.getResponse());
    }
}
