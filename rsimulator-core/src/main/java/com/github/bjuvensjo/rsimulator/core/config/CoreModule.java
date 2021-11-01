package com.github.bjuvensjo.rsimulator.core.config;

import com.github.bjuvensjo.rsimulator.core.*;
import com.github.bjuvensjo.rsimulator.core.handler.parser.xml.XmlParseHandler;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.JsonHandler;
import com.github.bjuvensjo.rsimulator.core.handler.regexp.TxtHandler;
import com.github.bjuvensjo.rsimulator.core.util.FileUtils;
import com.github.bjuvensjo.rsimulator.core.util.FileUtilsCacheInterceptor;
import com.github.bjuvensjo.rsimulator.core.util.Props;
import com.github.bjuvensjo.rsimulator.core.util.PropsCacheInterceptor;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * CoreModule holds Guice configurations.
 */
public class CoreModule extends AbstractModule {
    private Logger log = LoggerFactory.getLogger(CoreModule.class);

    /*
     * (non-Javadoc)
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // ***** Properties *****
        java.util.Properties properties = new java.util.Properties();
        URL resource = getClass().getResource("/rsimulator.properties");
        if (resource == null) {
            properties.setProperty("simulatorCache", "false");
        } else {
            try (BufferedInputStream bis = new BufferedInputStream(resource.openStream())) {
                properties.load(bis);
            } catch (Exception e) {
                log.error("Error reading properties from: {}", resource.getPath(), e);
            }
        }
        bind(java.util.Properties.class).annotatedWith(Names.named("rsimulator-core-properties")).toInstance(properties);

        Map<String, Handler> handlerMap = getHandlerMap();
        handlerMap.values().forEach(this::requestInjection);

        bind(new TypeLiteral<Map<String, Handler>>() {
        }).annotatedWith(Names.named("handlers")).toInstance(handlerMap);

        // ***** Interceptors for cache and script *****
        CacheManager.create();

        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("SimulatorCache")).toInstance(
                CacheManager.create().getCache("SimulatorCache"));
        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("FileUtilsCache")).toInstance(
                CacheManager.create().getCache("FileUtilsCache"));
        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("PropsCache")).toInstance(
                CacheManager.create().getCache("PropsCache"));

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

    protected Map<String, Handler> getHandlerMap() {
        Map<String, Handler> map = new HashMap<>();
        map.put("json", new JsonHandler());
        map.put("txt", new TxtHandler());
        map.put("xml", new XmlParseHandler());
//        map.put("xml", new XmlHandler());
        return map;
    }
}
