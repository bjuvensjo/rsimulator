package com.github.bjuvensjo.rsimulator.core;

import java.util.Optional;

/**
 * Handler.
 *
 * @author Magnus Bjuvensjö
 */
public interface Handler {

    /**
     * Returns a {@link SimulatorResponse} for the specified parameters.
     *
     * @param rootPath         the root path
     * @param rootRelativePath the root relative path
     * @param request          the request
     * @return a {@link SimulatorResponse} for the specified parameters
     */
    Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request);
}
