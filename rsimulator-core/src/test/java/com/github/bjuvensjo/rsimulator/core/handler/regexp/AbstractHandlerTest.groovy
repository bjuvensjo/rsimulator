package com.github.bjuvensjo.rsimulator.core.handler.regexp

import com.github.bjuvensjo.rsimulator.core.util.FileUtils
import com.github.bjuvensjo.rsimulator.core.util.Props
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

class AbstractHandlerTest extends Specification {
    @Shared
    AbstractHandler abstractHandler

    def 'setupSpec'() {
        abstractHandler = new AbstractHandler() {
            @Override
            protected String getExtension() {
                return 'txt'
            }

            @Override
            protected String format(String request) {
                return request
            }

            @Override
            protected String escape(String request, boolean isCandidate) {
                return request
            }
        }

    }


    def 'get response'() {
        Path candidatePath = Mock() {
            getFileName() >> {
                Paths.get('ARequest.txt')
            }
            resolveSibling(_) >> {
                Paths.get('AResponse.txt')
            }
        }
        FileUtils fileUtils = Mock() {
            read(_) >> {
                'response ${1} ${2}!'
            }
        }
        abstractHandler.fileUtils = fileUtils
        Matcher matcher = abstractHandler.getMatcher('request Hello World', 'request (.*) (.*)')
        matcher.matches()
        when:
        String response = abstractHandler.getResponse(candidatePath, matcher)
        then:
        response == 'response Hello World!'
    }

    @Unroll
    def 'get #path properties'(String path, boolean expected) {
        Props props = Mock() {
            getProperties(_) >> {
                expected ? Optional.of(new Properties()) : Optional.empty()
            }
        }
        abstractHandler.props = props

        when:
        def present = abstractHandler.getProperties(Paths.get(path)).isPresent()
        then:
        present == expected
        where:
        path           | expected
        'existing'     | true
        'non existing' | false
    }
}
