package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.bjuvensjo.rsimulator.core.TestUtil.getRootPath

class TxtTest extends Specification {
    @Shared
    Simulator simulator

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        simulator = injector.getInstance(Simulator.class)
    }

    @Unroll
    def 'matches'(String rootPath, String rootRelativePath, String request, String expected) {
        when:
        String contentType = 'txt'
        SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType).orElse(null)
        then:
        simulatorResponse != null
        expected == simulatorResponse.getResponse()
        where:
        rootPath                                      | rootRelativePath | request                        | expected
        getRootPath('/test1')                         | File.separator   | 'Hello Simulator, says Test1!' | 'Hello Test1, says Simulator!'
        getRootPath('/test1') + File.separator + '..' | File.separator   | 'Hello Simulator, says Test1!' | 'Hello Test1, says Simulator!'
        getRootPath('/test2')                         | File.separator   | 'Hello Simulator, says Test2!' | 'Hello Test2, says Simulator!'
        getRootPath('/test2') + File.separator + '..' | File.separator   | 'Hello Simulator, says Test2!' | 'Hello Test2, says Simulator!'
    }
}
