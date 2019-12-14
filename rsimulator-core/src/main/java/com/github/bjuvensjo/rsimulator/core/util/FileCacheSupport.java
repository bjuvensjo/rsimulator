package com.github.bjuvensjo.rsimulator.core.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.TimeUnit;

public class FileCacheSupport {
    private static final Logger log = LoggerFactory.getLogger(FileCacheSupport.class);

    public static Object invoke(MethodInvocation invocation, Cache cache) throws Throwable {
        Path path = (Path) invocation.getArguments()[0];
        Path key = path.toAbsolutePath();
        FileTime lastModifiedTime = Files.exists(path) ? Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime() : FileTime.from(0, TimeUnit.DAYS);
        Element cacheElement = cache.get(key);
        if (cacheElement != null) {
            Object[] cacheValue = (Object[]) cacheElement.getObjectValue();
            if (cacheValue != null && lastModifiedTime.equals(cacheValue[0])) {
                log.debug("{} returns from cache: {} (last modified: {}", invocation.getMethod(), cacheValue[1], lastModifiedTime);
                return cacheValue[1];
            }
        }
        Object response = invocation.proceed();
        cacheElement = new Element(key, new Object[]{lastModifiedTime, response});
        cache.put(cacheElement);
        log.debug("{} returns not from cache: {} (last modified: {}", invocation.getMethod(), response, lastModifiedTime);
        return response;
    }
}
