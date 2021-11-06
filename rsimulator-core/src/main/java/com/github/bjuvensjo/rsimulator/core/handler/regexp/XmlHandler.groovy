package com.github.bjuvensjo.rsimulator.core.handler.regexp

import com.github.bjuvensjo.rsimulator.core.Handler
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse
import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl
import com.github.bjuvensjo.rsimulator.core.config.Constants
import com.github.bjuvensjo.rsimulator.core.util.FileUtils
import com.github.bjuvensjo.rsimulator.core.util.Props
import com.google.inject.Inject
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovyjarjarantlr4.v4.runtime.misc.Tuple2

import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher

import static com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST
import static com.github.bjuvensjo.rsimulator.core.config.Constants.RESPONSE

/**
 * XmlHandler is a regular expression handler for xml (.xml).
 */
@CompileStatic
@Slf4j
class XmlHandler implements Handler {
    private static final String PROPERTIES_PATTERN = REQUEST + ".*";
    XmlMatcher xmlMatcher = new XmlMatcher()
    @Inject
    FileUtils fileUtils
    @Inject
    Props props

    @Override
    Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
        String path = rootPath.concat(rootRelativePath)
        log.debug('path: {}', path)

        List<Path> candidatePaths = fileUtils.findRequests(Paths.get(path), 'xml')
        Tuple2<Path, List<String>> result = candidatePaths.findResult { candidatePath ->
            String candidateRequest = fileUtils.read(candidatePath)
            XmlMatcher.Result result = xmlMatcher.match(request, candidateRequest, true, true)
            log.debug('result: {}', result)
            result.errorMessage ? null : new Tuple2<Path, List<String>>(candidatePath, result.groups)
        }

        Optional<SimulatorResponse> response
        if (result) {
            response = Optional.of((SimulatorResponse)new SimulatorResponseImpl(getResponse(result.item1, result.item2), getProperties(result.item1), result.item1))
            log.info('Match. Response: {}', response)
        } else {
            response = Optional.empty()
            log.info('No match. Considered: {}', candidatePaths)
        }
        
        return response
    }
    
    String getResponse(Path candidatePath, List<String> groups) {
        Path path = candidatePath.resolveSibling(candidatePath.fileName.toString().replaceFirst(REQUEST, RESPONSE))
        if (!path.toFile().exists()) {
            return null
        }
        String response = fileUtils.read(path)
        groups.eachWithIndex { item, index ->
            response = response.replaceAll('[$]+[{]+' + (index + 1) + '[}]+', item)
        }
        response
    }

    Properties getProperties(Path candidatePath) {
        Path path = candidatePath.resolveSibling(candidatePath.fileName.toString().replaceFirst(PROPERTIES_PATTERN, '.properties'))
        if (!path.toFile().exists()) {
            return null
        }
        props.getProperties(path).get()
    }
}
