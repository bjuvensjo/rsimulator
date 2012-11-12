package org.rsimulator.proxy.recorder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Helps creating properties while recording
 *
 * @author Anders Bälter
 */
public class SimulatorProperties {

    private File file;
    private static final String FILE_ENDING = ".properties";
    private static final String ENCODING = "UTF-8";
    private static final String EQUALS = "=";

    /**
     * Create a new property file for the simulator
     * @param basePath the base path for the current request
     * @param relativeRecordPath the relative record path for the current request
     * @param fileName the request and response prefix
     */
    public SimulatorProperties(String basePath, String relativeRecordPath, String fileName) {
        String fileWithPath = new StringBuilder(basePath)
                .append(File.separator)
                .append(relativeRecordPath)
                .append(File.separator).append(fileName).append(FILE_ENDING).toString();
        file = new File(fileWithPath);
    }

    /**
     * Writes a property line to file
     * @param key the key
     * @param value the value
     * @throws IOException if property line can't be written
     */
    public void set(String key, String value) throws IOException {
        String line = new StringBuilder(key).append(EQUALS).append(value).toString();
        Collection<String> lines = Arrays.asList(line);
        FileUtils.writeLines(file,ENCODING,lines);
    }
}
