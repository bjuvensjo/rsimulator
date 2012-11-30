package org.rsimulator.proxy;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;

/**
 * @author Anders Bälter
 */
public class RequestHandler {
    private Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            connection.setRequestProperty(headerName, headerValue);
            log.debug("copied request header {} : {}", headerName, headerValue);
        }
    }

    public void copyRequest(HttpServletRequest request, HttpURLConnection con) throws IOException {
        if (request.getContentLength() <= 0) {
            return;
        }
        InputStream in = request.getInputStream();
        OutputStream out = con.getOutputStream();
        IOUtils.copy(in, out);
    }

    public String copyQueryString(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query != null) {
            query = new StringBuilder("?").append(query).toString();
        } else {
            query = "";
        }
        return query;
    }
}
