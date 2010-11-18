package org.rsimulator.core.controller;

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
 * ControllerImpl implements {@link Controller}.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class ControllerImpl implements Controller {
    private Logger log = LoggerFactory.getLogger(ControllerImpl.class);

    @Inject
    @Named("handlers")
    private Map<String, Handler> handlers;

    /**
     * {@inheritDoc}
     */
    @Cache
    @Script
    public ControllerResponse service(String rootPath, String rootRelativePath, String request, String contentType)
            throws IOException {
        log.debug("rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", new Object[] {rootPath,
                rootRelativePath, request, contentType});
        ControllerResponse controllerResponse = handlers.get(contentType)
                .findMatch(rootPath, rootRelativePath, request);
        processResponse(controllerResponse);
        log.debug("controllerResponse: {}", controllerResponse);
        return controllerResponse;
    }

    private void processResponse(ControllerResponse controllerResponse) {
        if (controllerResponse != null && controllerResponse.getProperties() != null) {
            // Below to enable mock of slow response
            String delay = controllerResponse.getProperties().getProperty("delay");
            if (delay != null) {
                log.debug("Delaying response {} for {} ms.", controllerResponse, delay);
                try {
                    Thread.sleep(Long.parseLong(delay));
                } catch (Exception e) {
                    log.error(null, e);
                }
            }
        }
    }
}
