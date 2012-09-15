package org.rsimulator.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.http.config.HttpSimulatorConfig;

public class HttpSimulatorTest {
	private static final int BUFFER_SIZE = 500;
	private static final int READ_TIMEOUT = 12000;
	private static final String ENCODING = "UTF-8";

	@Before
	public void init() {
		try {
			String rootPath = new File(getClass().getResource("/").getPath()).getPath();
			HttpSimulatorConfig.config(rootPath, true);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Test
	public void testTxtWithUseRootRelativeURL() {
		HttpURLConnection con = getConnection("POST",
				"http://localhost:8080/txt-examples", "text/plain");
		try {
			con.getOutputStream().write(
					"Hello Simulator, says testTxtWithUseRootRelativeURL!"
							.getBytes(ENCODING));
			String response = read(con.getInputStream());
			assertEquals(
					"Hello testTxtWithUseRootRelativeURL, says Simulator!",
					response);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testTxtWithoutUseRootRelativeURL() {
		HttpURLConnection con = getConnection("POST", "http://localhost:8080",
				"text/plain");
		try {
			con.getOutputStream().write(
					"Hello Simulator, says testTxtWithoutUseRootRelativeURL!"
							.getBytes(ENCODING));
			String response = read(con.getInputStream());
			assertEquals(
					"Hello testTxtWithoutUseRootRelativeURL, says Simulator!",
					response);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testTxtResponseCodeAndDelay() {
		HttpURLConnection con = getConnection("POST", "http://localhost:8080",
				"text/plain");
		try {
			con.getOutputStream().write("Bad request...".getBytes(ENCODING));
			assertEquals("response code", 500, con.getResponseCode());
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testXml() {
		HttpURLConnection con = getConnection("POST", "http://localhost:8080",
				"application/soap+xml");
		try {
			con.getOutputStream()
					.write(("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
							+ "xmlns:hel=\"http://www.rsimulator.org/SayHello/\"><soapenv:Header/><soapenv:Body>"
							+ "<hel:SayHelloRequest><from>SoapUI</from><to>rsimulator-http-example</to><greeting>"
							+ "hello</greeting></hel:SayHelloRequest></soapenv:Body></soapenv:Envelope>")
							.getBytes(ENCODING));
			String response = read(con.getInputStream());
			assertTrue(response.indexOf("rsimulator-http-example") != -1);
			assertTrue(response.indexOf("SoapUI") != -1);
			assertTrue(response.indexOf("good bye") != -1);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testJsonDelete() {
		HttpURLConnection con = getConnection("DELETE",
				"http://localhost:8080/json-examples/account/1",
				"application/json");
		try {
			String response = read(con.getInputStream());
			assertTrue(response.indexOf("id1") != -1);
			assertTrue(response.indexOf("name1") != -1);
			assertTrue(response.indexOf("874") != -1);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}	
	
	@Test
	public void testJsonGet() {
		HttpURLConnection con = getConnection("GET",
				"http://localhost:8080/json-examples/account",
				"application/json");
		try {
			String response = read(con.getInputStream());
			assertTrue(response.indexOf("id") != -1);
			assertTrue(response.indexOf("balance") != -1);
			assertTrue(response.indexOf("874.8714758855125") != -1);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}

	@Test
	public void testJsonPost() {
		HttpURLConnection con = getConnection("POST", "http://localhost:8080/json-examples/account",
				"application/json");
		try {
			String request = "{\"id\": \"1234567890\", \"name\": \"name\", \"balance\": 1}";
			con.getOutputStream().write(request.getBytes(ENCODING));
			String response = read(con.getInputStream());
			assertTrue(response.indexOf("1234567890") != -1);
			assertTrue(response.indexOf("name") != -1);
			assertTrue(response.indexOf("1") != -1);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}
	
	@Test
	public void testJsonPut() {
		HttpURLConnection con = getConnection("PUT", "http://localhost:8080/json-examples/account/1",
				"application/json");
		try {
			String request = "{\"id\": \"1234567890\", \"name\": \"name\", \"balance\": 1}";
			con.getOutputStream().write(request.getBytes(ENCODING));
			String response = read(con.getInputStream());
			assertTrue(response.indexOf("1234567890") != -1);
			assertTrue(response.indexOf("name") != -1);
			assertTrue(response.indexOf("1") != -1);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			con.disconnect();
		}
	}	

	private HttpURLConnection getConnection(String method, String url,
			String contentType) {
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod(method);
			con.setRequestProperty("Content-Type", contentType);
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setReadTimeout(READ_TIMEOUT);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return con;
	}

	private String read(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[BUFFER_SIZE];
		int n;
		while ((n = is.read(buffer)) > 0) {
			sb.append(new String(buffer, 0, n, ENCODING));
		}
		return sb.toString();
	}
}
