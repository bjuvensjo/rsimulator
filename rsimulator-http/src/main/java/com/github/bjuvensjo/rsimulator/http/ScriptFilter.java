package com.github.bjuvensjo.rsimulator.http;

import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.http.config.Constants;
import com.github.bjuvensjo.rsimulator.http.config.GlobalConfig;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@WebFilter(urlPatterns = {"/*"})
public class ScriptFilter implements Filter {
    private static final String GROOVY_PATTERN = com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST + ".*";

    private final Logger log = LoggerFactory.getLogger(ScriptFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("doFilter");
        if (shouldApplyFilter(request)) {
            log.debug("ApplyingScripts");
            Map<String, Object> vars = new HashMap<>();
            vars.put(Constants.SERVLET_REQUEST, request);
            vars.put(Constants.SERVLET_RESPONSE, response);
            vars.put(Constants.ROOT_PATH, GlobalConfig.rootPath);
            vars.put(Constants.USE_ROOT_RELATIVE_PATH, GlobalConfig.useRootRelativePath);

            applyScript(Scope.GLOBAL_REQUEST, vars);
            request.setAttribute(Constants.VARS, vars);
            request.setAttribute(Constants.ROOT_PATH, vars.get(Constants.ROOT_PATH));
            request.setAttribute(Constants.USE_ROOT_RELATIVE_PATH, vars.get(Constants.USE_ROOT_RELATIVE_PATH));
            chain.doFilter(request, response);
            vars.put(Constants.SIMULATOR_RESPONSE, request.getAttribute(Constants.SIMULATOR_RESPONSE));
            applyScript(Scope.LOCAL_RESPONSE, vars);
            applyScript(Scope.GLOBAL_RESPONSE, vars);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldApplyFilter(ServletRequest request) {
        String theRootPath = request.getParameter(Constants.ROOT_PATH);
        String theUseRootRelativePath = request.getParameter(Constants.USE_ROOT_RELATIVE_PATH);
        return (theRootPath == null && theUseRootRelativePath == null);
    }

    private void applyScript(Scope type, Map<String, Object> vars) {
        try {
            String root = null;
            String script = null;
            switch (type) {
                case GLOBAL_REQUEST:
                    root = GlobalConfig.rootPath;
                    script = "GlobalServletRequest.groovy";
                    break;
                case GLOBAL_RESPONSE:
                    root = GlobalConfig.rootPath;
                    script = "GlobalServletResponse.groovy";
                    break;
                case LOCAL_RESPONSE:
                    SimulatorResponse simulatorResponse = (SimulatorResponse) vars.get(Constants.SIMULATOR_RESPONSE);
                    if (simulatorResponse != null && simulatorResponse.getMatchingRequest() != null) {
                        Path matchingRequest = simulatorResponse.getMatchingRequest();
                        root = matchingRequest.getParent().toAbsolutePath().toString();
                        script = matchingRequest.getFileName().toString().replaceAll(GROOVY_PATTERN, "Servlet.groovy");
                    }
                    break;
                default:
                    break;
            }
            File file = new File(root + File.separator + script);
            if (file.exists()) {
                log.debug("Applying script {} of type: {}, with vars: {}", file, type, vars);
                String[] roots = new String[]{root};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} of type: {}, and updated vars are: {}", file, type, vars);
            } else {
                log.debug("When applying script of type {}, script {} is not an existing file", type, file.getPath());
            }
        } catch (Exception e) {
            log.error("Script error.", e);
        }
    }

    private enum Scope {
        GLOBAL_REQUEST, GLOBAL_RESPONSE, LOCAL_RESPONSE
    }
}
