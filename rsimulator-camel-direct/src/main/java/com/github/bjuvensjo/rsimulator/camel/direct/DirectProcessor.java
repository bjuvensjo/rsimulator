package com.github.bjuvensjo.rsimulator.camel.direct;

import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectProcessor implements Processor {
    private static final Pattern ACCEPT_PATTERN = Pattern.compile("([^;]+)");
    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("([^;]+)");
    private static final String DEFAULT = "default";
    Map<String, String> accepts;
    String endpointUri;
    DirectComponentConfig directComponentConfig;
    @Inject
    Simulator simulator;
    Map<String, String> simulatorContentTypes;


    DirectProcessor(String endpointUri, DirectComponentConfig directComponentConfig) {
        this.endpointUri = endpointUri;
        this.directComponentConfig = directComponentConfig;
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);

        simulatorContentTypes = new HashMap<>();
        simulatorContentTypes.put("application/json", "json");
        simulatorContentTypes.put("application/xml", "xml");
        simulatorContentTypes.put("application/soap+xml", "xml");
        simulatorContentTypes.put("text/xml", "xml");
        simulatorContentTypes.put("text/html", "txt");
        simulatorContentTypes.put(DEFAULT, "txt");

        accepts = new HashMap<>();
        accepts.put("application/json", "json");
        accepts.put("application/xml", "xml");
        accepts.put(DEFAULT, "txt");
    }

    public void process(Exchange exchange) {
        Map<String, Object> vars = new HashMap<>();

        String rootRelativePath = exchange.getIn().getHeader(Exchange.HTTP_URI) != null ? exchange.getIn().getHeader(Exchange.HTTP_URI, String.class).replaceFirst(".*//[^/]+", "") : endpointUri.replaceFirst("[^/]+", "");

        Optional<SimulatorResponse> simulatorResponse = simulator.service(
                directComponentConfig.getRootPath(),
                rootRelativePath,
                exchange.getIn().getBody(String.class),
                getSimulatorContentType(exchange),
                vars);

        if (!simulatorResponse.isPresent()) {
            throw new IllegalStateException("No response present in rsimulator!");
        }
        Optional<Properties> properties = simulatorResponse.get().getProperties();
        int status = 200;
        if (properties != null && properties.isPresent()) {
            String responseCode = properties.get().getProperty("responseCode");
            if (responseCode != null) {
                status = Integer.valueOf(responseCode);
            }
        }
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, status);
        String response = simulatorResponse.get().getResponse();
        exchange.getOut().setBody(response);
    }

    String getSimulatorContentType(Exchange exchange) {
        String contentType = exchange.getIn().getHeader("Content-Type", String.class);
        if (contentType != null) {
            Matcher m = CONTENT_TYPE_PATTERN.matcher(contentType);
            if (m.find()) {
                return simulatorContentTypes.get(m.group(1));
            }
        }

        String accept = exchange.getIn().getHeader("Accept", String.class);
        if (accept != null) {
            Matcher m = ACCEPT_PATTERN.matcher(accept);
            if (m.find()) {
                String[] split = m.group(1).split(" *, *");
                for (int i = 0; i < split.length; i++) {
                    if (accepts.containsKey(split[i])) {
                        return accepts.get(split[i]);
                    }
                }
            }
        }

        return simulatorContentTypes.get(DEFAULT);
    }
}
