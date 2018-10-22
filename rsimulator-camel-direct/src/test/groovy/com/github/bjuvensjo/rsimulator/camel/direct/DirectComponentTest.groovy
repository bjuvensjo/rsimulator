package com.github.bjuvensjo.rsimulator.camel.direct

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import spock.lang.Unroll

class DirectComponentTest extends TestSupport {

    def setupSpec() {
        init([http: new DirectComponent()])

        context.addRoutes(new RouteBuilder() {
            void configure() {
                ['text/html', 'application/xml', 'application/json'].each {
                    from("direct:$it")
                            .setHeader('Content-Type', simple("$it; charset=utf-8"))
                            .to("http://localhost:8888/$it")
                }
            }
        })
    }

    @Unroll("call #endPointUri, #request, #expected")
    def 'call'() {
        when:
        Exchange responseExchange = template.send(endPointUri, createExchangeWithBody(request))
        String responseBody = responseExchange.getOut().getBody(String.class)

        then:
        responseBody == expected

        where:
        endPointUri               | request                     | expected
        'direct:text/html'        | 'Hello'                     | 'Hello from rsimulator!'
        'direct:text/html'        | 'This a one request to CSL' | 'This is the response to CSL'
        'direct:text/html'        | 'This a one request to LF'  | 'This is the response to LF'
        'direct:text/html'        | '2, 5'                      | '2 + 5 = 7'
        'direct:text/html'        | '7, 9'                      | '7 + 9 = 16'
        'direct:application/xml'  | '<request>Hello</request>'  | "<?xml version='1.0' encoding='UTF-8'?>\n<response>Hello from rsimulator!</response>"
        'direct:application/json' | '{"message": "Hello"}'      | '{"response": "Hello from rsimulator!"}'
    }
}