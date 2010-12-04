package org.rsimulator.core.util;

import static org.rsimulator.core.config.Constants.REQUEST;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    // TODO Does not belong here... Couldn't UTF-8 be required!?
    private static final Pattern CHARACTER_ENCODING_PATTERN = Pattern.compile("encoding=\"([^\"]+)\"");
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String REQUEST_PATTERN = ".*" + REQUEST + "\\.";
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
                return t.matches(REQUEST_PATTERN + extension);
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
            String encoding = DEFAULT_ENCODING;
            boolean first = true;
            while ((n = bis.read(buffer)) > 0) {
                if (first) {
                    String s = new String(buffer, 0, n, encoding);
                    Matcher m = CHARACTER_ENCODING_PATTERN.matcher(s);
                    if (m.find()) {
                        encoding = m.group(1);
                    }
                    first = false;
                }
                sb.append(new String(buffer, 0, n, encoding));
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
