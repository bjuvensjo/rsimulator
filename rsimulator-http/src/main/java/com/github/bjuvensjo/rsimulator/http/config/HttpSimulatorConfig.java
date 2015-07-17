package com.github.bjuvensjo.rsimulator.http.config;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpSimulatorConfig is a convenience class to use for configuring a HttpSimulator.
 * 
 * @author Magnus Bjuvensjö
 */
public final class HttpSimulatorConfig {
    private static final int READ_TIMEOUT = 12000;
    private static final String DEFAULT_SERVER_URL = "http://localhost:8080";
    private static final String METHOD = "GET";

    private HttpSimulatorConfig() {
    }

    /**
     * Configures the HttpSimulator (running on the default http://localhost:8080) with the root path of the folder of
     * the specified testClass class folder.
     * 
     * @param testClass the testClass
     * @throws IOException if something goes wrong
     */
    public static void config(Class<? extends Object> testClass) throws IOException {
        config(testClass, DEFAULT_SERVER_URL);
    }

    /**
     * Configures the HttpSimulator running on the specified serverURL (e.g. http://localhost:8080) with the root path
     * of the folder of the specified testClass class folder.
     * 
     * @param testClass the testClass
     * @param serverURL the serverURL
     * @throws IOException if something goes wrong
     */
    public static void config(Class<? extends Object> testClass, String serverURL) throws IOException {
        String resource = new StringBuilder().append(testClass.getSimpleName()).append(".class").toString();
        String rootPath = new File(testClass.getResource(resource).getPath()).getParentFile().getPath();
        config(rootPath, false, serverURL);
    }

    /**
     * Configures the HttpSimulator (running on the default http://localhost:8080) with the specified root path and
     * useRootRelativePath.
     * 
     * @param rootPath the rootPath
     * @param useRootRelativePath the useRootRelativePath
     * @throws IOException if something goes wrong
     */
    public static void config(String rootPath, boolean useRootRelativePath) throws IOException {
        config(rootPath, useRootRelativePath, DEFAULT_SERVER_URL);
    }

    /**
     * Configures the HttpSimulator running on the specified serverURL (e.g. http://localhost:8080) with the specified
     * root path and useRootRelativePath.
     * 
     * @param rootPath the rootPath
     * @param useRootRelativePath the useRootRelativePath
     * @param serverURL the serverURL
     * @throws IOException if something goes wrong
     */
    public static void config(String rootPath, boolean useRootRelativePath, String serverURL) throws IOException {
        HttpURLConnection con = null;
        try {
            String urlString = new StringBuilder().append(serverURL).append("/?").append(Constants.ROOT_PATH)
                    .append("=").append(rootPath).append("&").append(Constants.USE_ROOT_RELATIVE_PATH).append("=")
                    .append(String.valueOf(useRootRelativePath)).toString();
            con = (HttpURLConnection) new URL(urlString).openConnection();
            con.setRequestMethod(METHOD);
            con.setReadTimeout(READ_TIMEOUT);
            con.getResponseCode();
        } finally {
            con.disconnect();
        }
    }
}
