package com.github.bjuvensjo.rsimulator.core;

import com.github.bjuvensjo.rsimulator.core.util.Props;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SimulatorCacheInterceptor is an interceptor that caches invocations of
 * {@link Simulator#service(String, String, String, String, Map...)}.
 */
@Singleton
public class SimulatorCacheInterceptor implements MethodInterceptor {
    private final Logger log = LoggerFactory.getLogger(SimulatorCacheInterceptor.class);
    @Inject
    @Named("SimulatorCache")
    Cache cache;
    @Inject
    Props props;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.debug("SimulatorCache is {}", props.isSimulatorCache());
        if (props.isSimulatorCache()) {
            if (invocation.getMethod().getName().equals("service")) {
                Object[] arguments = invocation.getArguments();
                // Exclude vars from key
                String key = Arrays.stream(Arrays.copyOf(arguments, arguments.length - 1)).sequential().map(Object::toString).collect(Collectors.joining());
                return Optional.ofNullable(cache.get(key))
                        .map(ce -> {
                            Object response = ce.getObjectValue();
                            log.debug("{}, cache is on and returns from cache: {}", invocation.getMethod().getName(), response);
                            return response;
                        })
                        .orElseGet(() -> {
                            try {
                                Object response = invocation.proceed();
                                Element element = new Element(key, response);
                                cache.put(element);
                                log.debug("{}, cache is on and has put in cache and returns: {}", invocation.getMethod().getName(), response);
                                return response;
                            } catch (Throwable throwable) {
                                throw new RuntimeException(throwable);
                            }
                        });
            }
        }
        Object response = invocation.proceed();
        log.debug("{}, cache is off and returns: {}", invocation.getMethod().getName(), response);
        return response;
    }
}
