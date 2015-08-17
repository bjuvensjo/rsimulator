package com.github.bjuvensjo.rsimulator.http.config;

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
        // ***** Simulator content types *****
        Map<String, String> simulatorContentTypes = new HashMap<String, String>();
        simulatorContentTypes.put("application/json", "json");
        simulatorContentTypes.put("application/xml", "xml");
        simulatorContentTypes.put("application/soap+xml", "xml");
        simulatorContentTypes.put("text/xml", "xml");
        simulatorContentTypes.put("default", "txt");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("simulatorContentTypes")).toInstance(simulatorContentTypes);

        // ***** Accepts *****
        Map<String, String> accepts = new HashMap<String, String>();
        accepts.put("application/json", "json");
        accepts.put("default", "txt");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("accepts")).toInstance(accepts);
        
        // ***** Response content types *****
        Map<String, String> responseContentTypes = new HashMap<String, String>();
        responseContentTypes.put("json", "application/json");
        responseContentTypes.put("txt", "text/plain");
        responseContentTypes.put("xml", "application/xml");
        responseContentTypes.put("default", "text/plain");

        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("responseContentTypes")).toInstance(responseContentTypes);        
    }
}
