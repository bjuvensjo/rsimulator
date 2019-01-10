package com.github.bjuvensjo.rsimulator.cxf.transport

import com.github.bjuvensjo.rsimulator.core.Simulator
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse
import org.apache.cxf.Bus
import org.apache.cxf.io.CachedOutputStream
import org.apache.cxf.message.Exchange
import org.apache.cxf.message.Message
import org.apache.cxf.message.MessageImpl
import org.apache.cxf.service.model.EndpointInfo
import org.apache.cxf.transport.MessageObserver
import spock.lang.Specification

import java.util.stream.Collectors

class RSimulatorConduitTest extends Specification {
    static {
        System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
    }

    def "should prepare message"() {
        when:
        RSimulatorConduit conduit = new RSimulatorConduit(Mock(EndpointInfo.class), Mock(Simulator.class), Mock(Bus.class), "/")
        def message = Mock(Message) {
            1 * setContent(OutputStream.class, _)
        }
        then:
        conduit.prepare(message)
    }

    def "should not notify observer for inbound messages"() {
        def observer = Mock(MessageObserver)
        def message = Mock(Message)

        setup:
        message.get("org.apache.cxf.message.inbound") >> Boolean.TRUE
        when:
        RSimulatorConduit conduit = new RSimulatorConduit(Mock(EndpointInfo.class), Mock(Simulator.class), Mock(Bus.class), "/")
        conduit.setMessageObserver(observer)
        conduit.close(message)
        then:
        0 * observer.onMessage(message)
    }

    def "should notify observer for non-inbound messages"() {
        def observer = Mock(MessageObserver)
        Message message = new MessageImpl()
        message.put("org.apache.cxf.message.Message.ENCODING", "UTF-8")
        message.put(Message.ENDPOINT_ADDRESS, "http://host/foo/bar")
        message.setContent(OutputStream.class, Mock(CachedOutputStream) {
            getBytes() >> "request".getBytes("UTF-8")
        })
        message.setExchange(Mock(Exchange))
        def properties = Mock(Properties) {
            containsKey("responseCode") >> true
            get("responseCode") >> "200"
        }
        Message result
        def response = Mock(SimulatorResponse) {
            getResponse() >> "response"
            getProperties() >> Optional.of(properties)
        }

        def simulator = Mock(Simulator) {
            1 * service("/root", "/foo/bar", "request", "xml") >> Optional.of(response)
        }

        when:
        RSimulatorConduit conduit = new RSimulatorConduit(Mock(EndpointInfo.class), simulator, Mock(Bus.class), "/root")
        conduit.setMessageObserver(observer)
        conduit.close(message)
        then:
        1 * observer.onMessage(_ as Message) >> { args -> result = args[0] }
        and:
        InputStream inputStream = result.getContent(InputStream.class)
        inputStream != null
        and:
        new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n")) == "response"
        and:
        result.get(Message.RESPONSE_CODE) == 200
    }
}
