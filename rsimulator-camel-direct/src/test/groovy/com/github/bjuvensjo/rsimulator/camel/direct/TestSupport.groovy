package com.github.bjuvensjo.rsimulator.camel.direct

import org.apache.camel.*
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange
import spock.lang.Shared
import spock.lang.Specification

// TODO Move to another module!!!
class TestSupport extends Specification {
    @Shared
    CamelContext context
    @Shared
    ProducerTemplate template

    def init(Map components = [:]) {
        context = new DefaultCamelContext()
        components.each { k, v ->
            setComponent(k, v)
        }
        context.start()
        template = context.createProducerTemplate()
    }

    def setComponent(String name, Component component) {
        if (context.getComponent(name) != null) {
            context.removeComponent(name)
        }

        context.addComponent(name, component);
    }

    def createExchangeWithBody(Object body, ExchangePattern exchangePattern = ExchangePattern.InOut) {
        Exchange exchange = new DefaultExchange(context, exchangePattern)
        Message message = exchange.getIn()
        message.setHeader("testClass", this.getClass().getName())
        message.setBody(body)
        exchange
    }

    def cleanupSpec() {
        if (context) {
            context.stop()
        }
    }
}
