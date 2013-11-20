package org.rsimulator.recorder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Record proxy traffic to file
 *
 * @author Anders Bälter
 */
public class Recorder implements Filter {

    private Logger log = LoggerFactory.getLogger(Recorder.class);

    private static final Pattern ACCEPT_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([0-9A-Z-]+)");
    private static final String ENCODING = "UTF-8";

    private Map<String, String> contentTypes = new HashMap<String, String>();
    private Map<String, String> accepts = new HashMap<String, String>();
    private Config config;
    private RecorderScriptRunner recorderScriptRunner;

    {
        contentTypes.put("application/json", "json");
        contentTypes.put("application/xml", "xml");
        contentTypes.put("application/soap+xml", "xml");
        contentTypes.put("text/xml", "xml");
        contentTypes.put("default", "txt");

        accepts.put("application/json", "json");
        accepts.put("default", "txt");
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = new Config();
        recorderScriptRunner = new RecorderScriptRunner(config);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (recorderIsOn()) {
            log.debug("Recording request");
            RecorderServletRequestWrapper recorderRequest = new RecorderServletRequestWrapper(httpServletRequest);
            RecorderServletResponseWrapper recorderResponse = new RecorderServletResponseWrapper(httpServletResponse);
            String contentType = recorderRequest.getContentType();
            String encoding = getEncoding(contentType);
            recorderScriptRunner.runRequestScript(recorderRequest, recorderResponse, encoding);

            String path = buildPath(recorderRequest);
            log.debug("Creating directory: {}", path);
            (new File(path)).mkdirs();
            String fileType = fileType(contentType, recorderRequest.getHeader("Accept"));
            log.debug("Content-Type: {}", contentType);
            String filePrefix = (String)recorderRequest.getAttribute(Constants.FILE_PREFIX);
            String fileURI = buildFileURI(path, filePrefix, Constants.REQUEST_FILENAME, fileType);
            log.debug("Writing request file: {}", fileURI);
            String requestData = (String)recorderRequest.getAttribute(Constants.REQUEST_BODY_TO_RECORD);
            FileUtils.writeStringToFile(new File(fileURI), requestData, ENCODING);

            chain.doFilter(recorderRequest, recorderResponse);

            log.debug("Recording response");
            recorderScriptRunner.runResponseScript(recorderRequest, recorderResponse, encoding);
            fileURI = buildFileURI(path, filePrefix, Constants.RESPONSE_FILENAME, fileType);
            String responseData = (String)recorderRequest.getAttribute(Constants.RESPONSE_BODY_TO_RECORD);
            log.debug("Writing response file: {}", fileURI);
            FileUtils.writeStringToFile(new File(fileURI), responseData, ENCODING);
            response.getOutputStream().write(recorderResponse.getBytes());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    private String buildFileURI(String path, String filePrefix, String fileSuffix, String fileType) {
        return new StringBuilder(path).append(File.separator).append(filePrefix).append(fileSuffix)
                .append(".").append(fileType).toString();
    }

    private String fileType(String contentType, String accept) {
        String result = null;
        if (contentType != null) {
            Matcher m = CONTENT_TYPE_PATTERN.matcher(contentType);
            if (m.find()) {
                result = contentTypes.get(m.group(1));
            }
        }
        if (result == null) {
            if (accept != null) {
                Matcher m = ACCEPT_PATTERN.matcher(accept);
                if (m.find()) {
                    String[] split = m.group(1).split(" *, *");
                    for (int i = 0; result == null && i < split.length; i++) {
                        result = accepts.get(split[i]);
                    }
                }
            }
        }
        if (result == null) {
            result = contentTypes.get("default");
        }
        return result;
    }

    private String getEncoding(String contentType) {
        String result = ENCODING;
        if (contentType != null) {
            Matcher m = CHARSET_PATTERN.matcher(contentType);
            if (m.find()) {
                result = m.group(1);
            }
        }
        return result;
    }

    private boolean recorderIsOn() {
        return config.getBoolean(Config.RECORDER_IS_ON);
    }

    private String buildPath(HttpServletRequest request) {
        String requestedUriWithoutContext = (String)request.getAttribute(Constants.RELATIVE_RECORD_PATH);
        String basePath = (String)request.getAttribute(Constants.BASE_PATH);
        return new StringBuilder(basePath).append(File.separator).append(requestedUriWithoutContext).toString();
    }

}
