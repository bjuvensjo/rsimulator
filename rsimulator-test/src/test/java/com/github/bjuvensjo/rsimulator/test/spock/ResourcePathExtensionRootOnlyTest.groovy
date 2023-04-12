package com.github.bjuvensjo.rsimulator.test.spock

import spock.lang.Specification

class ResourcePathExtensionRootOnlyTest extends Specification {
    @ResourcePath(rootOnly = true)
    String resourcePath

    def '10 # resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/'
    }

    def '20 # resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/'
    }
}
