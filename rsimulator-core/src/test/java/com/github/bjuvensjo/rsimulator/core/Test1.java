package com.github.bjuvensjo.rsimulator.core;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test1.
 */
public class Test1 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/").toURI()).toString();
        String rootRelativePath = File.separator;
        String request = "Hello Simulator, says Test1!";
        String contentType = "txt";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("Hello Test1, says Simulator!", simulatorResponse.getResponse());

        // to test cache
        simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("Hello Test1, says Simulator!", simulatorResponse.getResponse());
    }

    @Test
    public void missingMockTest() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/").toURI()).toString();
        String rootRelativePath = File.separator;
        String request = "missing!";
        String contentType = "txt";
        Optional<SimulatorResponse> maybeSimulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType);
        assertFalse(maybeSimulatorResponse.isPresent());
    }
}
