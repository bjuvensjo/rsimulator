package com.github.bjuvensjo.rsimulator.core.util;

import groovy.lang.Singleton;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
public class FileCacheSupport {
    private static final Logger log = LoggerFactory.getLogger(FileCacheSupport.class);

    public Object invoke(MethodInvocation invocation, Cache cache) throws Throwable {
        Path path = (Path) invocation.getArguments()[0];
        Path key = path.toAbsolutePath();
        FileTime lastModifiedTime = getLastModifiedTime(path);
        return Optional.ofNullable(cache.get(key))
                .map(Element::getObjectValue)
                .filter(ov -> lastModifiedTime.equals(((Object[]) ov)[0]))
                .map(ov -> {
                    Object response = ((Object[]) ov)[1];
                    log.debug("{}, returns from cache: {} (last modified: {}", invocation.getMethod().getName(), response, lastModifiedTime);
                    return ((Object[]) ov)[1];
                })
                .orElseGet(() -> {
                    Object response;
                    try {
                        response = invocation.proceed();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                    Element cacheElement = new Element(key, new Object[]{lastModifiedTime, response});
                    cache.put(cacheElement);
                    log.debug("{}, returns not from cache: {} (last modified: {}", invocation.getMethod().getName(), response, lastModifiedTime);
                    return response;
                });
    }

    FileTime getLastModifiedTime(Path path) throws java.io.IOException {
        return Files.exists(path) ? Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime() : FileTime.from(0, TimeUnit.DAYS);
    }
}
