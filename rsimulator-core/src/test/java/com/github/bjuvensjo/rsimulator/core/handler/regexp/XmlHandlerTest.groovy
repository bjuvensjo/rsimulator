package com.github.bjuvensjo.rsimulator.core.handler.regexp

import com.github.bjuvensjo.rsimulator.core.util.Props
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class XmlHandlerTest extends Specification {
    @Shared
    XmlHandler xmlHandler

    def 'setupSpec'() {
        xmlHandler = new XmlHandler()
        xmlHandler.props = Mock(Props) {
            ignoreXmlNamespaces() >> false
        }
    }

    @Unroll
    def 'format #xml'(String xml, String expected) {
        expect:
        xmlHandler.format(xml) == expected
        where:
        xml                                   | expected
        null                                  | null
        ""                                    | ""
        '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from>(.*)  </from>
                     <to>(x{1, 3})</to>
                     <greeting>.*</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>''' | '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/"><soapenv:Header></soapenv:Header><soapenv:Body><hel:SayHelloRequest><from>(.*)</from><to>(x{1, 3})</to><greeting>.*</greeting></hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>'
        '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from></from>
                     <to></to>
                     <greeting>Hello</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>''' | '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/"><soapenv:Header></soapenv:Header><soapenv:Body><hel:SayHelloRequest><from></from><to></to><greeting>Hello</greeting></hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>'
    }

    @Unroll
    def 'format no namespaces #xml'(String xml, String expected) {
        xmlHandler = new XmlHandler()
        xmlHandler.props = Mock(Props) {
            ignoreXmlNamespaces() >> true
        }
        when:
        System.setProperty("rsimulator.noXmlNameSpace", "true")
        String formattedXml = xmlHandler.format(xml)
        System.clearProperty("rsimulator.noXmlNameSpace")
        then:
        formattedXml == expected
        where:
        xml                                   | expected
        null                                  | null
        ""                                    | ""
        '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest a1="v1" a2="v2">
                     <from>(.*)  </from>
                     <to>(x{1, 3})</to>
                     <greeting>.*</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>''' | '<Envelope><Header></Header><Body><SayHelloRequest a1="v1" a2="v2"><from>(.*)</from><to>(x{1, 3})</to><greeting>.*</greeting></SayHelloRequest></Body></Envelope>'
        '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from></from>
                     <to></to>
                     <greeting>Hello</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>''' | '<Envelope><Header></Header><Body><SayHelloRequest><from></from><to></to><greeting>Hello</greeting></SayHelloRequest></Body></Envelope>'
    }

    @Unroll
    def 'escape #xml'(String xml, String expected) {
        expect:
        xmlHandler.escape(xml, true) == expected
        where:
        xml                                   | expected
        null                                  | null
        ""                                    | ""
        '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from></from>
                     <to></to>
                     <greeting>Hello</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>''' | '''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloRequest>
                     <from></from>
                     <to></to>
                     <greeting>Hello</greeting>
                  </hel:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>'''
    }
}
