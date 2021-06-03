package com.github.bjuvensjo.rsimulator.aop

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.matcher.Matchers
import spock.lang.Specification

class GuiceAopAllianceSimulatorTest extends Specification {
    @Inject
    Foo foo

    def 'setup'() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Foo.class).to(DummyFoo.class)
                AopAllianceSimulator aopAllianceSimulator = new AopAllianceSimulatorImpl()
                aopAllianceSimulator.setRootPath(GuiceAopAllianceSimulatorTest.class)
                bindInterceptor(Matchers.subclassesOf(Foo.class), Matchers.any(), aopAllianceSimulator)
            }
        })
        injector.injectMembers(this)
    }

    def 'test'() {
        expect:
        foo.sayHello("Hello from ${getClass().name}") == "Hello ${getClass().name} from AopAllianceSimulator"
    }
}
