package com.github.bjuvensjo.rsimulator.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Recorder property handler
 */
class Config {
    static final String BASE_DIRECTORY = "recorder.directory";
    private static final String RECORDER_IS_ON = "recorder.record";
    private final Properties properties = new Properties();

    Config() {
        try (InputStream in = Config.class.getResourceAsStream("/recorder.properties")) {
            properties.load(in);
        } catch (IOException e) {
            Logger log = LoggerFactory.getLogger(Config.class);
            log.error("Could not load recorder properties", e);
        }
    }

    String get(String key) {
        return properties.get(key).toString();
    }

    boolean isOn() {
        return "true".equalsIgnoreCase(get(Config.RECORDER_IS_ON));
    }
}
