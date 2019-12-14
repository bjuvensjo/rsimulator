package com.github.bjuvensjo.rsimulator.proxy;

import com.github.bjuvensjo.rsimulator.proxy.config.ProxyModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class URIMapperTest {
    private URIMapper uriMapper;

    @BeforeEach
    public void init() {
        Injector injector = Guice.createInjector(new ProxyModule());
        uriMapper = injector.getInstance(URIMapper.class);
    }

    @Test
    public void testApi() {
        try {
            String expected = "http://127.0.0.1:8080/my-bank/api/account";
            String actual;

            actual = uriMapper.map("api/account");
            assertEquals(expected, actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDn() {
        try {
            String uri = "x/y/z";
            String expected = "http://127.0.0.1:8080/my-bank/" + uri;
            String actual = uriMapper.map(uri);
            assertEquals(expected, actual);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
