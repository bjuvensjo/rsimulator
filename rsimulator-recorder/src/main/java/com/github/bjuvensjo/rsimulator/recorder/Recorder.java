package com.github.bjuvensjo.rsimulator.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Record proxy traffic to file
 *
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
public class Recorder implements Filter {
    private static final Pattern ACCEPT_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([0-9A-Z-]+)");
    private static final String ENCODING = "UTF-8";
    private Logger log = LoggerFactory.getLogger(Recorder.class);
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
    public void init(FilterConfig filterConfig) {
        config = new Config();
        recorderScriptRunner = new RecorderScriptRunner(config);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (recorderIsOn()) {
            RecorderServletRequestWrapper recorderRequest = new RecorderServletRequestWrapper((HttpServletRequest) request);
            RecorderServletResponseWrapper recorderResponse = new RecorderServletResponseWrapper((HttpServletResponse) response);

            String basePath = config.get(Config.BASE_DIRECTORY);
            log.debug("basePath: {}", basePath);
            String relativeBasePath = getRelativeBasePath(recorderRequest);
            log.debug("relativeBasePath: {}", relativeBasePath);
            String recordDir = String.format("%s%s%s", basePath, File.separator, relativeBasePath);
            log.debug("recordDir: {}", recordDir);
            String filePrefix = getFilePrefix();
            log.debug("filePrefix {}", filePrefix);
            String contentType = recorderRequest.getContentType();
            log.debug("Content-Type: {}", contentType);
            String fileEnding = getFileEnding(contentType, recorderRequest.getHeader("Accept"));
            log.debug("fileEnding: {}", fileEnding);
            String encoding = getEncoding(contentType);
            log.debug("encoding: {}", encoding);
            String requestFilePath = getRequestFilePath(recordDir, filePrefix, fileEnding);
            log.debug("requestFilePath: {}", requestFilePath);
            String propertiesFilePath = getPropertiesFilePath(recordDir, filePrefix);
            log.debug("propertiesFilePath: {}", propertiesFilePath);
            String responseFilePath = getResponseFilePath(recordDir, filePrefix, fileEnding);
            log.debug("responseFilePath: {}", responseFilePath);
            String requestBody = getRequestBody(recorderRequest, encoding);
            log.debug("requestBody: {}", requestBody);

            log.debug("Creating recording directory: {}", recordDir);
            new File(recordDir).mkdirs();

            Map<String, Object> vars = new HashMap<>();
            vars.put(RecorderScriptVars.REQUEST, recorderRequest);
            vars.put(RecorderScriptVars.RESPONSE, recorderResponse);
            vars.put(RecorderScriptVars.REQUEST_BODY_TO_RECORD, requestBody);
            vars.put(RecorderScriptVars.RELATIVE_RECORD_PATH, relativeBasePath);
            vars.put(RecorderScriptVars.BASE_PATH, basePath);
            vars.put(RecorderScriptVars.FILE_PREFIX, filePrefix);

            log.debug("Running request scripts");
            log.debug("vars: {}", vars);
            recorderScriptRunner.runRequestScript(basePath, vars);
            log.debug("vars: {}", vars);

            requestBody = (String) vars.get(RecorderScriptVars.REQUEST_BODY_TO_RECORD);
            log.debug("Recording request: {}", requestFilePath);
            Files.write(Paths.get(requestFilePath), requestBody.getBytes(ENCODING));

            chain.doFilter(recorderRequest, recorderResponse);

            int statusCode = recorderResponse.getStatus();
            log.debug("statusCode: {}", statusCode);
            String responseBody = recorderResponse.getResponseBody(encoding);
            log.debug("responseBody: {}", responseBody);
            vars.put(RecorderScriptVars.RESPONSE_BODY_TO_RECORD, responseBody);

            log.debug("Recording response properties: {}", filePrefix);
            if (statusCode != 200) {
                SimulatorProperties simulatorProperties = new SimulatorProperties(propertiesFilePath);
                simulatorProperties.setProperty("responseCode", String.valueOf(statusCode));
                simulatorProperties.save(ENCODING);
            }

            log.debug("vars: {}", vars);
            log.debug("Running response script");
            recorderScriptRunner.runResponseScript(basePath, vars);
            log.debug("vars: {}", vars);

            log.debug("Recording response: {}", responseFilePath);
            responseBody = (String) vars.get(RecorderScriptVars.RESPONSE_BODY_TO_RECORD);
            log.debug("responseBody: {}", responseBody);
            Files.write(Paths.get(responseFilePath), responseBody.getBytes(ENCODING));

            response.getOutputStream().write(recorderResponse.getBytes());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private String getRequestFilePath(String recordDir, String filePrefix, String fileEnding) {
        return getFilePath(recordDir, filePrefix, Constants.REQUEST_FILENAME, fileEnding);
    }

    private String getResponseFilePath(String recordDir, String filePrefix, String fileEnding) {
        return getFilePath(recordDir, filePrefix, Constants.RESPONSE_FILENAME, fileEnding);
    }

    private String getPropertiesFilePath(String recordDir, String filePrefix) {
        return getFilePath(recordDir, filePrefix, "", "properties");
    }

    private String getRequestBody(RecorderServletRequestWrapper recorderRequest, String encoding) throws UnsupportedEncodingException {
        String requestBody = recorderRequest.getRequestBody(encoding);
        if (requestBody.length() > 0) {
            return requestBody;
        }
        String queryString = copyQueryString(recorderRequest);
        log.debug("queryString: {}", queryString);
        return queryString;
    }

    private String copyQueryString(HttpServletRequest request) {
        return request.getQueryString() != null ? request.getQueryString() : "";
    }

    private String getRelativeBasePath(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length() + 1);
    }

    private String getFilePrefix() {
        return (new SimpleDateFormat("yyyyMMddHHmmssSS")).format(new Date());
    }

    private String getFilePath(String recordDir, String filePrefix, String fileSuffix, String fileEnding) {
        return recordDir + File.separator + filePrefix + fileSuffix + "." + fileEnding;
    }

    private String getFileEnding(String contentType, String accept) {
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
        return config.isOn();
    }
}
