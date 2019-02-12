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
 * Test7.
 *
 * @author Magnus Bjuvensjö
 */
public class Test7 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/test7").toURI()).toString();
        String rootRelativePath = File.separator;
        String request = "[{\"id\": \"12345678901\",\"name\":\"Privatkonto\",\"balance\":34251.15766987801},{\"id\":\"23456789012\",\"name\":\"Lanekonto\",\"balance\":874.8714758855125},{\"id\":\"34567890123\",\"name\":\"Rakningskonto\",\"balance\":18935.745973888184},{\"id\":\"45678901234\",\"name\":\"Semesterkonto\",\"balance\":26763.710168201727},{\"id\":\"56789012345\",\"name\":\"Sparkonto\",\"balance\":281434.8179749503}]";
        String contentType = "json";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertEquals("{\"result\":\"ok\"}", simulatorResponse.getResponse());
    }
}
