package org.rsimulator.example.unittest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.interceptor.view.InterceptorMock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

public class TestWithInterceptor {
    @Inject
    private WebServiceClient webServiceClient;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(WebServiceClient.class).to(WebServiceClientImpl.class);
                InterceptorMock interceptorMock = new InterceptorMock();
                interceptorMock.setRootPath(TestWithInterceptor.class);
                interceptorMock.setUseRootRelativePath(false);
                bindInterceptor(Matchers.subclassesOf(WebServiceClient.class), Matchers.any(), interceptorMock);
            }
        });
        webServiceClient = injector.getInstance(WebServiceClient.class);
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
