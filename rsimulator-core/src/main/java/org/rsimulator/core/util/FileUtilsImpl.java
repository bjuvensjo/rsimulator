package org.rsimulator.core.util;

import static org.rsimulator.core.config.Constants.REQUEST;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rsimulator.core.config.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * FileUtilsImpl implements {@link FileUtils}.
 * 
 * @author Magnus Bjuvensjö
 */
@Singleton
public class FileUtilsImpl implements FileUtils {
    private static final int BUFFER_SIZE = 1000;
    private static final String ENCODING = "UTF-8";
    private static final String SUFFIX_PREFIX = new StringBuilder().append(REQUEST).append(".").toString();
    private Logger log = LoggerFactory.getLogger(FileUtilsImpl.class);

    private interface Filter {
        boolean accept(String t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<File> findRequests(File file, final String extension) {
        List<File> requests = new ArrayList<File>();
        find(file, new Filter() {
            public boolean accept(String t) {
                return t.endsWith(new StringBuilder().append(SUFFIX_PREFIX).append(extension).toString());
            }
        }, requests);
        log.debug("Requests: {}", requests);
        return requests;
    }

    /**
     * {@inheritDoc}
     */
    @Cache
    @Override
    public String read(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[BUFFER_SIZE];
            int n = -1;
            while ((n = bis.read(buffer)) > 0) {
                sb.append(new String(buffer, 0, n, ENCODING));
            }
            return sb.toString();
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }

    private void find(File file, Filter filter, List<File> files) {
        if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            for (int i = 0; i < dirFiles.length; i++) {
                find(dirFiles[i], filter, files);
            }
        } else if (filter.accept(file.getName())) {
            log.debug("adds {}", file.getName());
            files.add(file);
        }
    }
}
