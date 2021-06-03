package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification

class TestNotFound extends Specification {
    @Shared
    Simulator simulator
    @ResourcePath(rootOnly = true)
    String resourcePath
    String rootRelativePath = File.separator
    String request = 'XXX'
    String contentType = 'txt'

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    def 'no matching request'() {
        when:
        String rootPath = resourcePath + 'test1'
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }

    def 'no candidate request'() {
        when:
        String rootPath = resourcePath + 'test3'
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }

    def 'non existing root path'() {
        when:
        String rootPath = resourcePath + 'test8/notFound'
        Optional<SimulatorResponse> simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType)
        then:
        simulatorResponse.isEmpty()
    }
}
