package com.github.bjuvensjo.rsimulator.core

import org.aopalliance.intercept.MethodInvocation
import spock.lang.Specification
import spock.lang.Unroll

class SimulatorPropertiesInterceptorTest extends Specification {
    SimulatorPropertiesInterceptor spec

    def 'setup'() {
        spec = Spy(SimulatorPropertiesInterceptor)
    }


    @Unroll
    def 'invoke #response #delay'(String response, String delay, int n) {
        when:
        MethodInvocation invocation = Mock() {
            proceed() >> {
                if (response) {
                    SimulatorResponse simulatorResponse = Mock() {
                        getProperties() >> {
                            delay ? Optional.of(Mock(Properties)) : Optional.empty()
                        }
                    }
                    return Optional.of(simulatorResponse)
                }
                Optional.empty()
            }
        }
        spec.invoke(invocation)
        then:
        n * spec.handleDelay(_) >> {}
        where:
        response   | delay | n
        null       | null  | 0
        'response' | null  | 0
        null       | 1     | 0
        'response' | 1     | 1
    }

    @Unroll
    def 'handle delay #delay'(String delay, int n) {
        when:
        Properties props = Mock() {
            getProperty(SimulatorPropertiesInterceptor.DELAY) >> {
                delay
            }
        }
        spec.handleDelay(props)
        then:
        n * spec.doDelay(delay) >> {}
        where:
        delay | n
        null  | 0
        '1'   | 1
    }
}
