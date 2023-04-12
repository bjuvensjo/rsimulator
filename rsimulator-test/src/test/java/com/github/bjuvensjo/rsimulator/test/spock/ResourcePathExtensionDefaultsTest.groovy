package com.github.bjuvensjo.rsimulator.test.spock

import spock.lang.Specification

class ResourcePathExtensionDefaultsTest extends Specification {
    @ResourcePath
    String resourcePath

    def '10 - resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/ResourcePathExtensionDefaultsTest/10/'
    }

    def '20 - resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/ResourcePathExtensionDefaultsTest/20/'
    }
}
