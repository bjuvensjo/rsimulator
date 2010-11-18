package org.rsimulator.core.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.DIModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test5.
 *
 * @author Magnus Bjuvensjö
 */
public class Test5 {
    private Controller controller;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new DIModule());
        controller = injector.getInstance(Controller.class);
    }

    @Test
    public void test() {
        String rootPath = "src/test/resources/test5";
        String rootRelativePath = ".";
        String request = "Hello Controller, says Test5!";
        String contentType = "txt";
        try {
            ControllerResponse controllerResponse = controller
                    .service(rootPath, rootRelativePath, request, contentType);
            controller.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("Hello Test5, says GlobalResponse.groovy!", controllerResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
