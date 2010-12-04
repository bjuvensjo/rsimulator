package org.rsimulator.core.util;

import java.io.File;
import java.util.Date;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * PropsCacheInterceptor is an interceptor that caches {@link Props#getProperties(File)} so that the File is read from
 * disk only if modified..
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class PropsCacheInterceptor implements MethodInterceptor {
    private Logger log = LoggerFactory.getLogger(PropsCacheInterceptor.class);
    @Inject
    @Named("PropsCache")
    private Cache cache;

    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object response = null;
        File file = (File) invocation.getArguments()[0];
        String key = file.getAbsolutePath();
        Element cacheElement = cache.get(key);
        if (cacheElement != null) {
            Object[] cacheValue = (Object[]) cacheElement.getObjectValue();
            if (cacheValue != null && String.valueOf(file.lastModified()).equals(cacheValue[0])) {
                response = cacheValue[1];
                log.debug("{} returns from cache: {} (last modified: {}", new Object[] {invocation.getMethod(),
                        response, new Date(file.lastModified())});
                return cacheValue[1];
            }
        }
        response = invocation.proceed();
        cacheElement = new Element(key, new Object[] {String.valueOf(file.lastModified()), response});
        cache.put(cacheElement);
        log.debug("{} returns not from cache: {} (last modified: {}", new Object[] {invocation.getMethod(), response,
                new Date(file.lastModified())});
        return response;
    }
}
