package com.github.bjuvensjo.rsimulator.core.util

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.google.inject.Guice
import com.google.inject.Injector
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path

import static com.github.bjuvensjo.rsimulator.core.TestUtil.getRootPath

class FileUtilsTest extends Specification {
    @Shared
    FileUtils fileUtils

    def 'setupSpec'() {
        Injector injector = Guice.createInjector(new CoreModule())
        fileUtils = injector.getInstance(FileUtils.class)
    }

    @Unroll
    def 'find #ending requests'(String ending, boolean expected) {
        when:
        String rootPath = getRootPath('/test1') + File.separator + '..'
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
