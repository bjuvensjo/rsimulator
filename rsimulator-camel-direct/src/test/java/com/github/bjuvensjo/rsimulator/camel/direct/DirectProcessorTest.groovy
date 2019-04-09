package com.github.bjuvensjo.rsimulator.camel.direct

import com.github.bjuvensjo.rsimulator.core.Simulator
import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Specification
import spock.lang.Unroll

class DirectProcessorTest extends Specification {
    DirectProcessor directProcessor = new DirectProcessor('endpointUri', new DirectComponentConfig('rootPath'))
    Exchange exchange = new DefaultExchange(new DefaultCamelContext())
    Simulator simulator = Mock()

    def setup() {
        directProcessor.simulator = simulator
        exchange.getIn().setBody('request')
    }

    def 'process'() {
        when:
        simulator.service(*_) >> new Optional<>(new SimulatorResponseImpl('response', null, null))
        directProcessor.process(exchange)

        then:
        exchange.getOut().getBody() == 'response'
    }

    def 'process thrown'() {
        when:
        simulator.service(*_) >> new Optional<>()
        directProcessor.process(exchange)

        then:
        thrown(Exception.class)
    }


    @Unroll("getSimulatorContentType #contentType, #accept, #expected")
    def 'getSimulatorContentType'() {
        when:
        exchange.getIn().setHeader('Content-Type', contentType ? "$contentType; charset=utf-8" : null)
        exchange.getIn().setHeader('Accept', accept ? "$accept; charset=utf-8" : null)

        then:
        directProcessor.getSimulatorContentType(exchange) == expected

        where:
        contentType        | accept             | expected
        // txt
        'text/html'        | null               | 'txt'
        'text/html'        | 'text/html'        | 'txt'
        null               | 'text/html'        | 'txt'
        // xml
        'application/xml'  | null               | 'xml'
        'application/xml'  | 'application/xml'  | 'xml'
        null               | 'application/xml'  | 'xml'
        // json
        'application/json' | null               | 'json'
        'application/json' | 'application/json' | 'json'
        null               | 'application/json' | 'json'
        // default
        null               | null               | 'txt'
    }
}