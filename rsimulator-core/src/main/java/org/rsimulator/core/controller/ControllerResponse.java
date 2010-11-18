package org.rsimulator.core.controller;

import java.io.File;
import java.util.Properties;

import com.google.inject.ImplementedBy;

/**
 * ControllerResponse is returned by {@link Controller}.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(ControllerResponseImpl.class)
public interface ControllerResponse {

    /**
     * Returns the properties of a specific test data request and response pair. If the name of the test data request
     * file for instance is Test1Request.xml, the properties file must be named Test1.properties. For supported
     * properties, see *PropertiesInterceptor classes.
     *
     * @return the properties of a specific test data request and response pair
     */
    Properties getProperties();

    /**
     * Sets the specified properties.
     *
     * @param properties the properties to set
     */
    void setProperties(Properties properties);

    /**
     * Returns the test data response.
     *
     * @return the test data response
     */
    String getResponse();

    /**
     * Sets the specified response.
     *
     * @param response the response to set
     */
    void setResponse(String response);

    /**
     * Returns the matching request file.
     *
     * @return the matching request file
     */
    File getMatchingRequest();

    /**
     * Sets the specified file.
     *
     * @param file the file to set
     */
    void setMatchingRequest(File file);
}
