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
        Map<String, String> contentTypes = new HashMap<String, String>();
        contentTypes.put("application/json", "json");
        contentTypes.put("application/xml", "xml");
        contentTypes.put("application/soap+xml", "xml");
        contentTypes.put("text/xml", "xml");
        contentTypes.put("default", "txt");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("contentTypes")).toInstance(contentTypes);
        
        // ***** Accepts *****
        Map<String, String> accepts = new HashMap<String, String>();
        accepts.put("application/json", "json");
        accepts.put("default", "txt");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("accepts")).toInstance(accepts);
        
    }
}
