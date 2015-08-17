package com.github.bjuvensjo.rsimulator.socket;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import static org.apache.commons.lang.StringUtils.*;
import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.socket.config.SocketModule;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by ei4577 on 05/03/14.
 */
public class RequestReaderTest {

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

    @Inject
    private RequestReader requestReader;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new SocketModule());
        injector.injectMembers(this);
    }

    @Test
    public void testRead() throws Exception {
        String body = "123ABC";
        String header = rightPad("", headerBodyLengthBeginIndex) + leftPad(String.valueOf(body.length()), headerBodyLengthEndIndex - headerBodyLengthBeginIndex, "0") + rightPad("", headerLength - headerBodyLengthEndIndex);

        byte[] buffer = new byte[header.length() + body.length()];
        System.arraycopy(header.getBytes(encoding), 0, buffer, 0, headerLength);
        System.arraycopy(body.getBytes(encoding), 0, buffer, headerLength, body.length());

        ByteArrayInputStream in = new ByteArrayInputStream(buffer);

        RequestReader.Request request = requestReader.read(in);

        assertEquals(header, request.getHeader());
        assertEquals(body, request.getBody());
    }
}
