package com.github.bjuvensjo.rsimulator.core.util;

import com.google.inject.ImplementedBy;

import java.nio.file.Path;
import java.util.List;

/**
 * FileUtils provides file utilities.
 *
 * @author Magnus Bjuvensjö
 */
@ImplementedBy(FileUtilsImpl.class)
public interface FileUtils {

    /**
     * Returns the requests that have the specified file extension and are in the specified directory or a subdirectory.
     *
     * @param path      the directory
     * @param extension the file extension
     * @return the requests that have the specified file extension and are in the specified directory or a subdirectory
     */
    List<Path> findRequests(Path path, String extension);

    /**
     * Returns the content of the specified path.
     *
     * @param path the path
     * @return the content of the specified path
     */
    String read(Path path);
}
