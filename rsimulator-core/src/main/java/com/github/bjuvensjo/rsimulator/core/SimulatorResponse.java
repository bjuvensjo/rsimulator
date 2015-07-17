package com.github.bjuvensjo.rsimulator.core;

import com.google.inject.ImplementedBy;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

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
    Optional<Properties> getProperties();

    /**
     * Sets the specified properties.
     *
     * @param properties the properties to set
     */
    void setProperties(Optional<Properties> properties);

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
     * Returns the matching request path.
     *
     * @return the matching request path
     */
    Path getMatchingRequest();

    /**
     * Sets the specified path.
     *
     * @param path the path to set
     */
    void setMatchingRequest(Path path);
}
