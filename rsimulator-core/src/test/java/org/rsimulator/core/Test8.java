package org.rsimulator.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.CoreModule;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test7.
 *
 * @author Magnus Bjuvensjö
 */
public class Test8 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test8").getPath();
        String rootRelativePath = "/.";
		String request = "query=1234&another='dododo'";
        String contentType = "json";
        try {
            SimulatorResponse simulatorResponse = simulator.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("{\"result\":\"ok\"}", simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
