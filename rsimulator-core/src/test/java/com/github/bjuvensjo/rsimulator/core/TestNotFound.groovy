package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification

import static com.github.bjuvensjo.rsimulator.core.TestUtil.getRootPath

class TestNotFound extends Specification {
    @Shared
    Simulator simulator
    String rootRelativePath = File.separator
    String request = 'XXX'
    String contentType = 'txt'

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    def 'no matching request'() {
        when:
        String rootPath = getRootPath('/test1')
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }

    def 'no candidate request'() {
        when:
        String rootPath = getRootPath('/test3')
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }

    def 'non existing root path'() {
        when:
        String rootPath = getRootPath('/test3') + '/notFound'
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }
}
