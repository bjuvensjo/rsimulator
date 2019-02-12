package com.github.bjuvensjo.rsimulator.core;

import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;

/**
 * SimulatorPropertiesInterceptor is an interceptor that handles Properties if set on a {@link SimulatorResponse}.
 */
@Singleton
public class SimulatorPropertiesInterceptor implements MethodInterceptor {
    private static final String DELAY = "delay";
    private Logger log = LoggerFactory.getLogger(SimulatorPropertiesInterceptor.class);

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Optional<SimulatorResponse> simulatorResponseOptional = (Optional<SimulatorResponse>) invocation.proceed();

        simulatorResponseOptional
                .flatMap(SimulatorResponse::getProperties)
                .ifPresent(this::handleDelay);

        return simulatorResponseOptional;
    }

    private void handleDelay(Properties properties) {
        String delay = properties.getProperty(DELAY);
        if (delay != null) {
            log.debug("Delaying response {} ms.", delay);
            try {
                Thread.sleep(Long.parseLong(delay));
            } catch (Exception e) {
                log.error(null, e);
            }
        }
    }
}
