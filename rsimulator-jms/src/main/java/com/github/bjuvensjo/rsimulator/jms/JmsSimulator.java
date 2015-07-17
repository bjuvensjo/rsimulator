package com.github.bjuvensjo.rsimulator.jms;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.github.bjuvensjo.rsimulator.jms.config.GlobalConfig;
import com.github.bjuvensjo.rsimulator.jms.config.JmsModule;

import java.util.Optional;

/**
 * JmsSimulator.
 *
 * @author Magnus Bjuvensjö
 */
public class JmsSimulator extends RouteBuilder {
    private static final String className = JmsSimulator.class.getName();
    private String jms;
    private String queue;
    private String replyTo;
    private String simulatorContentType;
    private Decoder decoder;
    private Encoder encoder;
    private int concurrentConsumers;
    private int maxConcurrentConsumers;
    private int receiveTimeout;    

    @Inject
    private Simulator simulator;

    public JmsSimulator() {
        Injector injector = Guice.createInjector(new CoreModule(), new JmsModule());
        injector.injectMembers(this);
    }

    @Override
    public void configure() throws Exception {
        RouteDefinition definition = from(new StringBuilder()
                .append(jms)
                .append(":queue:").append(queue)
                .append("?replyTo=").append(replyTo)
                .append("&useMessageIDAsCorrelationID=true")
                .append("&concurrentConsumers=").append(concurrentConsumers)
                .append("&maxConcurrentConsumers=").append(maxConcurrentConsumers)
                .append("&receiveTimeout=").append(receiveTimeout)
                .toString());
        
        definition.to("log:" + className + "?showAll=true&multiline=true");

        if (decoder != null) {
            definition.bean(decoder);
        }

        definition.process(exchange -> {
            String request = exchange.getIn().getBody(String.class);
            Optional<SimulatorResponse> simulatorResponseOptional = simulator.service(GlobalConfig.rootPath, "", request, simulatorContentType);
            String responseBody = simulatorResponseOptional.map(SimulatorResponse::getResponse).orElse("No simulatorResponse found!");                
            exchange.getIn().setBody(responseBody);
        });

        if (encoder != null) {
            definition.bean(encoder);
        }

        definition.to("log:" + className + "?showAll=true&multiline=true");
    }

    public void setJms(String jms) {
        this.jms = jms;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public void setSimulatorContentType(String simulatorContentType) {
        this.simulatorContentType = simulatorContentType;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public void setMaxConcurrentConsumers(int maxConcurrentConsumers) {
        this.maxConcurrentConsumers = maxConcurrentConsumers;
    }

    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }
}
