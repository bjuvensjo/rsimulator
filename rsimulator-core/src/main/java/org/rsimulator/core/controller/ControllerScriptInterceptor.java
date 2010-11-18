package org.rsimulator.core.controller;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * ControllerScriptInterceptor is an interceptor that supports Groovy scripts intercepting invocations of
 * {@link Controller#service(String, String, String, String)}.
 * <p/>
 * The scripts supported are:
 * <ol>
 * <li>GlobalRequest.groovy; Must be put in the rootPath folder and is applied before the invocation.</li>
 * <li>&lt;TestName&gt;.groovy; Must be put in the same folder as test request and response and is applied first after
 * the invocation. The name of the groovy file must be the same as for the test request, e.g. Test1Request.txt -
 * Test1.groovy. If this script sets a {@link ControllerResponse} in the vars map (see below), the
 * {@link ControllerResponse} is directly returned.</li>
 * <li>GlobalResponse.groovy; Must be put in the rootPath folder and is applied last after the invocation.</li>
 * </ol>
 * <p/>
 * All script have a Map<String, Object> available through the variable vars. The keys contentType, controllerResponse,
 * request, rootPath, rootRelativePath can be used to access invocation arguments and return value. In addition, the map
 * can be used to communicate arbitrary objects between the Groovy scripts.
 */
@Singleton
public class ControllerScriptInterceptor implements MethodInterceptor {
    private static final int CONTENT_TYPE_INDEX = 3;
    private static final int REQUEST_INDEX = 2;
    private static final int ROOT_RELATIVE_PATH_INDEX = 1;
    private static final int ROOT_PATH_INDEX = 0;
    private static final String CONTENT_TYPE = "contentType";
    private static final String CONTROLLER_RESPONSE = "controllerResponse";
    private static final String REQUEST = "request";
    private static final String ROOT_PATH = "rootPath";
    private static final String ROOT_RELATIVE_PATH = "rootRelativePath";
    private static final String GROOVY_PATTERN = org.rsimulator.core.config.Constants.REQUEST + ".*";
    private Logger log = LoggerFactory.getLogger(ControllerScriptInterceptor.class);

    private enum Scope {
        GLOBAL_REQUEST, GLOBAL_RESPONSE, LOCAL_RESPONSE
    }

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.debug("Arguments are {}", invocation.getArguments());
        Map<String, Object> vars = new HashMap<String, Object>();

        vars.put(ROOT_PATH, invocation.getArguments()[ROOT_PATH_INDEX]);
        vars.put(ROOT_RELATIVE_PATH, invocation.getArguments()[ROOT_RELATIVE_PATH_INDEX]);
        vars.put(REQUEST, invocation.getArguments()[REQUEST_INDEX]);
        vars.put(CONTENT_TYPE, invocation.getArguments()[CONTENT_TYPE_INDEX]);

        applyScript(Scope.GLOBAL_REQUEST, vars);
        ControllerResponse controllerResponse = (ControllerResponse) vars.get(CONTROLLER_RESPONSE);
        if (controllerResponse != null) {
            // TODO Who should handle properties!?
            log.debug("Returning {}", controllerResponse);
            return controllerResponse;
        }

        invocation.getArguments()[ROOT_PATH_INDEX] = vars.get(ROOT_PATH);
        invocation.getArguments()[ROOT_RELATIVE_PATH_INDEX] = vars.get(ROOT_RELATIVE_PATH);
        invocation.getArguments()[REQUEST_INDEX] = vars.get(REQUEST);
        invocation.getArguments()[CONTENT_TYPE_INDEX] = vars.get(CONTENT_TYPE);
        controllerResponse = (ControllerResponse) invocation.proceed();

        vars.put(CONTROLLER_RESPONSE, controllerResponse);
        applyScript(Scope.LOCAL_RESPONSE, vars);
        applyScript(Scope.GLOBAL_RESPONSE, vars);
        return vars.get(CONTROLLER_RESPONSE);
    }

    private void applyScript(Scope type, Map<String, Object> vars) {
        try {
            String root = null;
            String script = null;
            switch (type) {
            case GLOBAL_REQUEST:
                root = (String) vars.get(ROOT_PATH);
                script = "GlobalRequest.groovy";
                break;
            case GLOBAL_RESPONSE:
                root = (String) vars.get(ROOT_PATH);
                script = "GlobalResponse.groovy";
                break;
            case LOCAL_RESPONSE:
                ControllerResponse controllerResponse = (ControllerResponse) vars.get(CONTROLLER_RESPONSE);
                root = controllerResponse.getMatchingRequest().getParentFile().getPath();
                script = controllerResponse.getMatchingRequest().getName().replaceAll(GROOVY_PATTERN, ".groovy");
                break;
            default:
                break;
            }
            File file = new File(new StringBuilder().append(root).append(File.separator).append(script).toString());
            if (file.exists()) {
                log.debug("Applying script {} of type: {}, with vars: {}", new Object[] {file, type, vars});
                String[] roots = new String[] {root};
                GroovyScriptEngine gse = new GroovyScriptEngine(roots);
                Binding binding = new Binding();
                binding.setVariable("vars", vars);
                gse.run(script, binding);
                log.debug("Applied script {} of type: {}, and updated vars are: {}", new Object[] {file, type, vars});
            } else {
                log.debug("When applying script of type {}, script path {} is not an existing file", type, root);
            }
        } catch (Exception e) {
            log.error("Script error.", e);
        }
    }
}
