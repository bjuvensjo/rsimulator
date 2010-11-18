package org.rsimulator.http.view;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rsimulator.core.config.DIModule;
import org.rsimulator.core.controller.Controller;
import org.rsimulator.core.controller.ControllerResponse;
import org.rsimulator.http.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * HttpMock makes the {@link Controller} functionality available over http.
 * 
 * @author Magnus Bjuvensjö
 */
public class HttpMock extends javax.servlet.http.HttpServlet {
    private static final long serialVersionUID = -3272134329745138875L;
    private static final String DEFAULT_ROOT_PATH = "src/main/resources";
    private static final int BUFFER_SIZE = 1000;
    private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([0-9A-Z-]+)");
    private static Logger log = LoggerFactory.getLogger(HttpMock.class);
    private static Controller controller;
    private static String rootPath;
    private static boolean useRootRelativePath;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Injector injector = Guice.createInjector(new DIModule());
        controller = injector.getInstance(Controller.class);
        rootPath = System.getProperty(Constants.ROOT_PATH) != null ? System.getProperty(Constants.ROOT_PATH)
                : DEFAULT_ROOT_PATH;
        useRootRelativePath = System.getProperty(Constants.USE_ROOT_RELATIVE_PATH) != null ? "true".equals(System
                .getProperty(Constants.USE_ROOT_RELATIVE_PATH)) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String queryString = request.getQueryString();
        if (queryString == null || "".equals(queryString)) {
            response.getWriter().write("Welcome to the Simulator!");
        } else {
            String theRootPath = request.getParameter(Constants.ROOT_PATH);
            String theUseRootRelativePath = request.getParameter(Constants.USE_ROOT_RELATIVE_PATH);
            if (theRootPath != null || theUseRootRelativePath != null) {
                if (theRootPath != null) {
                    HttpMock.rootPath = theRootPath;
                }
                if (theUseRootRelativePath != null) {
                    HttpMock.useRootRelativePath = "true".equals(theUseRootRelativePath);
                }
                String message = new StringBuilder().append("[").append("rootPath: ").append(theRootPath).append(", ")
                        .append("useRootRelativePath: ").append(theUseRootRelativePath).append("]").toString();
                log.debug("message: {}", message);
                response.getWriter().write(message);
            } else {
                handle(request, response);
            }
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

    private void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // TODO Improve!
            String contentType = request.getContentType();
            log.debug("contentType: {}", contentType);
            String charsetName = getCharsetName(contentType);
            log.debug("charsetName: {}", charsetName);
            String method = request.getMethod();
            log.debug("method: {}", method);
            String requestBody = "GET".equals(method) ? request.getParameter("request") : readBody(
                    new BufferedInputStream(request.getInputStream()), charsetName);
            log.debug("requestBody: {}", requestBody);
            String requestURI = request.getRequestURI();
            log.debug("requestURI: {}", requestURI);
            String rootRelativePath = useRootRelativePath ? requestURI : "";
            log.debug("rootRelativePath: {}", rootRelativePath);

            ControllerResponse controllerResponse = controller.service(rootPath, rootRelativePath, requestBody,
                    getControllerContentType(contentType));

            String responseBody = controllerResponse.getResponse();
            log.debug("responseBody: {}", responseBody);
            response.setContentType(contentType);
            handleResponseProperties(response, controllerResponse);
            if (responseBody != null) {
                response.getOutputStream().write(responseBody.getBytes(charsetName));
            }
        } catch (Exception e) {
            log.error(null, e);
            response.getWriter().write(e.getMessage());
        }
    }

    private void handleResponseProperties(HttpServletResponse response, ControllerResponse controllerResponse) {
        Properties properties = controllerResponse.getProperties();
        log.debug("properties: {}", properties);
        if (properties != null) {
            String responseCode = properties.getProperty("responseCode");
            log.debug("responseCode: {}", responseCode);
            if (responseCode != null) {
                response.setStatus(Integer.parseInt(responseCode));
            }
        }
    }

    private String getControllerContentType(String contentType) {
        if (contentType != null
                && (contentType.indexOf("text/xml") > -1 || contentType.indexOf("application/xml") > -1 || contentType
                        .indexOf("application/soap+xml") > -1)) {
            return "xml";
        } else {
            return "txt";
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
