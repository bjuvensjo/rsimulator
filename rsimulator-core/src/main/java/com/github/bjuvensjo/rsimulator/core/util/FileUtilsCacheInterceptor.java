package com.github.bjuvensjo.rsimulator.core.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.sf.ehcache.Cache;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.nio.file.Path;

/**
 * FileUtilsCacheInterceptor is an interceptor that caches {@link FileUtils#read(Path)} so that the File is read from
 * disk only if modified.
 */
@Singleton
public class FileUtilsCacheInterceptor implements MethodInterceptor {
    @Inject
    @Named("FileUtilsCache")
    private Cache cache;
    @Inject
    private FileCacheSupport fileCacheSupport;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        return fileCacheSupport.invoke(invocation, cache);
    }
}
