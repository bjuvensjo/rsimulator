package com.github.bjuvensjo.rsimulator.core

import com.github.bjuvensjo.rsimulator.core.config.CoreModule
import com.github.bjuvensjo.rsimulator.core.util.FileUtils
import com.github.bjuvensjo.rsimulator.core.util.Props
import com.google.inject.Guice
import com.google.inject.Inject
import com.google.inject.Injector
import org.junit.Before
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class Test11 {
    Simulator simulator

    @Before
    void init() {
        Injector injector = Guice.createInjector(new CoreModule() {
            protected Map<String, Handler> getHandlerMap() {
                [http: new HttpHandler()]
            }
        })
        simulator = injector.getInstance(Simulator.class)
    }

    @Test
    void testHttp() throws URISyntaxException {
        String request = '''GET /service/rest/v1/assets?repository=PESCPOC HTTP/1.1
Host: 10.252.224.240:8081
Authorization: Basic abc
Accept: application/json
User-Agent: curl
''';
        SimulatorResponse simulatorResponse = simulator.service(
            Paths.get(getClass().getResource("/test11").toURI()).toString(),
            File.separator, 
            request, 
            'http'
        ).get();
        assertTrue(simulatorResponse.response.contains('HTTP/1.1 200 OK'))
        assertEquals('value', simulatorResponse.properties.get().getProperty('key'))
    }
    
    class HttpHandler implements Handler {
        @Inject
        FileUtils fileUtils
        @Inject
        Props props

        @Override
        Optional<SimulatorResponse> findMatch(String rootPath, String rootRelativePath, String request) {
            String matchingRequest = 'src/test/resources/test11/TestRequest.http' 
            Optional.of(new SimulatorResponseImpl(
                fileUtils.read(Paths.get(matchingRequest.replace('Request', 'Response'))),
                props.getProperties(Paths.get(matchingRequest.replace('Request.http', '.properties'))),
                Paths.get(matchingRequest)
            ))
        }
    }
}
