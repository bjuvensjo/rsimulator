package org.simulator.jms;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.rsimulator.core.Simulator;
import org.rsimulator.core.SimulatorResponse;
import org.rsimulator.core.config.CoreModule;
import org.simulator.jms.config.GlobalConfig;
import org.simulator.jms.config.JmsModule;

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

    @Inject
    private Simulator simulator;

    public JmsSimulator() {
        Injector injector = Guice.createInjector(new CoreModule(), new JmsModule());
        injector.injectMembers(this);
    }

    @Override
    public void configure() throws Exception {
        RouteDefinition definition = from(jms + ":queue:" + queue + "?replyTo=" + replyTo + "&useMessageIDAsCorrelationID=true");

        definition.to("log:" + className + "?showAll=true&multiline=true");

        if (decoder != null) {
            definition.bean(decoder);
        }

        definition.process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                String request = exchange.getIn().getBody(String.class);
                SimulatorResponse simulatorResponse = simulator.service(GlobalConfig.rootPath, "", request, simulatorContentType);
                exchange.getIn().setBody(simulatorResponse.getResponse());
            }
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
}
