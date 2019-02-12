package com.github.bjuvensjo.rsimulator.core;

import com.github.bjuvensjo.rsimulator.core.config.Cache;
import com.github.bjuvensjo.rsimulator.core.config.Properties;
import com.github.bjuvensjo.rsimulator.core.config.Script;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

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

    @Properties
    @Cache
    @Script
    public Optional<SimulatorResponse> service(String rootPath, String rootRelativePath, String request, String contentType, Map<String, Object>... vars) {
        log.info("rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", rootPath, rootRelativePath, request, contentType);

        Optional<SimulatorResponse> simulatorResponse = handlers.get(contentType).findMatch(rootPath, rootRelativePath, request);

        log.info("simulatorResponse: {}", simulatorResponse);

        if (!simulatorResponse.isPresent()) {
            String message = "No simulatorResponse found!";
            log.warn("{}, rootPath: {}, rootRelativePath: {}, request: {}, contentType: {}", message, rootPath, rootRelativePath, request, contentType);
        }

        return simulatorResponse;
    }
}
