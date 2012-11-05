package org.rsimulator.proxy;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

public class URIMapperTest {

	@Test
	public void testApi() {
		try {
			URIMapper uriMapper = new URIMapper();
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
			URIMapper uriMapper = new URIMapper();
			String uri = "x/y/z";
			String expected = "http://127.0.0.1:8080/my-bank/" + uri;
			String actual = uriMapper.map(uri);
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
