package com.github.bjuvensjo.rsimulator.proxy.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * CoreModule holds Guice configurations.
 */
public class ProxyModule extends AbstractModule {
    private final Logger log = LoggerFactory.getLogger(ProxyModule.class);

    /*
     * (non-Javadoc)
     *
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
        // ***** Properties *****
        URL resource = getClass().getResource("/URIMapper.txt");
        if (resource == null) {
            log.debug("No /URIMapper.txt resource exists.");
        } else {
            bind(File.class).annotatedWith(Names.named("uri-mappings")).toInstance(
                    new File(resource.getFile()));
        }
    }
}
