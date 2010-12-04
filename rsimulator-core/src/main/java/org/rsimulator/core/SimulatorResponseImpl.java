package org.rsimulator.core;

import java.io.File;
import java.util.Properties;

/**
 * SimulatorResponseImpl implements {@link SimulatorResponse}.
 * 
 * @author Magnus Bjuvensjö
 */
public class SimulatorResponseImpl implements SimulatorResponse {
    private String response;
    private Properties properties;
    private File matchingRequest;

    /**
     * Creates an instance with the specified parameters.
     * 
     * @param aResponse the response
     * @param aProperties the properties
     * @param aMatchingRequest the matching request
     */
    public SimulatorResponseImpl(String aResponse, Properties aProperties, File aMatchingRequest) {
        this.response = aResponse;
        this.properties = aProperties;
        this.matchingRequest = aMatchingRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponse() {
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setResponse(String aResponse) {
        this.response = aResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getProperties() {
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Properties aProperties) {
        this.properties = aProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getMatchingRequest() {
        return matchingRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMatchingRequest(File aMatchingRequest) {
        this.matchingRequest = aMatchingRequest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringBuilder().append("[").append("response: ").append(response).append(", properties: ")
                .append(properties).append(", matchingRequest: ").append(matchingRequest).append("]").toString();
    }
}
