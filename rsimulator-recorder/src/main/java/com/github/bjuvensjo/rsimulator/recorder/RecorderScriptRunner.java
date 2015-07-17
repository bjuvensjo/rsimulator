package com.github.bjuvensjo.rsimulator.recorder;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecorderScriptRunner {

    private static final String GLOBAL_REQUEST = "RecorderRequest.groovy";
    private static final String GLOBAL_RESPONSE = "RecorderResponse.groovy";
    private Logger log = LoggerFactory.getLogger(RecorderScriptRunner.class);
    private Config config;

    public RecorderScriptRunner(Config config) {
        this.config = config;
    }

    public void runRequestScript(RecorderServletRequestWrapper recorderRequest, RecorderServletResponseWrapper recorderResponse, String encoding)
            throws IOException {
        Map<String, Object> vars = buildVars(recorderRequest, recorderResponse);
        vars.put(RecorderScriptVars.REQUEST_BODY_TO_RECORD, recorderRequest.getRequestBody(encoding));
        applyScript(GLOBAL_REQUEST, vars);
        recorderRequest.setAttribute(Constants.RELATIVE_RECORD_PATH, vars.get(RecorderScriptVars.RELATIVE_RECORD_PATH));
        recorderRequest.setAttribute(Constants.FILE_PREFIX, vars.get(RecorderScriptVars.FILE_PREFIX));
        recorderRequest.setAttribute(Constants.BASE_PATH, vars.get(RecorderScriptVars.BASE_PATH));
        recorderRequest.setAttribute(Constants.REQUEST_BODY_TO_RECORD, requestBodyToRecord(recorderRequest, vars));
    }

    public void runResponseScript(RecorderServletRequestWrapper recorderRequest,
                                  RecorderServletResponseWrapper recorderResponse, String encoding) throws IOException {
        Map<String, Object> vars = buildVars(recorderRequest, recorderResponse);
        vars.put(RecorderScriptVars.RESPONSE_BODY_TO_RECORD, recorderResponse.getResponseAsString(encoding));
        applyScript(GLOBAL_RESPONSE, vars);
        recorderRequest.setAttribute(Constants.RESPONSE_BODY_TO_RECORD, vars.get(RecorderScriptVars.RESPONSE_BODY_TO_RECORD));
    }

    private String requestBodyToRecord(RecorderServletRequestWrapper recorderRequest, Map vars) throws IOException {
        String dataToRecord = (String)vars.get(RecorderScriptVars.REQUEST_BODY_TO_RECORD);
        if(dataToRecord.length() > 0) {
            return dataToRecord;
        } else {
            return copyQueryString(recorderRequest);
        }
    }

    private String copyQueryString(HttpServletRequest request) {
        return request.getQueryString() != null ? request.getQueryString() : "";
    }

    private Map<String, Object> buildVars(RecorderServletRequestWrapper recorderRequest, RecorderServletResponseWrapper recorderResponse) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put(RecorderScriptVars.REQUEST, recorderRequest);
        vars.put(RecorderScriptVars.RESPONSE, recorderResponse);
        vars.put(RecorderScriptVars.RELATIVE_RECORD_PATH, requestedUriWithoutContext(recorderRequest));
        vars.put(RecorderScriptVars.BASE_PATH, basePath());
        vars.put(RecorderScriptVars.FILE_PREFIX, buildFilePrefix());
        return vars;
    }

    private void applyScript(String script, Map vars) {
        try {
            File file = new File((new StringBuilder()).append(basePath()).append(File.separator).append(script).toString());
            if(file.exists()) {
                log.debug("Applying script {} with vars: {}", new Object[] {file, vars});
                String roots[] = {basePath()};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} and updated vars are: {}", new Object[] {file, vars });
            } else {
                log.debug("When applying script path {} is not an existing file", basePath());
            }
        } catch(Exception e) {
            log.error("Script error.", e);
        }
    }

    private String basePath() {
        String basePath = config.get(Config.BASE_DIRECTORY);
        return basePath == null ? "" : basePath;
    }

    private String buildFilePrefix() {
        return (new SimpleDateFormat("yyyyMMddHHmmssSS")).format(new Date());
    }

    private String requestedUriWithoutContext(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length() + 1);
    }
}