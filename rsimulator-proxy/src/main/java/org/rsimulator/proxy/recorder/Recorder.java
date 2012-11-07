package org.rsimulator.proxy.recorder;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Record proxy traffic to file
 *
 * @author Anders Bälter
 */
public class Recorder implements Filter {

    private Properties properties = new Properties();
    private Logger log = LoggerFactory.getLogger(Recorder.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        InputStream in = getClass().getResourceAsStream("recorder.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            log.warn("Could not load recorder.properties");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (recorderIsOn()) {
            // record request
            log.debug("Recording request");
        }
        chain.doFilter(request, response);
        if (recorderIsOn()) {
            // record response
            log.debug("Recording response");
        }
    }

    @Override
    public void destroy() {
    }

    private boolean recorderIsOn() {
        return "true".equals(properties.getProperty("recorder.record"));
    }
}
