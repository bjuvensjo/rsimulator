package com.github.bjuvensjo.rsimulator.socket;

import com.github.bjuvensjo.rsimulator.socket.config.GlobalConfig;
import com.github.bjuvensjo.rsimulator.socket.config.SocketModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SocketSimulatorIT {

    @Inject
    @Named("encoding")
    private String encoding;

    @BeforeEach
    public void init() {
        Injector injector = Guice.createInjector(new SocketModule());
        injector.injectMembers(this);
    }

    @Disabled
    @Test
    public void test() {
        String header = "    00061234                                    ";
        String body = "123ABC";

        String expected = "123AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEE";

        try {
            Socket s = new Socket("localhost", GlobalConfig.port);

            s.getOutputStream().write((header + body).getBytes(encoding));

            InputStream in = s.getInputStream();

            byte[] headerBuffer = new byte[48];
            in.read(headerBuffer);

            String sLength = new String(headerBuffer, 4, 4, encoding);
            int length = Integer.parseInt(sLength);

            byte[] bodyBuffer = new byte[length];
            in.read(bodyBuffer);
            String responseBody = new String(bodyBuffer, encoding);

            assertEquals(expected, responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
