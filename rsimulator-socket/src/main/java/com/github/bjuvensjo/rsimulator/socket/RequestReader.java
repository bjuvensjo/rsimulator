package com.github.bjuvensjo.rsimulator.socket;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ei4577 on 05/03/14.
 */
@Singleton
public class RequestReader {
    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);

    @Inject
    @Named("encoding")
    private String encoding;

    @Inject
    @Named("headerLength")
    private int headerLength;

    @Inject
    @Named("headerBodyLengthBeginIndex")
    private int headerBodyLengthBeginIndex;

    @Inject
    @Named("headerBodyLengthEndIndex")
    private int headerBodyLengthEndIndex;

    public Request read(InputStream in) throws IOException {
        String header = null;
        String body = null;

        byte[] headerBytes = new byte[headerLength];
        int n = in.read(headerBytes);
        if (n > 0) {
            header = new String(headerBytes, encoding);
            log.debug("header: #{}# ", header);

            int length = Integer.parseInt(header.substring(headerBodyLengthBeginIndex, headerBodyLengthEndIndex));
            log.debug("length: {}", length);

            byte[] bodyBytes = new byte[length];
            in.read(bodyBytes);
            body = new String(bodyBytes, encoding);

            log.debug("body: #{}#", body);
        }
        log.info("request: #{}#", header + body);

        return new Request(header, body);
    }

    static class Request {
        private String header;
        private String body;

        Request(String header, String body) {
            this.header = header;
            this.body = body;
        }

        public String getHeader() {
            return header;
        }

        public String getBody() {
            return body;
        }

        public boolean isValid() {
            return header != null && body != null;
        }
    }
}
