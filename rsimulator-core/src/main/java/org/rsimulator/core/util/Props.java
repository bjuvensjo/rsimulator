package org.rsimulator.core.util;

import java.io.File;
import java.util.Properties;

import com.google.inject.ImplementedBy;

/**
 * Props is a utility that reloads properties from disk when changed.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(PropsImpl.class)
public interface Props {

    /**
     * Returns true if Controller cache is on, otherwise false.
     *
     * @return true if Controller cache is on, otherwise false.
     */
    boolean isControllerCache();

    /**
     * Returns the properties read from the specified file.
     *
     * @param file the file
     * @return the properties read from the specified file
     */
    Properties getProperties(File file);
}
