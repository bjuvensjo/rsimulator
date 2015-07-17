package com.github.bjuvensjo.rsimulator.socket.config;

/**
 * Constants.
 * 
 * @author Magnus Bjuvensjö
 */
public interface Constants {
    /**
     * The parameter name that is used to set/unset the use of relative root path.
     */
    public static final String USE_ROOT_RELATIVE_PATH = "useRootRelativePath";

    /**
     * The parameter name that is used to set the root path.
     */
    public static final String ROOT_PATH = "rootPath";

    /**
     * The parameter name that is used to set the port.
     */
    public static final String PORT = "port";

    public static final String DEFAULT_ROOT_PATH = "src/main/resources";

    public static final int DEFAULT_PORT = 14030;
}
