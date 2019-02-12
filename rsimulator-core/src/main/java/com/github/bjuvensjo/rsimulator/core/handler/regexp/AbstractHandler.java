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
 * @author Magnus Bjuvensjö
 * @see Handler
 */
public abstract class AbstractHandler implements Handler {
    private static final String PROPERTIES_PATTERN = REQUEST + ".*";
    private Logger log = LoggerFactory.getLogger(AbstractHandler.class);
    @Inject
    private FileUtils fileUtils;
    @Inject
    private Props props;

    public Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
        String path = rootPath.concat(rootRelativePath);
        log.debug("path: {}", path);

        Optional<SimulatorResponse> result = fileUtils.findRequests(Paths.get(path), getExtension())
                .stream()
                .map(candidatePath -> {
                    SimulatorResponse simulatorResponse = null;
                    String candidateRequest = fileUtils.read(candidatePath);
                    Matcher matcher = getMatcher(request, candidateRequest);
                    if (matcher.matches()) {
                        String response = getResponse(candidatePath, matcher);
                        Optional<Properties> properties = getProperties(candidatePath);
                        simulatorResponse = new SimulatorResponseImpl(response, properties, candidatePath);
                    }
                    return Optional.ofNullable(simulatorResponse);
                })
                .filter(simulatorResponse -> simulatorResponse.isPresent())
                .findFirst()
                .get();

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

    private Matcher getMatcher(String request, String candidate) {
        String formattedRequest = format(request);
        String formattedCandidate = format(candidate);
        String escapedRequest = escape(formattedRequest, false);
        String escapedCandidate = escape(formattedCandidate, true);
        Pattern p = Pattern.compile(escapedCandidate);
        return p.matcher(escapedRequest);
    }

    private String getResponse(Path candidatePath, Matcher matcher) {
        String name = candidatePath.getFileName().toString().replaceFirst(REQUEST, RESPONSE);
        Path responsePath = candidatePath.resolveSibling(name);
        String response = fileUtils.read(responsePath);
        for (int j = 1; j <= matcher.groupCount(); j++) {
            response = response.replaceAll("[$]+[{]+" + j + "[}]+", matcher.group(j));
        }
        log.debug("Response: [{}, {}]", response, responsePath);
        return response;
    }

    private Optional<Properties> getProperties(Path candidatePath) {
        String name = candidatePath.getFileName().toString().replaceFirst(PROPERTIES_PATTERN, ".properties");
        Path propertiesPath = candidatePath.resolveSibling(name);
        Optional<Properties> properties = props.getProperties(propertiesPath);
        log.debug("Properties: [{}, {}]", properties, propertiesPath);
        return properties;
    }
}
