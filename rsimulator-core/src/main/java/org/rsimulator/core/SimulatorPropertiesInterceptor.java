package org.rsimulator.core;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * SimulatorPropertiesInterceptor is an interceptor that handles Properties if set on a {@link SimulatorResponse}.
 */
@Singleton
public class SimulatorPropertiesInterceptor implements MethodInterceptor {
    private static final String DELAY = "delay";
    private Logger log = LoggerFactory.getLogger(SimulatorPropertiesInterceptor.class);

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SimulatorResponse simulatorResponse = (SimulatorResponse) invocation.proceed();

        if (simulatorResponse != null && simulatorResponse.getProperties() != null) {
            handleDelay(simulatorResponse);
        }
        
        return simulatorResponse;
    }

    private void handleDelay(SimulatorResponse simulatorResponse) {
        String delay = simulatorResponse.getProperties().getProperty(DELAY);
        if (delay != null) {
            log.debug("Delaying response {} for {} ms.", simulatorResponse, delay);
            try {
                Thread.sleep(Long.parseLong(delay));
            } catch (Exception e) {
                log.error(null, e);
            }
        }
    }
}
