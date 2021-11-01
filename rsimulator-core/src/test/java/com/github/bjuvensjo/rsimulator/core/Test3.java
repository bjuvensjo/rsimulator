package com.github.bjuvensjo.rsimulator.core;

import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.XmlHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class Test3 {

    @Test
    public void testXmlParseHandler() throws URISyntaxException {
        Injector injector = Guice.createInjector(new CoreModule());
        doTest(injector.getInstance(Simulator.class), "xml");
    }

    @Test
    public void testReplaceXmlHandler() throws URISyntaxException {
        Injector injector = Guice.createInjector(new CoreModule() {
            protected Map<String, Handler> getHandlerMap() {
                Map<String, Handler> handlerMap = super.getHandlerMap();
                handlerMap.put("xml", new XmlHandler());
                return handlerMap;
            }
        });
        doTest(injector.getInstance(Simulator.class), "xml");
    }

    @Test
    public void testAdditionalXmlHandler() throws URISyntaxException {
        Injector injector = Guice.createInjector(new CoreModule() {
            protected Map<String, Handler> getHandlerMap() {
                Map<String, Handler> handlerMap = super.getHandlerMap();
                handlerMap.put("xml2", new XmlHandler());
                return handlerMap;
            }
        });
        Simulator simulator = injector.getInstance(Simulator.class);
        doTest(simulator, "xml");
        doTest(simulator, "xml2");
    }

    private void doTest(Simulator simulator, String contentType) throws URISyntaxException {
        String rootPath = Paths.get(getClass().getResource("/test3").toURI()).toString();
        String rootRelativePath = File.separator;
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:hel=\"http://www.github.com/bjuvensjo/rsimulator/SayHello/\"><soapenv:Header/><soapenv:Body>"
                + "<hel:SayHelloRequest><from>Test3</from><to>Simulator</to><greeting>Hello</greeting>"
                + "</hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>";
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).get();
        assertNotNull(simulatorResponse);
        assertNotNull(simulatorResponse.getResponse());
    }
}
