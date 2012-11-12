package org.rsimulator.http;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import org.apache.commons.lang.StringUtils;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.http.config.Constants;
import org.rsimulator.http.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anders Bälter
 */
public class ScriptFilter implements Filter {
    private static final String ROOT_PATH = "rootPath";
    private static final String USE_ROOT_RELATIVE_PATH = "rootRelativePath";
    private static final String REQUEST = "request";
    private static final String RESPONSE = "response";
    private static final String SIMULATOR_RESPONSE = "simulatorResponse";
    private static final String GROOVY_PATTERN = org.rsimulator.core.config.Constants.REQUEST + ".*";

    private Logger log = LoggerFactory.getLogger(ScriptFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("doFilter");
        if (shouldApplyFilter(request)) {
            log.debug("ApplyingScripts");
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put(REQUEST, request);
            vars.put(RESPONSE, response);
            vars.put(ROOT_PATH, GlobalConfig.rootPath);
            vars.put(USE_ROOT_RELATIVE_PATH, GlobalConfig.useRootRelativePath);

            applyScript(Scope.GLOBAL_REQUEST, vars);
            request.setAttribute(Constants.ROOT_PATH, vars.get(ROOT_PATH));
            request.setAttribute(Constants.USE_ROOT_RELATIVE_PATH, vars.get(USE_ROOT_RELATIVE_PATH));
            chain.doFilter(request, response);
            vars.put(SIMULATOR_RESPONSE, request.getAttribute(Constants.SIMULATOR_RESPONSE));
            applyScript(Scope.LOCAL_RESPONSE, vars);
            applyScript(Scope.GLOBAL_RESPONSE, vars);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
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
                    SimulatorResponse simulatorResponse = (SimulatorResponse) vars.get(SIMULATOR_RESPONSE);
                    if (simulatorResponse != null && simulatorResponse.getMatchingRequest() != null) {
                        root = simulatorResponse.getMatchingRequest().getParentFile().getPath();
                        script = simulatorResponse.getMatchingRequest().getName().replaceAll(GROOVY_PATTERN, "Servlet.groovy");
                    }
                    break;
                default:
                    break;
            }
            File file = new File(new StringBuilder().append(root).append(File.separator).append(script).toString());
            if (file.exists()) {
                log.debug("Applying script {} of type: {}, with vars: {}", new Object[]{file, type, vars});
                String[] roots = new String[]{root};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} of type: {}, and updated vars are: {}", new Object[]{file, type, vars});
            } else {
                log.debug("When applying script of type {}, script path {} is not an existing file", type, root);
            }
        } catch (Exception e) {
            log.error("Script error.", e);
        }
    }

    private static enum Scope {
        GLOBAL_REQUEST, GLOBAL_RESPONSE, LOCAL_RESPONSE
    }
}
