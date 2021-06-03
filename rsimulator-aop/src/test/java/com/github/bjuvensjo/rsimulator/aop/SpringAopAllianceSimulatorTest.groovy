package com.github.bjuvensjo.rsimulator.aop

import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

//@ContextConfiguration(classes = SlaskConfiguration)
@ContextConfiguration
class SpringAopAllianceSimulatorTest extends Specification {
    @Autowired
    AopAllianceSimulator aopAllianceSimulator

    @Autowired
    Foo foo

    @ResourcePath(rootOnly = true)
    String rootPath

    def 'testWithoutRootRelativePath'() {
        when:
        aopAllianceSimulator.setRootPath(this.getClass())
        aopAllianceSimulator.setUseRootRelativePath(false)
        String msg = foo.sayHello("Hello from ${getClass().getName()}")
        then:
        msg == "Hello ${getClass().name} from AopAllianceSimulator"
    }

    def 'testWithRootRelativePath'() {
        when:
        aopAllianceSimulator.setRootPath(rootPath)
        aopAllianceSimulator.setUseRootRelativePath(true)
        String msg = foo.sayHello("Hi from ${getClass().getName()}")
        then:
        msg == "Hello ${getClass().getName()}"
    }

    def 'testException'() {
        when:
        aopAllianceSimulator.setRootPath(this.getClass())
        aopAllianceSimulator.setUseRootRelativePath(false)
        BarException barException = null
        try {
            foo.doThrow('Give me an exception')
        } catch (BarException be) {
            barException = be
        }
        then:
        barException.code == '1'
        barException.message == 'msg'
    }

    def 'testDoNotReturn'() {
        when:
        aopAllianceSimulator.setRootPath(this.getClass())
        aopAllianceSimulator.setUseRootRelativePath(false)
        foo.doNotReturn('Do not return')
        then:
        noExceptionThrown()
    }
}
