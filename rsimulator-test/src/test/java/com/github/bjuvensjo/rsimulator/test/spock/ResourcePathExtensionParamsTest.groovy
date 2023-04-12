package com.github.bjuvensjo.rsimulator.test.spock

import spock.lang.Specification

class ResourcePathExtensionParamsTest extends Specification {
    @ResourcePath(fixtureDelimiter = '[#]+', simpleClassName = false)
    String resourcePath

    def '10 # resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/com/github/bjuvensjo/rsimulator/test/spock/ResourcePathExtensionParamsTest/10/'
    }

    def '20 # resource path should be simple and delimited by -'() {
        expect:
        resourcePath ==~ '.*/rsimulator/rsimulator-test/target/test-classes/com/github/bjuvensjo/rsimulator/test/spock/ResourcePathExtensionParamsTest/20/'
    }
}
