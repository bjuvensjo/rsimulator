package com.github.bjuvensjo.rsimulator.core;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

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
    public void test() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/test6").toURI()).toString();
        String rootRelativePath = File.separator;
        String request = "Hello Simulator, says Test6!";
        String contentType = "txt";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("Hello Test6, says Test.groovy!", simulatorResponse.getResponse());
    }
}
