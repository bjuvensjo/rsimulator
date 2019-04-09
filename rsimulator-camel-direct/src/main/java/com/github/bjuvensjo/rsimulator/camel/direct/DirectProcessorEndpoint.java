package com.github.bjuvensjo.rsimulator.camel.direct;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.impl.ProcessorEndpoint;

public class DirectProcessorEndpoint extends ProcessorEndpoint {
    private DirectComponentConfig directComponentConfig;

    DirectProcessorEndpoint(String endpointUri, CamelContext camelContext, DirectComponentConfig directComponentConfig) {
        super(endpointUri, camelContext, null);
        this.directComponentConfig = directComponentConfig;
    }

    @Override
    protected Processor createProcessor() {
        return new DirectProcessor(this.getEndpointUri(), directComponentConfig);
    }
}
