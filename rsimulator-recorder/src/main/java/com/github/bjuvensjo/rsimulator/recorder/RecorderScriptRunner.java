package com.github.bjuvensjo.rsimulator.recorder;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * RecorderScriptRunner
 *
 * @author Anders Bälter
 * @author Magnus Bjuvensjö
 */
class RecorderScriptRunner {
    private static final String GLOBAL_REQUEST = "RecorderRequest.groovy";
    private static final String GLOBAL_RESPONSE = "RecorderResponse.groovy";
    private Logger log = LoggerFactory.getLogger(RecorderScriptRunner.class);
    private Config config;

    RecorderScriptRunner(Config config) {
        this.config = config;
    }

    void runRequestScript(String basePath, Map<String, Object> vars) {
        applyScript(basePath, GLOBAL_REQUEST, vars);
    }

    void runResponseScript(String basePath, Map<String, Object> vars) {
        applyScript(basePath, GLOBAL_RESPONSE, vars);
    }

    private void applyScript(String basePath, String script, Map vars) {
        try {
            File file = new File(basePath + File.separator + script);
            if (file.exists()) {
                log.debug("Applying script {} with vars: {}", new Object[]{file, vars});
                GroovyScriptEngine gse = new GroovyScriptEngine(new String[]{basePath});
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} and updated vars are: {}", new Object[]{file, vars});
            } else {
                log.debug("When applying script path {} is not an existing file", basePath);
            }
        } catch (Exception e) {
            log.error("Script error.", e);
        }
    }
}