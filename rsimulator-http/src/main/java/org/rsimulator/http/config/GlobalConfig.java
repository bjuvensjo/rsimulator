package org.rsimulator.http.config;

/**
 * @author Anders Bälter
 */
public abstract class GlobalConfig {
    public static String rootPath;
    public static boolean useRootRelativePath;

    {
        rootPath = System.getProperty(Constants.ROOT_PATH) != null ? System.getProperty(Constants.ROOT_PATH)
                : Constants.DEFAULT_ROOT_PATH;
        useRootRelativePath = System.getProperty(Constants.USE_ROOT_RELATIVE_PATH) != null ? "true".equals(System
                .getProperty(Constants.USE_ROOT_RELATIVE_PATH)) : false;
    }
}
