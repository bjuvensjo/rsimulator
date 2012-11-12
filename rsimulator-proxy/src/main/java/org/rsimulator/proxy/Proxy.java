package org.rsimulator.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Proxy servlet proxies as {@link URIMapper} is configured.
 *
 * @author Magnus Bjuvensjö
 */
public class Proxy extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Logger log = LoggerFactory.getLogger(Proxy.class);
    private static final int READ_TIMEOUT = 12000;
    private URIMapper uriMapper;
    private SecurityHandler securityHandler;
    private RequestHandler requestHandler;
    private ResponseHandler responseHandler;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            uriMapper = new URIMapper();
            securityHandler = new SecurityHandler();
            requestHandler = new RequestHandler();
            responseHandler = new ResponseHandler();
        } catch (Exception e) {
            log.error("Can not initialize.", e);
        }
    }

    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        try {
            String method = request.getMethod();

            String requestURI = new StringBuilder(request.getRequestURI())
                    .append(requestHandler.copyQueryString(request))
                    .toString();
            log.debug("requestURI: {}", requestURI);
            String requestURIWithoutContext = requestURI.substring(request.getContextPath().length() + 1);
            log.debug("requestURIWithoutContext: {}", requestURIWithoutContext);
            String mappedURL = uriMapper.map(requestURIWithoutContext);
            log.debug("mappedURL: {}", mappedURL);
            boolean isSecure = mappedURL.startsWith("https");
            log.debug("isSecure: {}", isSecure);
            if (isSecure) {
                securityHandler.installTrustManager();
            }
            HttpURLConnection con = getConnection(method, mappedURL);

            requestHandler.copyRequestHeaders(request, con);
            requestHandler.copyRequest(request, con);

            responseHandler.copyResponseHeaders(response, con);
            responseHandler.copyResponse(response, con);
        } catch (Exception e) {
            log.error("Can not service.", e);
            throw new ServletException(e);
        }
    }

    private HttpURLConnection getConnection(String method, String url) throws IOException {
        HttpURLConnection con = null;
        con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod(method);
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setReadTimeout(READ_TIMEOUT);
        return con;
    }
}