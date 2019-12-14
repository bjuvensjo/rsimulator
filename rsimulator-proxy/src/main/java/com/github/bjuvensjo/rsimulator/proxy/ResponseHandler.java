package com.github.bjuvensjo.rsimulator.proxy;

import com.google.inject.Singleton;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

@Singleton
public class ResponseHandler {
    private final Logger log = LoggerFactory.getLogger(ResponseHandler.class);

    public void handle(HttpServletResponse response, HttpURLConnection connection) throws IOException {
        copyResponseHeaders(response, connection);
        copyResponse(response, connection);
    }

    private void copyResponseHeaders(HttpServletResponse response, HttpURLConnection con) throws IOException {
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
