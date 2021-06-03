package com.github.bjuvensjo.rsimulator.core.util

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.test.spock.ResourcePath
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path

class FileUtilsTest extends Specification {
    @Shared
    FileUtils fileUtils
    @ResourcePath(rootOnly = true)
    String resourcePath

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        fileUtils = injector.getInstance(FileUtils.class)
    }

    @Unroll
    def 'find #ending requests'(String ending, boolean expected) {
        when:
        String rootPath = resourcePath + 'test1' + File.separator + '..'
        List<Path> xmlRequests = fileUtils.findRequests(new File(rootPath).toPath(), ending)
        def correctEndings = xmlRequests.size() > 0 && xmlRequests.inject(true) { mem, p -> mem && p.toString().endsWith('Request.' + ending) }
        then:
        correctEndings == expected
        where:
        ending | expected
        'txt'  | true
        'json' | true
        'xml'  | true
    }
}
