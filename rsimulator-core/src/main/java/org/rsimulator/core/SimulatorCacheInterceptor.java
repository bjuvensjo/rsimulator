package org.rsimulator.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.rsimulator.core.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * SimulatorCacheInterceptor is an interceptor that caches invokations of
 * {@link Simulator#service(String, String, String, String)}.
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

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        log.debug("SimulatorCache is {}", props.isSimulatorCache());
        Object response = null;
        if (props.isSimulatorCache()) {
            if (invocation.getMethod().getName().equals("service")) {
                Object[] arguments = invocation.getArguments();
                String key = new StringBuilder().append(arguments[0]).append(arguments[1]).append(arguments[2])
                        .toString();
                Element cacheElement = cache.get(key);
                if (cacheElement != null) {
                    response = cacheElement.getObjectValue();
                    if (response != null) {
                        log.debug("{} returns from cache: {}", new Object[] {invocation.getMethod(), response});
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
        log.debug("{} returns not from cache: {}", new Object[] {invocation.getMethod(), response});
        return response;
    }
}
