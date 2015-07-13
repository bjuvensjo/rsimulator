package org.rsimulator.core;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * SimulatorResponseImpl implements {@link SimulatorResponse}.
 *
 * @author Magnus Bjuvensjö
 */
public class SimulatorResponseImpl implements SimulatorResponse {
    private String response;
    private Optional<Properties> properties;
    private Path matchingRequest;

    /**
     * Creates an instance with the specified parameters.
     *
     * @param response        the response
     * @param properties      the properties
     * @param matchingRequest the matching request
     */
    public SimulatorResponseImpl(String response, Properties properties, Path matchingRequest) {
        this.response = response;
        this.properties = Optional.ofNullable(properties);
        this.matchingRequest = matchingRequest;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Optional<Properties> getProperties() {
        return properties;
    }

    public void setProperties(Optional<Properties> properties) {
        this.properties = properties;
    }

    public Path getMatchingRequest() {
        return matchingRequest;
    }

    public void setMatchingRequest(Path matchingRequest) {
        this.matchingRequest = matchingRequest;
    }

    public String toString() {
        return new StringBuilder()
                .append("[")
                .append("response: ")
                .append(response)
                .append(", properties: ")
                .append(properties)
                .append(", matchingRequest: ")
                .append(matchingRequest)
                .append("]")
                .toString();
    }
}