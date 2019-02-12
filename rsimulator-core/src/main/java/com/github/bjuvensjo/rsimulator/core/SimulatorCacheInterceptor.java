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
import java.util.stream.Collectors;

/**
 * SimulatorCacheInterceptor is an interceptor that caches invokations of
 * {@link Simulator#service(String, String, String, String, Map...)}.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class SimulatorCacheInterceptor implements MethodInterceptor {
    private Logger log = LoggerFactory.getLogger(SimulatorCacheInterceptor.class);
    @Inject
    @Named("SimulatorCache")
    private Cache cache;
    @Inject
    private Props props;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.debug("SimulatorCache is {}", props.isSimulatorCache());
        Object response = null;
        if (props.isSimulatorCache()) {
            if (invocation.getMethod().getName().equals("service")) {
                Object[] arguments = invocation.getArguments();
                String key = Arrays.stream(arguments).map(Object::toString).collect(Collectors.joining());
                Element cacheElement = cache.get(key);
                if (cacheElement != null) {
                    response = cacheElement.getObjectValue();
                    if (response != null) {
                        log.debug("{} returns from cache: {}", new Object[]{invocation.getMethod(), response});
                        return response;
                    }
                }
                response = invocation.proceed();
                cacheElement = new Element(key, response);
                cache.put(cacheElement);
            }
        } else {
            response = invocation.proceed();
        }
        log.debug("{} returns not from cache: {}", new Object[]{invocation.getMethod(), response});
        return response;
    }
}
