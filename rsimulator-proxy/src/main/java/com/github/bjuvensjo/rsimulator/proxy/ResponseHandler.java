package com.github.bjuvensjo.rsimulator.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
@Singleton
public class ResponseHandler {
    private Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    public void handle(HttpServletResponse response, HttpURLConnection connection) throws IOException {
        copyResponseHeaders(response, connection);
        copyResponse(response, connection);
    }
    
    private void copyResponseHeaders(HttpServletResponse response, HttpURLConnection con) throws IOException {
        //response.setContentType(con.getContentType());
        response.setStatus(con.getResponseCode());
        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                for (String value : entry.getValue()) {
                    if (value != null) {
                        response.addHeader(key, value);
                        log.debug("copied response header {} : {}", key, value);
                    }
                }
            }
        }
    }

    private void copyResponse(HttpServletResponse response, HttpURLConnection connection) throws IOException {
        InputStream in = connection.getInputStream();
        ServletOutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
    }
}
