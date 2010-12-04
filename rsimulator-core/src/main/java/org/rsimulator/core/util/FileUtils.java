package org.rsimulator.core.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * FileUtils provides file utilities.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(FileUtilsImpl.class)
public interface FileUtils {

    /**
     * Returns the requests that have the specified file extension and are in the specified file directory or a
     * subdirectory.
     *
     * @param file the directory
     * @param extension the file extension
     * @return the requests that have the specified file extension and are in the specified file directory or a
     *         subdirectory
     */
    List<File> findRequests(File file, final String extension);

    /**
     * Returns the content of the specified file.
     *
     * @param file the file
     * @return the content of the specified file
     * @throws IOException if the file can not be read
     */
    String read(File file) throws IOException;
}
