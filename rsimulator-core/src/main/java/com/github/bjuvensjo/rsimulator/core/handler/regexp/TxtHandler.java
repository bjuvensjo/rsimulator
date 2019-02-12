package com.github.bjuvensjo.rsimulator.core.handler.regexp;

import com.google.inject.Singleton;

/**
 * TxtHandler is a regular expression handler for text (.txt).
 *
 * @author Magnus Bjuvensjö
 * @see AbstractHandler
 */
@Singleton
public class TxtHandler extends AbstractHandler {
    private static final String EXTENSION = "txt";

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExtension() {
        return EXTENSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String format(String request) {
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String escape(String request, boolean isCandidate) {
        return request;
    }
}
