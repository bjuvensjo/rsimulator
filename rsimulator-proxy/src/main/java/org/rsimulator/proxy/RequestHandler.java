package org.rsimulator.proxy;

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
    private static final int BUFFER_SIZE = 100000;

    public void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            connection.setRequestProperty(headerName, headerValue);
        }
    }

    public void copyRequest(HttpServletRequest request, HttpURLConnection con) throws IOException {
        String method = request.getMethod();
        log.debug("method: {}", method);
        if ("GET".equals(method) || "DELETE".equals(method)) {
            return;
        }
        InputStream is = request.getInputStream();
        OutputStream os = con.getOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buffer)) > 0) {
            os.write(buffer, 0, n);
        }
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
