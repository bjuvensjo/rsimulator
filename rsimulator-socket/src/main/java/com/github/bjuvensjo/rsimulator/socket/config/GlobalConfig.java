package com.github.bjuvensjo.rsimulator.socket.config;

/**
 * GlobalConfig
 *
 * @author Magnus Bjuvensjö
 */
public class GlobalConfig {
    public static int port;
    public static String rootPath;
    public static boolean useRootRelativePath;

    static {
        port = System.getProperty(Constants.PORT) != null ? Integer.parseInt(System
                .getProperty(Constants.PORT)) : Constants.DEFAULT_PORT;

        rootPath = System.getProperty(Constants.ROOT_PATH) != null ? System.getProperty(Constants.ROOT_PATH)
                : Constants.DEFAULT_ROOT_PATH;

        useRootRelativePath = System.getProperty(Constants.USE_ROOT_RELATIVE_PATH) != null ? "true".equals(System
                .getProperty(Constants.USE_ROOT_RELATIVE_PATH)) : false;
    }

    private GlobalConfig() {
        // noop
    }
}
