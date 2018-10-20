package com.github.bjuvensjo.rsimulator.camel.direct

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.ProducerTemplate
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

    def setupSpec() {
        context = new DefaultCamelContext()

        if (context.getComponent('http') != null) {
            context.removeComponent('http')
        }

        context.addComponent('http', context.getComponent('rsimulator-direct'));
//        The below lines is the alternative to the above if not the default rootPath can be used.
//        context.addComponent("http", new DirectComponent("./src/test/resources"));

        context.start()

        template = context.createProducerTemplate()
    }


    protected Exchange createExchangeWithBody(Object body) {
        Exchange exchange = new DefaultExchange(context)
        Message message = exchange.getIn()
        message.setHeader("testClass", this.getClass().getName())
        message.setBody(body)
        return exchange
    }

//    def cleanup(){
//        context.stop()
//    }
}
