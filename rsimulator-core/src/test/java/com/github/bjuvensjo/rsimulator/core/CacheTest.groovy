package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification

class CacheTest extends Specification {
    @Shared
    Simulator simulator
    @ResourcePath(rootOnly = true)
    String resourcePath

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    def 'find second in cache'() {
        String rootPath = resourcePath + 'test1'
        String rootRelativePath = File.separator
        String request = 'Hello Simulator, says Test1!'
        when:
        String contentType = 'txt'
        SimulatorResponse firstResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        SimulatorResponse secondResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)

        then:
        firstResponse.is(secondResponse)
    }
}
