package com.github.bjuvensjo.rsimulator.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The URIMapper supports regular expression mappings configured in the resource /URIMapper.txt.
 *
 * @author Magnus Bjuvensjö
 */
@Singleton
public class URIMapper {
    private Logger log = LoggerFactory.getLogger(URIMapper.class);
    private List<Mapping> mappings;

    @Inject
    public URIMapper(@Named("uri-mappings") File mappingsFile) throws IOException {
        mappings = new ArrayList<Mapping>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(mappingsFile));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("#") && line.length() > 0) {
                    String[] mapping = line.split(" *= *");
                    log.debug("Adds mapping: {} = {}", mapping);
                    mappings.add(new Mapping(mapping[0], mapping[1]));
                }
            }
        } finally {
            IOUtils.closeQuietly(br);
        }
    }

    public String map(String uri) {
        for (Mapping mapping : mappings) {
            Matcher matcher = mapping.from.matcher(uri);
            if (matcher.matches()) {
                String result = mapping.to;
                log.debug("uri: {}, mapping to: {}", uri, result);
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    log.debug("group {}: {}", i, matcher.group(i));
                    result = result.replaceAll("\\$\\{" + i + "\\}", matcher.group(i));
                }
                log.debug("result: {}", result);
                return result;
            }
        }
        return null;
    }

    private static class Mapping {
        Pattern from;
        String to;

        public Mapping(String from, String to) {
            this.from = Pattern.compile(from);
            this.to = to;
        }
    }
}
