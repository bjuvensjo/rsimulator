package com.github.bjuvensjo.rsimulator.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.http.config.HttpSimulatorConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Anders Bälter
 */
public class ScriptFilterIT {
    private static final int BUFFER_SIZE = 500;
    private static final int READ_TIMEOUT = 12000;
    private static final String ENCODING = "UTF-8";
    private static final String PORT = System.getProperty("jetty.port", "25001");

    @Before
    public void setUp() throws Exception {
        String rootPath = new File(getClass().getResource("/").getPath()).getPath();
        HttpSimulatorConfig.config(rootPath, true, "http://localhost:" + PORT);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGlobalServletRequestScript() throws Exception {
        HttpURLConnection con = getConnection("GET", "http://localhost:" + PORT, "application/json");
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
    public void testGlobalServletResponseScript() throws Exception {
        HttpURLConnection con = getConnection("GET", "http://localhost:" + PORT + "/properties", "text/plain");
        try {
            con.getOutputStream().write("test".getBytes(ENCODING));
            assertEquals("1000", con.getHeaderField("Error-Code"));
            assertEquals("Could not service request", con.getHeaderField("Error-Message"));
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection getConnection(String method, String url,
                                            String contentType) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod(method);
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
