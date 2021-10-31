package com.github.bjuvensjo.rsimulator.core.handler.parser.xml;

import com.github.bjuvensjo.rsimulator.core.Handler;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl;
import com.github.bjuvensjo.rsimulator.core.util.FileUtils;
import com.github.bjuvensjo.rsimulator.core.util.Props;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST;
import static com.github.bjuvensjo.rsimulator.core.config.Constants.RESPONSE;

public class XmlParseHandler implements Handler {
    @Inject
    private FileUtils fileUtils;
    @Inject
    private Props props;

    private static final String PROPERTIES_PATTERN = REQUEST + ".*";
    private static final String EXTENSION = "xml";

    private final Logger log = LoggerFactory.getLogger(XmlParseHandler.class);

    @Override
    public Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
        String path = rootPath.concat(rootRelativePath);
        log.debug("path: {}", path);

        List<Path> candidatePaths = fileUtils.findRequests(Paths.get(path), EXTENSION);

        List<Result> results = candidatePaths.stream()
                .map(candidatePath -> {
                    String candidateRequest = fileUtils.read(candidatePath);
                    MatcherResponse matcherResponse = Matcher.match(request, candidateRequest);
                    Result result = new Result();
                    result.candidatePath = candidatePath;
                    result.errorMessage = matcherResponse.errorMessage;
                    result.groups = matcherResponse.groups;
                    return result;
                })
                .collect(Collectors.toList());

        Optional<Result> match = results.stream()
                .filter(result -> result.groups != null)
                .findFirst();

        if (match.isPresent()) {
            log.info("Matching mock found: {}", match.get().candidatePath);
            return Optional.of(createSimulatorResponse(match.get()));
        }

        // When no match found
        log.info("Request paths considered: {}", candidatePaths);
        for (Result result : results) {
            log.error("[ERROR] Request path: {}, Reason: {}", result.candidatePath, result.errorMessage);
        }

        return Optional.empty();
    }

    private SimulatorResponse createSimulatorResponse(Result result) {
        List<String> groups = result.groups;
        String responseMock = getResponseMock(result.candidatePath);
        String response = Matcher.createResponse(responseMock, groups);
        return new SimulatorResponseImpl(
                response, getProperties(result.candidatePath), result.candidatePath);
    }

    private String getResponseMock(Path candidatePath) {
        String name = candidatePath.getFileName().toString().replaceFirst(REQUEST, RESPONSE);
        Path responsePath = candidatePath.resolveSibling(name);
        return fileUtils.read(responsePath);
    }

    private Optional<Properties> getProperties(Path candidatePath) {
        String name = candidatePath.getFileName().toString().replaceFirst(PROPERTIES_PATTERN, ".properties");
        Path propertiesPath = candidatePath.resolveSibling(name);
        Optional<Properties> properties = props.getProperties(propertiesPath);
        log.debug("Properties: [{}, {}]", properties, propertiesPath);
        return properties;
    }
}
