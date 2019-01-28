package com.github.bjuvensjo.rsimulator.recorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

/**
 * Helps creating properties while recording
 *
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
class SimulatorProperties {
    private Logger log = LoggerFactory.getLogger(SimulatorProperties.class);
    private Properties properties;
    private String propertiesFilePath;

    /**
     * Create a new property file for the simulator
     *
     * @param propertiesFilePath the propertiesFilePath
     */
    SimulatorProperties(String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
        properties = new Properties();
    }

    void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    void save(String encoding) {
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(propertiesFilePath, true), encoding)) {
            properties.store(out, "Recorded properties");
        } catch (IOException e) {
            log.error("Can not store properties: {}", properties, e);
        }
    }
}
