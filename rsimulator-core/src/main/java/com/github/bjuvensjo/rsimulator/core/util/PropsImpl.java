package com.github.bjuvensjo.rsimulator.core.util;

import com.github.bjuvensjo.rsimulator.core.config.Cache;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * PropsImpl implements {@link Props}.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class PropsImpl implements Props {
    private static final String SIMULATOR_CACHE = "simulatorCache";
    private Logger log = LoggerFactory.getLogger(PropsImpl.class);
    @Inject
    @Named("rsimulator-core-properties")
    private Properties properties;

    public boolean isSimulatorCache() {
        return properties.getProperty(SIMULATOR_CACHE).equalsIgnoreCase("true");
    }

    @Cache
    public Optional<Properties> getProperties(Path path) {
        if (!Files.exists(path)) {
            return Optional.ofNullable(null);
        }
        Properties properties = new Properties();
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path))) {
            properties.load(bis);
        } catch (Exception e) {
            log.error("Error reading properties from: {}", path.toAbsolutePath(), e);
        }
        return Optional.of(properties);
    }
}
