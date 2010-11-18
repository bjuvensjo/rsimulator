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
 * Test1.
 *
 * @author Magnus Bjuvensjö
 */
public class Test1 {
    private Controller controller;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new DIModule());
        controller = injector.getInstance(Controller.class);
    }

    @Test
    public void test() {
        String rootPath = "src/test/resources";
        String rootRelativePath = ".";
        String request = "Hello Controller, says Test1!";
        String contentType = "txt";
        try {
            ControllerResponse controllerResponse = controller
                    .service(rootPath, rootRelativePath, request, contentType);
            controller.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("Hello Test1, says Controller!", controllerResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
