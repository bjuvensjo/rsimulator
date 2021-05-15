package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.util.Props
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import org.aopalliance.intercept.MethodInvocation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SimulatorCacheInterceptorTest extends Specification {
    @Shared
    SimulatorCacheInterceptor spec

    def 'setupSpec'() {
        spec = new SimulatorCacheInterceptor();
    }

    @Unroll
    def 'invoke with cache on #isCached #expected'(boolean isCached, int expected) {
        String response = 'response'
        spec.cache = CacheManager.create().getCache('SimulatorCache')
        String key = 'rootPath' + 'rootRelativePath' + 'request' + 'contentType'
        if (isCached) {
            spec.cache.put(new Element(key, response))
        } else {
            spec.cache.remove(key)
        }
        spec.props = Mock(Props) {
            isSimulatorCache() >> true
        }
        MethodInvocation methodInvocation = Mock() {
            getMethod() >> SimulatorImpl.class.getMethod('service', String.class, String.class, String.class, String.class, Map[])
            getArguments() >> ['rootPath', 'rootRelativePath', 'request', 'contentType', ['var1', 'var2']]
        }
        when:
        Object o = spec.invoke(methodInvocation)
        then:
        o == response
        expected * methodInvocation.proceed() >> response
        where:
        isCached | expected
        false    | 1
        true     | 0
    }

    def 'invoke with cache off'() {
        String response = 'response'
        spec.props = Mock(Props) {
            isSimulatorCache() >> false
        }
        MethodInvocation methodInvocation = Mock() {
            getMethod() >> SimulatorImpl.class.getMethod('service', String.class, String.class, String.class, String.class, Map[])
        }
        when:
        Object o = spec.invoke(methodInvocation)
        then:
        o == response
        1 * methodInvocation.proceed() >> response
    }
}
