package com.github.bjuvensjo.rsimulator.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;

import static org.junit.Assert.assertEquals;

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
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("Hello Test5, says GlobalResponse.groovy!", simulatorResponse.getResponse());
    }
}
