package com.github.bjuvensjo.rsimulator.core.util

import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import org.aopalliance.intercept.MethodInvocation
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.attribute.FileTime
import java.util.concurrent.TimeUnit

class FileCacheSupportTest extends Specification {
    FileCacheSupport spec

    def 'setup'() {
        spec = Spy(FileCacheSupport)
    }

    @Unroll
    def 'invoke with #isCached #lastModified #expected'(boolean isCached, int lastModified, int modified, int expected) {
        Path path = Mock() {
            toAbsolutePath() >> Mock(Path)
        }
        MethodInvocation methodInvocation = Mock() {
            getArguments() >> [path]
            getMethod() >> FileUtilsImpl.class.getMethod('read', Path.class)
        }
        spec.getLastModifiedTime(path) >> FileTime.from(modified, TimeUnit.DAYS)
        String response = 'response'
        Cache cache = CacheManager.create().getCache('FileUtilsCache')
        if (isCached) {
            cache.put(new Element(path.toAbsolutePath(), [FileTime.from(lastModified, TimeUnit.DAYS), response] as Object[]))
        } else {
            cache.remove(path.toAbsolutePath())
        }
        when:
        Object o = spec.invoke(methodInvocation, cache)
        then:
        o == response
        expected * methodInvocation.proceed() >> response
        where:
        isCached | lastModified | modified | expected
        false    | 0            | 0        | 1
        true     | 0            | 0        | 0
        true     | 0            | 1        | 1
        true     | 1            | 1        | 0
    }

}
