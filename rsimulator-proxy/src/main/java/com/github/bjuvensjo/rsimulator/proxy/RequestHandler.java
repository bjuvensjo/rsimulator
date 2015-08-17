package com.github.bjuvensjo.rsimulator.proxy;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;

/**
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
@Singleton
public class RequestHandler {
    private Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public void handle(HttpServletRequest request, HttpURLConnection connection) throws IOException {
        copyRequestHeaders(request, connection);
        copyRequest(request, connection);
    }
    
    private void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {
        @SuppressWarnings("unchecked")
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            connection.setRequestProperty(headerName, headerValue);
            log.debug("copied request header {} : {}", headerName, headerValue);
        }
    }

    private void copyRequest(HttpServletRequest request, HttpURLConnection connection) throws IOException {
        if (request.getContentLength() <= 0) {
            return;
        }
        InputStream in = request.getInputStream();
        OutputStream out = connection.getOutputStream();
        IOUtils.copy(in, out);
    }
}
