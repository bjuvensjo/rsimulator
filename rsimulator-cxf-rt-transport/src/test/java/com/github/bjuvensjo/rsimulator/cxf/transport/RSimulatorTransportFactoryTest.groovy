package com.github.bjuvensjo.rsimulator.cxf.transport

import org.apache.cxf.Bus
import org.apache.cxf.service.model.EndpointInfo
import org.apache.cxf.ws.addressing.EndpointReferenceType
import spock.lang.Specification

class RSimulatorTransportFactoryTest extends Specification {
    static {
        System.setProperty("net.sf.ehcache.skipUpdateCheck", "true");
    }

    def "should create a transport factory with minimal config"() {
        when:
        RSimulatorTransportFactory factory = new RSimulatorTransportFactory("/")
        then:
        factory.transportIds.size() == 4
        factory.uriPrefixes.size() == 1
        factory.uriPrefixes.contains("http://") == true
        and:
        factory.getConduit(Mock(EndpointInfo.class), Mock(Bus.class)) != null
        factory.getConduit(Mock(EndpointInfo.class), Mock(EndpointReferenceType.class), Mock(Bus.class)) != null
    }

    def "should create a transport factory with specific config"() {
        when:
        RSimulatorTransportFactory factory = new RSimulatorTransportFactory("/", Arrays.asList("jms://", "rs://", "https://"))
        then:
        factory.transportIds.size() == 4
        factory.uriPrefixes.size() == 3
        ["https://", "jms://", "rs://"].each {
            factory.uriPrefixes.contains(it) == true
        }
        and:
        factory.getConduit(Mock(EndpointInfo.class), Mock(Bus.class)) != null
        factory.getConduit(Mock(EndpointInfo.class), Mock(EndpointReferenceType.class), Mock(Bus.class)) != null
    }
}
