package org.rsimulator.proxy;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * @author Anders Bälter
 */
public class ResponseHandler {
    private Logger log = LoggerFactory.getLogger(RequestHandler.class);

    public void copyResponseHeaders(HttpServletResponse response, HttpURLConnection con) throws IOException {
        response.setContentType(con.getContentType());
        response.setStatus(con.getResponseCode());
        Map<String, List<String>> headerFields = con.getHeaderFields();
        for (Map.Entry<String, List<String>> entry: headerFields.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                for (String value: entry.getValue()) {
                    if (value != null) {
                        response.addHeader(entry.getKey(), value);
                    }
                }
            }
        }
    }

    public void copyResponse(HttpServletResponse response, HttpURLConnection con) {
        try {
            InputStream in = con.getInputStream();
            ServletOutputStream out = response.getOutputStream();
            IOUtils.copy(in, out);
        } catch (IOException e) {
            log.error("Could not copy response data: {}", e.getMessage());
        }
    }
}
