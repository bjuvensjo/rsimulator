package com.github.bjuvensjo.rsimulator.core.util;

import com.google.inject.ImplementedBy;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Props is a utility that reloads properties from disk when changed.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(PropsImpl.class)
public interface Props {

    /**
     * Returns true if Simulator cache is on, otherwise false.
     *
     * @return true if Simulator cache is on, otherwise false.
     */
    boolean isSimulatorCache();

    /**
     * Returns the properties read from the specified path.
     *
     * @param path the path
     * @return the properties read from the specified path
     */
    Optional<Properties> getProperties(Path path);
}
