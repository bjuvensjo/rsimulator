package com.github.bjuvensjo.rsimulator.core.util;

import com.github.bjuvensjo.rsimulator.core.config.Cache;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.bjuvensjo.rsimulator.core.config.Constants.REQUEST;

/**
 * FileUtilsImpl implements {@link FileUtils}.
 */
@Singleton
public class FileUtilsImpl implements FileUtils {
    private static final String ENCODING = "UTF-8";
    private static final String SUFFIX_PREFIX = REQUEST.concat(".");
    private final Logger log = LoggerFactory.getLogger(FileUtilsImpl.class);

    public List<Path> findRequests(Path path, String extension) {
        try (Stream<Path> stream = Files.walk(path)) {
            List<Path> requests = stream
                    .filter(p -> Files.isRegularFile(p) && p.toFile().getName().endsWith(SUFFIX_PREFIX.concat(extension)))
                    .sorted(Comparator.comparing(Path::getNameCount).thenComparing(Path::compareTo)) // Do not want depth first as given by Files.walk
                    .collect(Collectors.toList());
            log.debug("Requests: {}", requests);
            return requests;
        } catch (Exception e) {
            log.warn("Cannot find requests.", e);
            return Collections.emptyList();
        }
    }

    @Cache
    public String read(Path path) {
        try {
            return new String(Files.readAllBytes(path), ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
