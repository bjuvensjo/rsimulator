package com.github.bjuvensjo.rsimulator.proxy;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import com.github.bjuvensjo.rsimulator.proxy.config.ProxyModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class URIMapperTest {
    private URIMapper uriMapper;
    
    @Before
    public void init() {
        Injector injector = Guice.createInjector(new ProxyModule());
        uriMapper = injector.getInstance(URIMapper.class);
    }

	@Test
	public void testApi() {
		try {
			String expected = "http://127.0.0.1:8080/my-bank/api/account";
			String actual = null;
			
			actual = uriMapper.map("api/account");
			Assert.assertEquals(expected, actual);
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
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
