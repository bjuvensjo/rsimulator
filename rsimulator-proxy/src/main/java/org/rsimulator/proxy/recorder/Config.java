package org.rsimulator.proxy.recorder;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Recorder property handler
 *
 * @author Anders Bälter
 */
public class Config {

    private Logger log = LoggerFactory.getLogger(Recorder.class);

    private java.util.Properties properties = new java.util.Properties();

    public Config() {
        InputStream in = getClass().getResourceAsStream("recorder.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            log.warn("Could not load recorder.properties");
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public String get(String key) {
        return properties.get(key).toString();
    }

    public boolean getBoolean(String key) {
        return "true".equals(get(key));
    }
}
