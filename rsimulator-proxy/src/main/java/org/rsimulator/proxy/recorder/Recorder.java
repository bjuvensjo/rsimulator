package org.rsimulator.proxy.recorder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
 */
public class Recorder implements Filter {

    private Logger log = LoggerFactory.getLogger(Recorder.class);
    private Config config = new Config();
    private FilterConfig filterConfig;

    private static final String REQUEST_FILENAME = "_Request";
    private static final String RESPONSE_FILENAME = "_Response";
    private static final Pattern ACCEPT_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([0-9A-Z-]+)");
    private static final String ENCODING = "UTF-8";

    Map<String, String> contentTypes = new HashMap<String, String>();
    Map<String, String> accepts = new HashMap<String, String>();

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
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (recorderIsOn()) {
            log.debug("Recording request");
            RecorderServletRequestWrapper recorderRequest = new RecorderServletRequestWrapper(httpServletRequest);
            RecorderServletResponseWrapper recorderResponse = new RecorderServletResponseWrapper(httpServletResponse);

            String path = buildPath(recorderRequest);
            log.debug("Creating directory: {}", path);
            new File(path).mkdirs();
            String contentType = recorderRequest.getContentType();
            String encoding = getEncoding(contentType);
            String fileType = fileType(recorderRequest);
            log.debug("Content-Type: {}", contentType);
            String filePrefix = buildFilePrefix();
            String fileWithPath = buildFilePath(path, filePrefix, REQUEST_FILENAME, fileType);
            log.debug("Writing request file: {}", fileWithPath);
            String requestData = requestData(recorderRequest, encoding);
            FileUtils.writeStringToFile(new File(fileWithPath), requestData);

            chain.doFilter(recorderRequest, recorderResponse);

            log.debug("Recording response");
            fileWithPath = buildFilePath(path, filePrefix, RESPONSE_FILENAME, fileType);
            String responseData = recorderResponse.getResponseAsString(encoding);
            log.debug("Writing response file: {}", fileWithPath);
            FileUtils.writeStringToFile(new File(fileWithPath), responseData);
            response.getOutputStream().write(recorderResponse.getBytes());
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

    private String buildPath(HttpServletRequest request) {
        String requestedUriWithoutContext = requestedUriWithoutContext(request);
        return new StringBuilder(basePath()).append(File.separator).append(requestedUriWithoutContext).toString();
    }

    private String buildFilePrefix() {
        return new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
    }

    private String buildFilePath(String path, String filePrefix, String fileSuffix, String fileType) {
        return new StringBuilder(path).append(File.separator).append(filePrefix).append(fileSuffix)
                .append(".").append(fileType).toString();
    }

    private String requestedUriWithoutContext(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length() + 1);
    }

    private String fileType(HttpServletRequest httpServletRequest) {
        String contentType = httpServletRequest.getContentType();
        String accept =  httpServletRequest.getHeader("Accept");
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

    private String requestData(RecorderServletRequestWrapper recorderRequest, String encoding) throws IOException {
        String method = recorderRequest.getMethod();
        if ("GET".equals(method)) {
            String requestParameter = recorderRequest.getParameter("request");
            return requestParameter != null ? requestParameter : "";
        }
        if ("DELETE".equals(method)) {
            return "";
        }
        return recorderRequest.getRequestAsString(encoding);
    }

    private String getEncoding(String contentType) {
        String result = "UTF-8";
        if (contentType != null) {
            Matcher m = CHARSET_PATTERN.matcher(contentType);
            if (m.find()) {
                result = m.group(1);
            }
        }
        return result;
    }

    private boolean recorderIsOn() {
        return config.getBoolean("recorder.record");
    }

    private String basePath() {
        String basePath = config.get("recorder.directory");
        return basePath != null ? basePath : "";
    }
}
