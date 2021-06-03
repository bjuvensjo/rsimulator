package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.github.bjuvensjo.rsimulator.core.Handler;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl;
import com.github.bjuvensjo.rsimulator.core.util.FileUtils;
import com.github.bjuvensjo.rsimulator.core.util.FileUtilsImpl;
import com.github.bjuvensjo.rsimulator.core.util.Props;
import com.github.bjuvensjo.rsimulator.core.util.PropsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST;
import static com.github.bjuvensjo.rsimulator.core.config.Constants.RESPONSE;

public class FunctionalHandler implements Handler {
    private static final String PROPERTIES_PATTERN = REQUEST + ".*";
    private final Logger log = LoggerFactory.getLogger(FunctionalHandler.class);
    private final FileUtils fileUtils;
    private final Props props;
    private final String extension;
    private final UnaryOperator<String> format;
    private final BiFunction<String, Boolean, String> escape;

    public FunctionalHandler(FileUtils fileUtils, Props props, String extension, UnaryOperator<String> format, BiFunction<String, Boolean, String> escape) {
        this.fileUtils = fileUtils;
        this.props = props;
        this.extension = extension;
        this.format = format;
        this.escape = escape;
    }

    public Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
        String path = rootPath.concat(rootRelativePath);
        log.debug("path: {}", path);

        Optional<SimulatorResponse> result = fileUtils.findRequests(Paths.get(path), extension)
                .stream()
                .map(candidatePath -> {
                    SimulatorResponse simulatorResponse = null;
                    String candidateRequest = fileUtils.read(candidatePath);
                    Matcher matcher = getMatcher(request, candidateRequest);
                    if (matcher.matches()) {
                        String response = getResponse(candidatePath, matcher);
                        Properties properties = getProperties(candidatePath).orElse(null);
                        simulatorResponse = new SimulatorResponseImpl(response, properties, candidatePath);
                        simulatorResponse.setMatchingRequest(candidatePath);
                    }
                    return simulatorResponse;
                })
                .filter(Objects::nonNull)
                .findFirst();

        log.debug("result: {}", result);
        return result;
    }

    private Matcher getMatcher(String request, String candidate) {
        String formattedRequest = format.apply(request);
        String formattedCandidate = format.apply(candidate);
        String escapedRequest = escape.apply(formattedRequest, false);
        String escapedCandidate = escape.apply(formattedCandidate, true);
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

    public static void main(String[] args) {
        FileUtilsImpl fileUtils = new FileUtilsImpl();
        PropsImpl props = new PropsImpl(new Properties());

        FunctionalHandler txtHandler = new FunctionalHandler(fileUtils, props, "txt", Core.txtFormat, Core.txtEscape);
        Optional<SimulatorResponse> txtResponseOptional = txtHandler.findMatch("/Users/magnus/git/rsimulator/rsimulator-core/src/test/resources/test1", "", "Hello Simulator, says Test1!");
        System.out.println(txtResponseOptional.map(SimulatorResponse::getResponse).orElse("No response"));

        FunctionalHandler jsonHandler = new FunctionalHandler(fileUtils, props, "json", Core.jsonFormat, Core.jsonEscape);
        Optional<SimulatorResponse> jsonResponseOptional = jsonHandler.findMatch("/Users/magnus/git/rsimulator/rsimulator-core/src/test/resources/test9", "", "");
        System.out.println(jsonResponseOptional.map(SimulatorResponse::getResponse).orElse("No response"));
    }
}
