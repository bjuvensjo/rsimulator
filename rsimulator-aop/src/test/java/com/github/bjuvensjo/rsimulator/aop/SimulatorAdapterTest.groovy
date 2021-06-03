package com.github.bjuvensjo.rsimulator.aop

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import spock.lang.Specification

class SimulatorAdapterTest extends Specification {
    @Inject
    SimulatorAdapter simulatorAdapter
    @ResourcePath(rootOnly = true)
    String rootPath

    def 'setup'() {
        Injector injector = Guice.createInjector(new CoreModule())
        injector.injectMembers(this)
    }

    def 'testService'() {
        when:
        String declaringClassCanonicalName = Foo.class.getCanonicalName()
        String methodName = 'sayHello'
        Object[] arguments = ["Hello from ${getClass().name}"] as String[]
        String msg = (String) simulatorAdapter.service(declaringClassCanonicalName, methodName, arguments, rootPath, true)
        then:
        "Hello ${getClass().name} from SimulatorAdapter" == msg
    }
}
