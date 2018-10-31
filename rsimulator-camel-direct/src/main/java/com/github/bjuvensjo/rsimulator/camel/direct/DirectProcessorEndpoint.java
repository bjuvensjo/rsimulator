package com.github.bjuvensjo.rsimulator.camel.direct;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.impl.ProcessorEndpoint;

public class DirectProcessorEndpoint extends ProcessorEndpoint {
    private String rootPath;

    DirectProcessorEndpoint(String endpointUri, CamelContext camelContext, String rootPath) {
        super(endpointUri, camelContext, null);
        this.rootPath = rootPath;
    }

    @Override
    protected Processor createProcessor() {
        return new DirectProcessor(this.getEndpointUri(), rootPath);
    }
}
