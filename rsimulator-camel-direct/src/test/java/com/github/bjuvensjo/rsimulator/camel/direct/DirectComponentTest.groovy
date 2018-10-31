package com.github.bjuvensjo.rsimulator.camel.direct

import groovy.json.JsonOutput
import groovy.xml.XmlUtil
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import spock.lang.Unroll

class DirectComponentTest extends TestSupport {
    static String TEXT = 'text/html'
    static String XML = 'application/xml'
    static String JSON = 'application/json'
    static String NOT_SUPPORTED = 'NOT_SUPPORTED'

    def setupSpec() {
        init([http: new DirectComponent()])

        context.addRoutes(new RouteBuilder() {
            void configure() {
                [TEXT, XML, JSON, NOT_SUPPORTED].each {
                    from("direct:$it")
                            .setHeader('Content-Type', simple("$it; charset=utf-8"))
                            .to("http://localhost:8888/$it")
                }
            }
        })
    }

    @Unroll("call #type, #request, #responseCode, #expected")
    def 'call'() {
        when:
        Exchange responseExchange = template.send("direct:$type", createExchangeWithBody(request))
        String responseBody = responseExchange.getOut().getBody(String.class)
        def normalizedExpected, normalizedResponseBody
        switch (type) {
            case JSON:
                normalizedExpected = JsonOutput.prettyPrint(expected)
                normalizedResponseBody = JsonOutput.prettyPrint(responseBody)
                break
            case XML:
                normalizedExpected = XmlUtil.serialize(expected)
                normalizedResponseBody = XmlUtil.serialize(responseBody)
                break
            default:
                normalizedExpected = expected
                normalizedResponseBody = responseBody
        }

        then:
        responseExchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE) == responseCode
        normalizedResponseBody == normalizedExpected

        where:
        type          | request                     | responseCode | expected
        TEXT          | 'Hello'                     | 200          | 'Hello from rsimulator!'
        TEXT          | 'This is a request to Nils' | 200          | 'Hi Nils'
        TEXT          | 'This is a request to Lisa' | 200          | 'Hi Lisa'
        TEXT          | '2, 6'                      | 200          | '2 + 6 = 8'
        TEXT          | '7, 9'                      | 200          | '7 + 9 = 16'
        JSON          | '{"message": "Hello"}'      | 201          | '{"response": "Hello from rsimulator!"}'
        XML           | '<request>Hello</request>'  | 200          | "<?xml version='1.0' encoding='UTF-8'?>\n<response>Hello from rsimulator!</response>"
        NOT_SUPPORTED | 'request'                   | 400          | "contentType not supported"
        JSON          | 'Will not be found'         | 404          | '"Not Found"'
    }
}