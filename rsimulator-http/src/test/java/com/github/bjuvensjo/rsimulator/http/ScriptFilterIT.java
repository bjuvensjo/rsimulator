package com.github.bjuvensjo.rsimulator.http;

import com.github.bjuvensjo.rsimulator.http.config.HttpSimulatorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ScriptFilterIT {
    private static final int BUFFER_SIZE = 500;
    private static final int READ_TIMEOUT = 12000;
    private static final String ENCODING = "UTF-8";
    private static final String PORT = System.getProperty("jetty.port", "25001");

    @BeforeEach
    public void setUp() throws Exception {
        String resource = "/txt-examples";
        String rootPath = Paths.get(getClass().getResource(resource).toURI()).toString().replace(resource, "");
        HttpSimulatorConfig.config(rootPath, true, "http://localhost:" + PORT);
    }

    @Test
    public void testGlobalServletRequestScript() {
        HttpURLConnection con = getConnection("http://localhost:" + PORT, "application/json");
        try {
            con.setRequestProperty("user", "specific_user");
            String response = read(con.getInputStream());
            assertTrue(response.contains("ok"));
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            con.disconnect();
        }
    }

    @Test
    public void testGlobalServletResponseScript() {
        HttpURLConnection con = getConnection("http://localhost:" + PORT + "/properties", "text/plain");
        try {
            con.getOutputStream().write("test".getBytes(ENCODING));
            Map<String, List<String>> headerFields = con.getHeaderFields();
            System.out.println(headerFields);
            assertEquals("1000", con.getHeaderField("Error-Code"));
            assertEquals("Could not service request", con.getHeaderField("Error-Message"));
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection getConnection(String url, String contentType) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", contentType);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setReadTimeout(READ_TIMEOUT);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return con;
    }

    private String read(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buffer)) > 0) {
            sb.append(new String(buffer, 0, n, ENCODING));
        }
        return sb.toString();
    }
}
