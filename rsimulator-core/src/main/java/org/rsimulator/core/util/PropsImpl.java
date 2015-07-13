package org.rsimulator.core.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.rsimulator.core.config.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * PropsImpl implements {@link Props}.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class PropsImpl implements Props {
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private static final String SIMULATOR_CACHE = "simulatorCache";
    private Logger log = LoggerFactory.getLogger(PropsImpl.class);
    @Inject
    @Named("rsimulator-core-properties")
    private Path propertyPath;

    @Cache
    public Properties getProperties(Path path) {
        if (!Files.exists(path)) {
            return EMPTY_PROPERTIES;
        }
        Properties result = new Properties();
        try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path))) {
            result.load(bis);
        } catch (Exception e) {
            log.error("Error reading properties from: {}", path.toAbsolutePath(), e);
        }
        return result;
    }

    public boolean isSimulatorCache() {
        return "true".equals(getProperties(propertyPath).getProperty(SIMULATOR_CACHE));
    }
}
