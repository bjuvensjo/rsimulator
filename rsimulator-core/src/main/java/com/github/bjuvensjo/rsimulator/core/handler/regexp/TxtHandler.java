package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.google.inject.Singleton;

/**
 * TxtHandler is a regular expression handler for text (.txt).
 *
 * @see AbstractHandler
 */
@Singleton
public class TxtHandler extends AbstractHandler {

    @Override
    protected String getExtension() {
        return "txt";
    }

    @Override
    protected String format(String request) {
        return request;
    }

    @Override
    protected String escape(String request, boolean isCandidate) {
        return request;
    }
}
