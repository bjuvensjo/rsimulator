package com.github.bjuvensjo.rsimulator.http.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class HttpSimulatorConfigIT {
    private static final int BUFFER_SIZE = 500;
    private static final int READ_TIMEOUT = 12000;
    private static final String ENCODING = "UTF-8";
    private static final String PORT = System.getProperty("jetty.port", "25001");
    private String rootPath;

    @BeforeEach
    public void init() throws URISyntaxException {
        String resource = "/txt-examples";
        rootPath = Paths.get(getClass().getResource(resource).toURI()).toString().replace(resource, "");
    }

    @Test
    public void testConfigClassOfExtendsObjectString() {
        try {
            HttpSimulatorConfig.config(getClass(), "http://localhost:" + PORT + "/foo/bar");
            call();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConfigStringBoolean() {
        try {
            HttpSimulatorConfig.config(rootPath, false, "http://localhost:" + PORT);
            call();
            HttpSimulatorConfig.config(rootPath, true, "http://localhost:" + PORT);
            call();

            String resource = getClass().getSimpleName() + ".class";
            rootPath = new File(getClass().getResource(resource).getPath()).getParentFile().getPath();
            HttpSimulatorConfig.config(rootPath, false, "http://localhost:" + PORT);
            call();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConfigStringBooleanString() {
        try {
            HttpSimulatorConfig.config(rootPath, false, "http://localhost:" + PORT + "/qwerty");
            call();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void call() throws IOException {
        HttpURLConnection con = getConnection("http://localhost:" + PORT + "/com/github/bjuvensjo/rsimulator");
        try {
            con.getOutputStream().write("Hello".getBytes(ENCODING));
            String response = read(con.getInputStream());
            assertEquals("World", response);
        } finally {
            con.disconnect();
        }
    }

    private HttpURLConnection getConnection(String url) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "text/plain");
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
