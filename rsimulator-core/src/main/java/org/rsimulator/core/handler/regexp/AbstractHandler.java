package org.rsimulator.core.handler.regexp;

import static org.rsimulator.core.config.Constants.REQUEST;
import static org.rsimulator.core.config.Constants.RESPONSE;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rsimulator.core.Handler;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.core.SimulatorResponseImpl;
import org.rsimulator.core.util.FileUtils;
import org.rsimulator.core.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public SimulatorResponse findMatch(String rootPath, String rootRelativePath, String request) throws IOException {
        SimulatorResponse result = null;
        String path = new StringBuilder().append(rootPath).append(rootRelativePath).toString();
        log.debug("path: {}", path);
        for (File candidateFile : fileUtils.findRequests(new File(path), getExtension())) {
            String candidate = fileUtils.read(candidateFile);
            Matcher matcher = getMatcher(request, candidate);
            if (matcher.matches()) {
                return new SimulatorResponseImpl(getResponse(candidateFile, matcher), getProperties(candidateFile),
                        candidateFile);
            }
        }
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
     * @param request the request
     * @param isCandidate the isCandidate
     * @return the specified request escaped for matching
     */
    protected abstract String escape(String request, boolean isCandidate);
    
    private Matcher getMatcher(String request, String candidate) throws IOException {
        String formatedRequest = format(request);
        String formatedCandidate = format(candidate);
        String escapedRequest = escape(formatedRequest, false);
        String escapedCandidate = escape(formatedCandidate, true);
        Pattern p = Pattern.compile(escapedCandidate);
        return p.matcher(escapedRequest);
    }

    private String getResponse(File candidateFile, Matcher matcher) throws IOException {
        String responsePath = candidateFile.getPath().replaceAll(REQUEST, RESPONSE);
        String response = fileUtils.read(new File(responsePath));
        for (int j = 1; j <= matcher.groupCount(); j++) {
            response = response.replaceAll("[$]+[{]+" + j + "[}]+", matcher.group(j));
        }
        log.debug("Response: [{}, {}]", response, responsePath);
        return response;
    }

    private Properties getProperties(File candidateFile) {
        String propertiesPath = candidateFile.getPath().replaceAll(PROPERTIES_PATTERN, ".properties");
        Properties properties = props.getProperties(new File(propertiesPath));
        log.debug("Properties: [{}, {}]", properties, propertiesPath);
        return properties;
    }
}
