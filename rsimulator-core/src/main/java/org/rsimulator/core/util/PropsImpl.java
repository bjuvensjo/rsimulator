package org.rsimulator.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.rsimulator.core.config.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

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
    private File propertyFile;

    /**
     * {@inheritDoc}
     */
    @Cache
    public Properties getProperties(File file) {
        if (!file.exists()) {
            return EMPTY_PROPERTIES;
        }
        Properties result = new Properties();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            result.load(bis);
            bis.close();
        } catch (Exception e) {
            log.error("Error reading properties from: {}", file.getAbsolutePath(), e);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSimulatorCache() {
        return "true".equals(getProperties(propertyFile).getProperty(SIMULATOR_CACHE));
    }
}
