package org.rsimulator.core;

import java.io.IOException;

import com.google.inject.ImplementedBy;

/**
 * The Simulator encapsulates the simulation logic.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(SimulatorImpl.class)
public interface Simulator {

    /**
     * Returns a SimulatorResponse that matches the specified request.
     *
     * @param rootPath the root path on which to search recursively for matches to the specified request
     * @param rootRelativePath the path on which to search recursively for matches to the specified request
     * @param request the request
     * @param contentType the content type of the request, e.g. txt or xml
     * @return a SimulatorResponse that matches the specified request
     * @throws IOException if something goes wrong
     */
    SimulatorResponse service(String rootPath, String rootRelativePath, String request, String contentType)
            throws IOException;
}
