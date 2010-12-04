package org.rsimulator.http.config;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * CoreModule holds Guice configurations.
 * 
 * @author Magnus Bjuvensjö
 */
public class HttpModule extends AbstractModule {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // ***** Content types *****
        Map<String, String> map = new HashMap<String, String>();
        map.put("application/xml", "xml");
        map.put("application/soap+xml", "xml");
        map.put("text/xml", "xml");
        map.put("default", "txt");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("contentTypes")).toInstance(map);
    }
}
