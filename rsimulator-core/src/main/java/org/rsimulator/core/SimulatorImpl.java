package org.rsimulator.core;

import java.io.IOException;
import java.util.Map;

import org.rsimulator.core.config.Cache;
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
    @Cache
    @Script
    public SimulatorResponse service(String rootPath, String rootRelativePath, String request, String contentType)
            throws IOException {
        log.debug("rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", new Object[] {rootPath,
                rootRelativePath, request, contentType});
        SimulatorResponse simulatorResponse = handlers.get(contentType)
                .findMatch(rootPath, rootRelativePath, request);
        processResponse(simulatorResponse);
        log.debug("simulatorResponse: {}", simulatorResponse);
        return simulatorResponse;
    }

    private void processResponse(SimulatorResponse simulatorResponse) {
        if (simulatorResponse != null && simulatorResponse.getProperties() != null) {
            // Below to enable simulation of slow response
            String delay = simulatorResponse.getProperties().getProperty("delay");
            if (delay != null) {
                log.debug("Delaying response {} for {} ms.", simulatorResponse, delay);
                try {
                    Thread.sleep(Long.parseLong(delay));
                } catch (Exception e) {
                    log.error(null, e);
                }
            }
        }
    }
}
