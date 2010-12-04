package org.rsimulator.core.handler.regexp;

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

    /*
     * (non-Javadoc)
     * 
     * @see org.rsimulator.core.handler.regexp.AbstractHandler#format(java.lang.String)
     */
    @Override
    protected String format(String request) {
        return request;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rsimulator.core.handler.regexp.AbstractHandler#getExtension()
     */
    @Override
    protected String getExtension() {
        return EXTENSION;
    }
}
