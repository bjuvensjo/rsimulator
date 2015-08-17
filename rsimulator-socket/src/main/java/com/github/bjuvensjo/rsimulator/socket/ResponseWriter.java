package com.github.bjuvensjo.rsimulator.socket;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang.StringUtils;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by ei4577 on 05/03/14.
 */
@Singleton
public class ResponseWriter {
    private static final Logger log = LoggerFactory.getLogger(ResponseWriter.class);

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

    public void write(RequestReader.Request request, SimulatorResponse simulatorResponse, OutputStream out) throws IOException {
        String responseBody = simulatorResponse.getResponse();

        log.debug("responseBody: #{}#", responseBody);

        String response = prependHeader(request.getHeader(), responseBody);
        log.info("response: #{}#", response);

        out.write(response.getBytes(encoding));
        out.flush();
    }

    private String prependHeader(String requestHeader, String responseBody) {
        int bodyLength = headerBodyLengthEndIndex - headerBodyLengthBeginIndex;
        String responseBodyLength = StringUtils.leftPad(String.valueOf(responseBody.length()), bodyLength, "0");

        return new StringBuilder()
                .append(requestHeader.substring(0, headerBodyLengthBeginIndex))
                .append(responseBodyLength)
                .append(requestHeader.substring(headerBodyLengthEndIndex))
                .append(responseBody)
                .toString();
    }
}
