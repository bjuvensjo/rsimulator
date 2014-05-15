package org.simulator.jms;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
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
    // TODO Use encoding?
    private String encoding;
    private String jms;
    private String queue;
    private String replyTo;
    private String simulatorContentType;

    @Inject
    private Simulator simulator;

    public JmsSimulator() {
        Injector injector = Guice.createInjector(new CoreModule(), new JmsModule());
        injector.injectMembers(this);
    }

    @Override
    public void configure() throws Exception {
        from(jms + ":queue:" + queue + "?replyTo=" + replyTo + "&useMessageIDAsCorrelationID=true")
                .to("log:" + className + "?showAll=true&multiline=true")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String request = exchange.getIn().getBody(String.class);
                        SimulatorResponse simulatorResponse = simulator.service(GlobalConfig.rootPath, "", request, simulatorContentType);
                        exchange.getIn().setBody(simulatorResponse.getResponse());
                    }
                })
                .to("log:" + className + "?showAll=true&multiline=true");
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
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
}
