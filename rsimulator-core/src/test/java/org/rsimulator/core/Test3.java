package org.rsimulator.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.DIModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test3.
 * 
 * @author Magnus Bjuvensjö
 */
public class Test3 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new DIModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test3").getPath();
        String rootRelativePath = "";
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:hel=\"http://www.rsimulator.org/SayHello/\"><soapenv:Header/><soapenv:Body>"
                + "<hel:SayHelloRequest><from>Test3</from><to>Simulator</to><greeting>Hello</greeting>"
                + "</hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>";
        String contentType = "xml";
        try {
            SimulatorResponse simulatorResponse = simulator
                    .service(rootPath, rootRelativePath, request, contentType);
            simulator.service(rootPath, rootRelativePath, request, contentType);
            assertNotNull(simulatorResponse);
            assertNotNull(simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
