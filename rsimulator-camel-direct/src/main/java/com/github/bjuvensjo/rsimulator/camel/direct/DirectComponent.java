package com.github.bjuvensjo.rsimulator.camel.direct;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class DirectComponent extends DefaultComponent {
    private String rootPath;

    public DirectComponent() {
        this("./src/test/resources");
    }

    public DirectComponent(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        return new DirectProcessorEndpoint(remaining, this.getCamelContext(), rootPath);
    }
}
