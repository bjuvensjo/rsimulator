package com.github.bjuvensjo.rsimulator.core.handler.regexp;

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
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST;
import static com.github.bjuvensjo.rsimulator.core.config.Constants.RESPONSE;

/**
 * AbstractHandler implements what is common for regular expression handlers.
 *
 * @see Handler
 */
public abstract class AbstractHandler implements Handler {
    private static final String PROPERTIES_PATTERN = REQUEST + ".*";
    private final Logger log = LoggerFactory.getLogger(AbstractHandler.class);
    @Inject
    protected FileUtils fileUtils;
    @Inject
    protected Props props;

    public Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
        String path = rootPath.concat(rootRelativePath);
        log.debug("path: {}", path);
        Optional<SimulatorResponse> result = fileUtils.findRequests(Paths.get(path), getExtension())
                .stream()
                .map(candidatePath -> Optional.of(fileUtils.read(candidatePath))
                        .map(candidateRequest -> getMatcher(request, candidateRequest))
                        .filter(Matcher::matches)
                        .map(matcher -> getResponse(candidatePath, matcher))
                        .map(response -> (SimulatorResponse) new SimulatorResponseImpl(response, getProperties(candidatePath), candidatePath)))
                .flatMap(Optional::stream)
                .findFirst();

        log.debug("result: {}", result);
        return result;
    }

    /**
     * Returns the supported file extension.
     *
     * @return the supported file extension
     */
    protected abstract String getExtension();

    /**
     * Returns the specified request formatted for matching.
     *
     * @param request the request
     * @return the specified request formatted for matching
     */
    protected abstract String format(String request);

    /**
     * Returns the specified request escaped for matching.
     *
     * @param request     the request
     * @param isCandidate the isCandidate
     * @return the specified request escaped for matching
     */
    protected abstract String escape(String request, boolean isCandidate);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    Matcher getMatcher(String request, String candidate) {
        return Optional.of(candidate)
                .map(this::format)
                .map(formattedCandidate -> escape(formattedCandidate, true))
                .map(Pattern::compile)
                .map(p -> p.matcher(escape(format(request), false)))
                .get();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    String getResponse(Path candidatePath, Matcher matcher) {
        String result = Optional.of(candidatePath)
                .map(Path::getFileName)
                .map(Path::toString)
                .map(s -> s.replaceFirst(REQUEST, RESPONSE))
                .map(candidatePath::resolveSibling)
                .map(responsePath -> fileUtils.read(responsePath))
                .map(response -> {
                    for (int j = 1; j <= matcher.groupCount(); j++) {
                        response = response.replaceAll("[$]+[{]+" + j + "[}]+", matcher.group(j));
                    }
                    return response;
                })
                .get();
        log.debug("Response: [{}]", result);
        return result;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    Optional<Properties> getProperties(Path candidatePath) {
        return Optional.of(candidatePath)
                .map(Path::getFileName)
                .map(Path::toString)
                .map(s -> s.replaceFirst(PROPERTIES_PATTERN, ".properties"))
                .map(candidatePath::resolveSibling)
                .map(props::getProperties)
                .get();
    }
}
