package org.rsimulator.core.config;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.CacheManager;

import org.rsimulator.core.controller.Controller;
import org.rsimulator.core.controller.ControllerCacheInterceptor;
import org.rsimulator.core.controller.ControllerScriptInterceptor;
import org.rsimulator.core.controller.Handler;
import org.rsimulator.core.controller.handler.regexp.TxtHandler;
import org.rsimulator.core.controller.handler.regexp.XmlHandler;
import org.rsimulator.core.util.FileUtils;
import org.rsimulator.core.util.FileUtilsCacheInterceptor;
import org.rsimulator.core.util.Props;
import org.rsimulator.core.util.PropsCacheInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * DIModule holds Guice configurations.
 * 
 * @author Magnus Bjuvensjö
 */
public class DIModule extends AbstractModule {
    private Logger log = LoggerFactory.getLogger(DIModule.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // ***** Properties *****
        URL resource = getClass().getResource("/rsimulator.properties");
        if (resource == null) {
            log.debug("No /rsimulator.properties resource exists. Configuring /rsimulator-default.properties");
            resource = getClass().getResource("/rsimulator-default.properties");
        }
        bind(File.class).annotatedWith(Names.named("rsimulator-core-properties")).toInstance(
                new File(resource.getFile()));

        // ***** Handlers *****
        Map<String, Handler> map = new HashMap<String, Handler>();
        TxtHandler txtHandler = new TxtHandler();
        requestInjection(txtHandler);
        XmlHandler xmlHandler = new XmlHandler();
        requestInjection(xmlHandler);
        map.put("txt", txtHandler);
        map.put("xml", xmlHandler);

        bind(new TypeLiteral<Map<String, Handler>>() {
        }).annotatedWith(Names.named("handlers")).toInstance(map);

        // ***** Interceptors for cache and script *****
        CacheManager.create();

        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("ControllerCache")).toInstance(
                CacheManager.create().getCache("ControllerCache"));
        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("FileUtilsCache")).toInstance(
                CacheManager.create().getCache("FileUtilsCache"));
        bind(net.sf.ehcache.Cache.class).annotatedWith(Names.named("PropsCache")).toInstance(
                CacheManager.create().getCache("PropsCache"));

        ControllerCacheInterceptor controllerCacheInterceptor = new ControllerCacheInterceptor();
        FileUtilsCacheInterceptor fileUtilsCacheInterceptor = new FileUtilsCacheInterceptor();
        PropsCacheInterceptor propsCacheInterceptor = new PropsCacheInterceptor();
        ControllerScriptInterceptor controllerScriptInterceptor = new ControllerScriptInterceptor();

        requestInjection(controllerCacheInterceptor);
        requestInjection(fileUtilsCacheInterceptor);
        requestInjection(propsCacheInterceptor);
        requestInjection(controllerScriptInterceptor);

        // Order is significant
        bindInterceptor(Matchers.subclassesOf(Controller.class), Matchers.annotatedWith(Cache.class),
                controllerCacheInterceptor);
        bindInterceptor(Matchers.subclassesOf(Controller.class), Matchers.annotatedWith(Script.class),
                controllerScriptInterceptor);
        bindInterceptor(Matchers.subclassesOf(FileUtils.class), Matchers.annotatedWith(Cache.class),
                fileUtilsCacheInterceptor);
        bindInterceptor(Matchers.subclassesOf(Props.class), Matchers.annotatedWith(Cache.class), propsCacheInterceptor);
    }
}
