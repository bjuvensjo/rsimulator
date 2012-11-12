package org.rsimulator.proxy.recorder;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter allowing script execution during recording
 *
 * @author Anders Bälter
 */
public class RecorderScriptFilter implements Filter {
    private static final String RELATIVE_RECORD_PATH = "relativeRecordPath";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final String FILE_PREFIX = "filePrefix";
    private static final String BASE_PATH = "basePath";
    private static final String GLOBAL_REQUEST = "RecorderRequest.groovy";
    private static final String GLOBAL_RESPONSE = "RecorderResponse.groovy";

    private Config config;
    private Logger log = LoggerFactory.getLogger(RecorderScriptFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = new Config();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("doFilter");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (config.getBoolean(Config.RECORDER_IS_ON)) {
            log.debug("ApplyingScripts");
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put(REQUEST, httpServletRequest);
            vars.put(RESPONSE, httpServletResponse);
            vars.put(RELATIVE_RECORD_PATH, requestedUriWithoutContext(httpServletRequest));
            vars.put(BASE_PATH, basePath());
            vars.put(FILE_PREFIX, buildFilePrefix());

            applyScript(GLOBAL_REQUEST, vars);
            request.setAttribute(Constants.RELATIVE_RECORD_PATH, vars.get(RELATIVE_RECORD_PATH));
            request.setAttribute(Constants.FILE_PREFIX, vars.get(FILE_PREFIX));
            request.setAttribute(Constants.BASE_PATH, vars.get(BASE_PATH));
            chain.doFilter(httpServletRequest, httpServletResponse);
            applyScript(GLOBAL_RESPONSE, vars);
        } else {
            chain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void destroy() {
    }

    private void applyScript(String script, Map<String, Object> vars) {
        try {
            File file = new File(new StringBuilder().append(basePath()).append(File.separator).append(script).toString());
            if (file.exists()) {
                log.debug("Applying script {} with vars: {}", new Object[]{file, vars});
                String[] roots = new String[]{basePath()};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} and updated vars are: {}", new Object[]{file, vars});
            } else {
                log.debug("When applying script path {} is not an existing file", basePath());
            }
        } catch (Exception e) {
            log.error("Script error.", e);
        }
    }

    private String basePath() {
        String basePath = config.get(Config.BASE_DIRECTORY);
        return basePath != null ? basePath : "";
    }

    private String buildFilePrefix() {
        return new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
    }

    private String requestedUriWithoutContext(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length() + 1);
    }
}
