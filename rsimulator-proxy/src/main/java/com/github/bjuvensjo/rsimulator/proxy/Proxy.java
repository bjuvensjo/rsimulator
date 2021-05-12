package com.github.bjuvensjo.rsimulator.proxy;

import com.github.bjuvensjo.rsimulator.proxy.config.ProxyModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Proxy servlet proxies as {@link URIMapper} is configured.
 */
@WebServlet(urlPatterns = "/*")
public class Proxy extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(Proxy.class);
    private static final int READ_TIMEOUT = 12000;
    @Inject
    private URIMapper uriMapper;
    @Inject
    private RequestHandler requestHandler;
    @Inject
    private ResponseHandler responseHandler;
    @Inject
    private SecurityHandler securityHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Injector injector = Guice.createInjector(new ProxyModule());
            injector.injectMembers(this);
            securityHandler.initialize();
        } catch (Exception e) {
            throw new ServletException("Can not initialize.", e);
        }
    }

    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            HttpURLConnection connection = getConnection(request.getMethod(), getMappedURL(request));
            requestHandler.handle(request, connection);
            responseHandler.handle(response, connection);
        } catch (Exception e) {
            log.error("Can not service.", e);
            throw new ServletException(e);
        }
    }

    private String getMappedURL(HttpServletRequest request) {
        String uriToMap = getURIToMap(request);
        String mappedURL = uriMapper.map(uriToMap);
        log.debug("uriToMap: {}", uriToMap);
        log.debug("mappedURL: {}", mappedURL);
        return mappedURL;
    }

    private String getURIToMap(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        log.debug("contextPath: {}", contextPath);
        log.debug("requestURI: {}", requestURI);
        log.debug("queryString: {}", queryString);

        StringBuilder uri = new StringBuilder(requestURI.substring(request.getContextPath().length() + 1));
        if (queryString != null) {
            uri.append("?").append(queryString);
        }

        return uri.toString();
    }

    private HttpURLConnection getConnection(String method, String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setReadTimeout(READ_TIMEOUT);
        return con;
    }
}