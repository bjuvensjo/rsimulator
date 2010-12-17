package org.rsimulator.core;

import java.io.IOException;
import java.util.Map;

import org.rsimulator.core.config.Cache;
import org.rsimulator.core.config.Properties;
import org.rsimulator.core.config.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * SimulatorImpl implements {@link Simulator}.
 * 
 * @author Magnus Bjuvensjö
 */
@Singleton
public class SimulatorImpl implements Simulator {
    private Logger log = LoggerFactory.getLogger(SimulatorImpl.class);

    @Inject
    @Named("handlers")
    private Map<String, Handler> handlers;

    /**
     * {@inheritDoc}
     */
    @Properties
    @Cache
    @Script
    @Override
    public SimulatorResponse service(String rootPath, String rootRelativePath, String request, String contentType)
            throws IOException {
        log.debug("rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", new Object[] {rootPath,
                rootRelativePath, request, contentType});
        SimulatorResponse simulatorResponse = handlers.get(contentType).findMatch(rootPath, rootRelativePath, request);
        if (simulatorResponse == null) {
            String responseBody = "No simulatorResponse found!";
            log.error("{}, rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", new Object[] {
                    responseBody, rootPath, rootRelativePath, request, contentType});
        } else {
            log.debug("simulatorResponse: {}", simulatorResponse);
        }
        return simulatorResponse;
    }
}
