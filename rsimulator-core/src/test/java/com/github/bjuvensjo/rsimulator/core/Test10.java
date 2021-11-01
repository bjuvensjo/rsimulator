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

import static org.junit.Assert.*;

public class Test10 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    private String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:hel=\"http://www.github.com/bjuvensjo/rsimulator/SayHello/\"><soapenv:Header/><soapenv:Body>"
            + "<hel:SayHelloRequest><from>Test10</from><to>Simulator</to><inner><greeting>Hello</greeting><code>10</code></inner>"
            + "</hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>";

    @Test
    public void groupTest() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/test10/group").toURI()).toString();
        String rootRelativePath = File.separator;
        String contentType = "xml";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertNotNull(simulatorResponse);
        assertNotNull(simulatorResponse.getResponse());
    }

    @Test
    public void missingTest() throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/test10/missing").toURI()).toString();
        String rootRelativePath = File.separator;
        String contentType = "xml";
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType);
        assertFalse(simulatorResponse.isPresent());
    }
}
