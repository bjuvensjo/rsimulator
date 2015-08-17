package com.github.bjuvensjo.rsimulator.recorder;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Recorder property handler
 *
 * @author Anders Bälter
 */
public class Config {

    public static final String BASE_DIRECTORY = "recorder.directory";
    public static final String RECORDER_IS_ON = "recorder.record";

    private Logger log = LoggerFactory.getLogger(Recorder.class);

    private java.util.Properties properties = new java.util.Properties();

    public Config() {
        InputStream in = null;
        try {
            in = Config.class.getResourceAsStream("/recorder.properties");
            this.properties.load(in);
        } catch (Exception e) {
            log.warn("Could not load recorder properties", e);
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
