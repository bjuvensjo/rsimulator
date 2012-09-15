package org.rsimulator.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.rsimulator.core.config.CoreModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test7.
 *
 * @author Magnus Bjuvensjö
 */
public class Test7 {
    private Simulator simulator;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new CoreModule());
        simulator = injector.getInstance(Simulator.class);
    }

    @Test
    public void test() {
        String rootPath = getClass().getResource("/test7").getPath();
        String rootRelativePath = "/.";
		String request = "[{\"id\": \"12345678901\",\"name\":\"Privatkonto\",\"balance\":34251.15766987801},{\"id\":\"23456789012\",\"name\":\"Lånekonto\",\"balance\":874.8714758855125},{\"id\":\"34567890123\",\"name\":\"Räkningskonto\",\"balance\":18935.745973888184},{\"id\":\"45678901234\",\"name\":\"Semesterkonto\",\"balance\":26763.710168201727},{\"id\":\"56789012345\",\"name\":\"Sparkonto\",\"balance\":281434.8179749503}]";
		//String request = "{\"id\":1}";
        String contentType = "json";
        try {
            SimulatorResponse simulatorResponse = simulator
                    .service(rootPath, rootRelativePath, request, contentType);
            simulator.service(rootPath, rootRelativePath, request, contentType);
            assertEquals("{\"result\":\"ok\"}", simulatorResponse.getResponse());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
