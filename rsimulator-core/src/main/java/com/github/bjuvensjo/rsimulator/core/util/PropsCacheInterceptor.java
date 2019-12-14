package com.github.bjuvensjo.rsimulator.core.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

/**
 * PropsCacheInterceptor is an interceptor that caches {@link Props#getProperties(Path)} so that the Path is read from
 * disk only if modified..
 */
@Singleton
public class PropsCacheInterceptor implements MethodInterceptor {
    private final Logger log = LoggerFactory.getLogger(PropsCacheInterceptor.class);
    @Inject
    @Named("PropsCache")
    private Cache cache;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        return FileCacheSupport.invoke(invocation, cache);
    }
}
