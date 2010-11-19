package org.rsimulator.example.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.rsimulator.http.config.HttpSimulatorConfig.config;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class WebServiceClientTest {
    private WebServiceClient webServiceClient;

    @Before
    public void init() {
        webServiceClient = new WebServiceClientImpl();
        try {
            config(getClass());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGreet() {
        try {
            String greeting = webServiceClient.greet(new Greeting("WebServiceClientTest", "rsimulator-http", "Hello"));
            assertEquals("Goodbye!", greeting);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
