package com.github.bjuvensjo.rsimulator.cxf.transport

import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import org.apache.cxf.Bus
import org.apache.cxf.BusFactory
import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean
import org.apache.cxf.message.Message
import org.apache.cxf.transport.ConduitInitiatorManager
import org.apache.cxf.transport.http.HTTPTransportFactory
import spock.lang.Specification

import javax.jws.WebService
import java.nio.file.Path
import java.nio.file.Paths

class ExampleIT extends Specification {
    @ResourcePath(rootOnly = true)
    String resourcePath
    
    def 'setup'() {
        Bus bus = BusFactory.getThreadDefaultBus()
        RSimulatorTransportFactory rSimulatorTransportFactory = new RSimulatorTransportFactory(resourcePath)
        ConduitInitiatorManager extension = bus.getExtension(ConduitInitiatorManager.class)
        HTTPTransportFactory.DEFAULT_NAMESPACES.each {
            extension.registerConduitInitiator(it, rSimulatorTransportFactory)
        }
    }

    def "When a web service is invoked and RSimulator provides the response"() {
        when:
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean()
        factory.setServiceClass(HelloWorld.class)
        factory.setAddress("http://localhost:9000/helloWorld")
        HelloWorld helloClient = (HelloWorld) factory.create()
        String reply = helloClient.sayHi("world")
        Client client = ClientProxy.getClient(helloClient)
        then:
        reply == "RSimulator world!"
        and:
        client.getResponseContext().get(Message.RESPONSE_CODE) == 201
    }

    @WebService
    interface HelloWorld {
        String sayHi(String text)
    }

    static {
        System.setProperty("net.sf.ehcache.skipUpdateCheck", "true")
    }
}