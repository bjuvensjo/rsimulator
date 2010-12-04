package org.rsimulator.core;

import java.io.File;
import java.util.Properties;

import com.google.inject.ImplementedBy;

/**
 * SimulatorResponse is returned by {@link Simulator}.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(SimulatorResponseImpl.class)
public interface SimulatorResponse {

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
