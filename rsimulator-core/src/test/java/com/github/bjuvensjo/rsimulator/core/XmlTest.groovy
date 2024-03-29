package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Injector
import groovy.xml.XmlSlurper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class XmlTest extends Specification {
    @Shared
    Simulator simulator
    @ResourcePath(rootOnly = true)
    String resourcePath

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    @Unroll
    def 'matches'() {
        when:
        String rootPath = resourcePath + 'test3'
        String rootRelativePath = File.separator
        String request = '''
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:h="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <h:SayHelloRequest>
                     <from>Test3</from>
                     <to>Simulator</to>
                     <greeting>hey</greeting>
                  </h:SayHelloRequest>
               </soapenv:Body>
            </soapenv:Envelope>
            '''
        String contentType = 'xml'
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        def xml = new XmlSlurper().parseText(simulatorResponse.getResponse())
        def expectedXml = new XmlSlurper().parseText('''<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://www.github.com/bjuvensjo/rsimulator/SayHello/">
               <soapenv:Header/>
               <soapenv:Body>
                  <hel:SayHelloResponse>
                     <from>Simulator</from>
                     <to>Test3</to>
                     <greeting>good bye</greeting>
                  </hel:SayHelloResponse>
               </soapenv:Body>
            </soapenv:Envelope>''')
        then:
        xml == expectedXml
    }
}
