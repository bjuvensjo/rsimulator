package com.github.bjuvensjo.rsimulator.core.config;

/**
 * Constants.
 *
 * @author Magnus Bjuvensjö
 */
public final class Constants {
    /**
     * The REQUEST constant holds the value used as convention for the last part of the name of request files.
     */
    public static final String REQUEST = "Request";
    /**
     * The RESPONSE constant holds the value used as convention for the last part of the name of response files.
     */
    public static final String RESPONSE = "Response";

    /**
     * The CONTENT_TYPE constant is used as key in the script vars.
     */
    public static final String CONTENT_TYPE = "contentType";

    /**
     * The EXCEPTION constant is used as key in the script vars.
     */
    public static final String EXCEPTION = "exception";

    /**
     * The SIMULATOR_RESPONSE_OPTIONAL constant is used as key in the script vars.
     */
    public static final String SIMULATOR_RESPONSE_OPTIONAL = "simulatorResponseOptional";

    /**
     * The SIMULATOR_REQUEST constant is used as key in the script vars.
     */
    public static final String SIMULATOR_REQUEST = "simulatorRequest";

    /**
     * The ROOT_PATH constant is used as key in the script vars.
     */
    public static final String ROOT_PATH = "rootPath";

    /**
     * The ROOT_RELATIVE_PATH constant is used as key in the script vars.
     */
    public static final String ROOT_RELATIVE_PATH = "rootRelativePath";

    private Constants() {
    }
}
