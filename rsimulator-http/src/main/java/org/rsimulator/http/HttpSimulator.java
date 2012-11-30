package org.rsimulator.http;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import org.rsimulator.core.Simulator;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.core.config.CoreModule;
import org.rsimulator.http.config.Constants;
import org.rsimulator.http.config.GlobalConfig;
import org.rsimulator.http.config.HttpModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpSimulator makes the {@link Simulator} functionality available over http.
 *
 * @author Magnus Bjuvensjö
 */
public class HttpSimulator extends javax.servlet.http.HttpServlet {
    private static final long serialVersionUID = -3272134329745138875L;
    private static final int BUFFER_SIZE = 1000;
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([0-9A-Z-]+)");
    private static final Pattern ACCEPT_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static Logger log = LoggerFactory.getLogger(HttpSimulator.class);
    @Inject
    private Simulator simulator;
    @Inject
    @Named("contentTypes")
    private Map<String, String> contentTypes;
    @Inject
    @Named("accepts")
    private Map<String, String> accepts;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Injector injector = Guice.createInjector(new CoreModule(), new HttpModule());
        injector.injectMembers(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String theRootPath = request.getParameter(Constants.ROOT_PATH);
        String theUseRootRelativePath = request.getParameter(Constants.USE_ROOT_RELATIVE_PATH);
        if (theRootPath != null || theUseRootRelativePath != null) {
            if (theRootPath != null) {
                GlobalConfig.rootPath = theRootPath;
            }
            if (theUseRootRelativePath != null) {
                GlobalConfig.useRootRelativePath = "true".equals(theUseRootRelativePath);
            }
            String message = new StringBuilder().append("[").append("rootPath: ").append(theRootPath).append(", ")
                    .append("useRootRelativePath: ").append(theUseRootRelativePath).append("]").toString();
            log.debug("message: {}", message);
            response.getWriter().write(message);
        } else {
            handle(request, response);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        handle(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handle(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handle(request, response);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String rootPath = getRequestRootPath(request);
            boolean  useRootRelativePath = getRequestUseRootRelativePath(request);
            String contentType = request.getContentType();
            String accept = request.getHeader("Accept");
            String charsetName = getCharsetName(contentType);
            String simulatorRequest = getSimulatorRequest(request, charsetName);
            String requestURI = request.getRequestURI();
            String rootRelativePath = useRootRelativePath ? requestURI : "";

            log.debug("contentType: {}", contentType);
            log.debug("accept: {}", accept);
            log.debug("charsetName: {}", charsetName);
            log.debug("method: {}", request.getMethod());
            log.debug("requestBody: {}", simulatorRequest);
            log.debug("requestURI: {}", requestURI);
            log.debug("rootRelativePath: {}", rootRelativePath);
            log.debug("rootPath: {}", rootPath);

            SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, simulatorRequest,
                    getSimulatorContentType(contentType, accept));

            String responseBody = simulatorResponse != null ? simulatorResponse.getResponse()
                    : "No simulatorResponse found!";
            request.setAttribute(Constants.SIMULATOR_RESPONSE, simulatorResponse);

            log.debug("responseBody: {}", responseBody);
            response.setContentType(contentType == null ? accept : contentType);
            response.setDateHeader("Date", System.currentTimeMillis());
            handleResponseProperties(response, simulatorResponse);
            response.getOutputStream().write(responseBody.getBytes(charsetName));
        } catch (Exception e) {
            log.error(null, e);
            response.getWriter().write(e.getMessage());
        }
    }

    private String getRequestRootPath(HttpServletRequest request) {
        Object rootPath = request.getAttribute(Constants.ROOT_PATH);
        if (rootPath instanceof String && !StringUtils.isBlank((String)rootPath)) {
            return (String)rootPath;
        }
        return GlobalConfig.rootPath;
    }

    private boolean getRequestUseRootRelativePath(HttpServletRequest request) {
        Object useRootRelativePath = request.getAttribute(Constants.USE_ROOT_RELATIVE_PATH);
        if (useRootRelativePath != null && useRootRelativePath instanceof Boolean) {
            return  (Boolean)useRootRelativePath;
        }
        return GlobalConfig.useRootRelativePath;
    }

    private String getSimulatorRequest(HttpServletRequest request, String charsetName)
            throws IOException {
        if (request.getContentLength() > 0) {
            return readBody(new BufferedInputStream(request.getInputStream()), charsetName);
        }
        return copyQueryString(request);
    }

    private void handleResponseProperties(HttpServletResponse response, SimulatorResponse simulatorResponse) {
        if (simulatorResponse != null) {
            Properties properties = simulatorResponse.getProperties();
            log.debug("properties: {}", properties);
            if (properties != null) {
                String responseCode = properties.getProperty("responseCode");
                log.debug("responseCode: {}", responseCode);
                if (responseCode != null) {
                    response.setStatus(Integer.parseInt(responseCode));
                }
            }
        }
    }

    private String readBody(BufferedInputStream bis, String charsetName) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = -1;
        while ((n = bis.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, n, charsetName));
        }
        return sb.toString();
    }

    private String getSimulatorContentType(String contentType, String accept) {
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

    private String copyQueryString(HttpServletRequest request) {
        return request.getQueryString() == null ? "" : request.getQueryString();
    }

    private String getCharsetName(String contentType) {
        String result = "UTF-8";
        if (contentType != null) {
            Matcher m = CHARSET_PATTERN.matcher(contentType);
            if (m.find()) {
                result = m.group(1);
            }
        }
        return result;
    }
}
