package org.rsimulator.core.controller;

import java.io.IOException;

import com.google.inject.ImplementedBy;

/**
 * The Controller encapsulates the control logic.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(ControllerImpl.class)
public interface Controller {

    /**
     * Returns a ControllerResponse that matches the specified request.
     *
     * @param rootPath the root path on which to search recursively for matches to the specified request.
     * @param rootRelativePath the path on which to search recursively for matches to the specified request.
     * @param request the request
     * @param contentType the content type of the request, e.g. txt or xml
     * @return a ControllerResponse that matches the specified request
     * @throws IOException if something goes wrong
     */
    ControllerResponse service(String rootPath, String rootRelativePath, String request, String contentType)
            throws IOException;
}
