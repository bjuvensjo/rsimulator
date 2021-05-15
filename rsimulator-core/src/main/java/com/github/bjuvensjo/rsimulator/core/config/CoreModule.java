package com.github.bjuvensjo.rsimulator.core.config;

import com.github.bjuvensjo.rsimulator.core.*;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.JsonHandler;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.TxtHandler;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.XmlHandler;
import com.github.bjuvensjo.rsimulator.core.util.FileUtils;
import com.github.bjuvensjo.rsimulator.core.util.FileUtilsCacheInterceptor;
import com.github.bjuvensjo.rsimulator.core.util.Props;
import com.github.bjuvensjo.rsimulator.core.util.PropsCacheInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * CoreModule holds Guice configurations.
 */
public class CoreModule extends AbstractModule {
    private final Logger log = LoggerFactory.getLogger(CoreModule.class);
    private static final CacheManager cacheManager = CacheManager.create();

    @Provides
    @Named("rsimulator-core-properties")
    java.util.Properties provideProperties() {
        java.util.Properties properties = new java.util.Properties();
        URL resource = getClass().getResource("/rsimulator.properties");
        if (resource == null) {
            properties.setProperty("simulatorCache", "false");
            properties.setProperty("ignoreXmlNamespaces", "false");
        } else {
            try (BufferedInputStream bis = new BufferedInputStream(resource.openStream())) {
                properties.load(bis);
            } catch (Exception e) {
                log.error("Error reading properties from: {}", resource.getPath(), e);
            }
        }
        return properties;
    }

    @Override
    protected void configure() {
        MapBinder<String, Handler> map = MapBinder.newMapBinder(binder(), String.class, Handler.class);
        map.addBinding("json").to(JsonHandler.class);
        map.addBinding("txt").to(TxtHandler.class);
        map.addBinding("xml").to(XmlHandler.class);

        // ***** Interceptors for cache and script *****
        Arrays.asList("SimulatorCache", "FileUtilsCache", "PropsCache").forEach(name ->
                bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named(name)).toInstance(cacheManager.getCache(name)));


        SimulatorPropertiesInterceptor simulatorPropertiesInterceptor = new SimulatorPropertiesInterceptor();
        SimulatorCacheInterceptor simulatorCacheInterceptor = new SimulatorCacheInterceptor();
        SimulatorScriptInterceptor simulatorScriptInterceptor = new SimulatorScriptInterceptor();
        FileUtilsCacheInterceptor fileUtilsCacheInterceptor = new FileUtilsCacheInterceptor();
        PropsCacheInterceptor propsCacheInterceptor = new PropsCacheInterceptor();

        requestInjection(simulatorPropertiesInterceptor);
        requestInjection(simulatorCacheInterceptor);
        requestInjection(simulatorScriptInterceptor);
        requestInjection(fileUtilsCacheInterceptor);
        requestInjection(propsCacheInterceptor);

        // Order is significant
        bindInterceptor(Matchers.subclassesOf(Simulator.class), Matchers.annotatedWith(Properties.class),
                simulatorPropertiesInterceptor);
        bindInterceptor(Matchers.subclassesOf(Simulator.class), Matchers.annotatedWith(Cache.class),
                simulatorCacheInterceptor);
        bindInterceptor(Matchers.subclassesOf(Simulator.class), Matchers.annotatedWith(Script.class),
                simulatorScriptInterceptor);
        bindInterceptor(Matchers.subclassesOf(FileUtils.class), Matchers.annotatedWith(Cache.class),
                fileUtilsCacheInterceptor);
        bindInterceptor(Matchers.subclassesOf(Props.class), Matchers.annotatedWith(Cache.class),
                propsCacheInterceptor);
    }
}
