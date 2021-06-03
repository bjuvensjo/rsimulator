package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification


class JsonTest extends Specification {
    @Shared
    Simulator simulator
    @ResourcePath(rootOnly = true)
    String resourcePath

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    def 'body'() {
        when:
        String rootPath = resourcePath + 'test7'
        String rootRelativePath = File.separator
        String request = '''[
                  {
                    "id": "12345678901",
                    "name": "Privatkonto",
                    "balance": 34251.15766987801
                  },
                  {
                    "id": "23456789012",
                    "name": "Lanekonto",
                    "balance": 874.8714758855125
                  },
                  {
                    "id": "34567890123",
                    "name": "Rakningskonto",
                    "balance": 18935.745973888184
                  },
                  {
                    "id": "45678901234",
                    "name": "Semesterkonto",
                    "balance": 26763.710168201727
                  },
                  {
                    "id": "56789012345",
                    "name": "Sparkonto",
                    "balance": 281434.8179749503
                  }
                ]'''
        String contentType = 'json'
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        then:
        simulatorResponse != null
        '{"result":"ok"}' == simulatorResponse.getResponse()
    }

    def 'query'() {
        when:
        String rootPath = resourcePath + 'test8'
        String rootRelativePath = File.separator
        String request = "query=1234&another='dododo'"
        String contentType = 'json'
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        then:
        simulatorResponse != null
        '{"result":"ok"}' == simulatorResponse.getResponse()
    }

    def 'empty request'() {
        when:
        String rootPath = resourcePath + 'test9'
        String rootRelativePath = File.separator
        String request = ""
        String contentType = 'json'
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        then:
        simulatorResponse != null
        '{"result":"ok"}' == simulatorResponse.getResponse()
    }
}
