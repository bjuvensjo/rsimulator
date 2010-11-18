package org.rsimulator.core.controller.handler.regexp;

import static org.rsimulator.core.config.Constants.REQUEST;
import static org.rsimulator.core.config.Constants.RESPONSE;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rsimulator.core.controller.ControllerResponse;
import org.rsimulator.core.controller.ControllerResponseImpl;
import org.rsimulator.core.controller.Handler;
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

    /*
     * (non-Javadoc)
     *
     * @see org.rsimulator.controller.handler.regexp.Handler#findMatch(java.lang.String, java.lang.String)
     */
    @Override
    public ControllerResponse findMatch(String rootPath, String rootRelativePath, String request) throws IOException {
        ControllerResponse result = null;
        String formatedRequest = format(request);
        String path = new StringBuilder().append(rootPath).append(rootRelativePath).toString();
        for (File candidateFile : fileUtils.findRequests(new File(path), getExtension())) {
            String candidate = fileUtils.read(candidateFile);
            Matcher matcher = getMatcher(formatedRequest, candidate);
            if (matcher.matches()) {
                return new ControllerResponseImpl(getResponse(candidateFile, matcher), getProperties(candidateFile),
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

    private Matcher getMatcher(String formatedRequest, String candidate) throws IOException {
        String formatedCandidate = format(candidate);
        Pattern p = Pattern.compile(formatedCandidate);
        return p.matcher(formatedRequest);
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
