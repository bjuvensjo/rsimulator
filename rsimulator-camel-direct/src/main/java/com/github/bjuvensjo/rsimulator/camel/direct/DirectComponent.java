package com.github.bjuvensjo.rsimulator.camel.direct;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class DirectComponent extends DefaultComponent {
    private DirectComponentConfig directComponentConfig;

    public DirectComponent() {
        this("./src/test/resources");
    }

    public DirectComponent(String rootPath) {
        this.directComponentConfig = new DirectComponentConfig(rootPath);
    }

    public void setRootPath(String rootPath) {
        directComponentConfig.setRootPath(rootPath);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
        return new DirectProcessorEndpoint(remaining, this.getCamelContext(), directComponentConfig);
    }

    @Override
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        
    }
}
