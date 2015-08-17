package com.github.bjuvensjo.rsimulator.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;

import static org.junit.Assert.assertNotNull;

/**
 * Test3.
 *
 * @author Magnus Bjuvensjö
 */
public class Test3 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test3").getPath();
        String rootRelativePath = "";
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:hel=\"http://www.github.com/bjuvensjo/rsimulator/SayHello/\"><soapenv:Header/><soapenv:Body>"
                + "<hel:SayHelloRequest><from>Test3</from><to>Simulator</to><greeting>Hello</greeting>"
                + "</hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>";
        String contentType = "xml";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertNotNull(simulatorResponse);
        assertNotNull(simulatorResponse.getResponse());
    }
}
