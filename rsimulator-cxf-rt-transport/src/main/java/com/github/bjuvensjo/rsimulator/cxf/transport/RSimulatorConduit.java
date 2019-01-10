package com.github.bjuvensjo.rsimulator.cxf.transport;

import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.SimulatorResponse;
import org.apache.cxf.Bus;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractConduit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Conduit implementation that takes incoming requests and tries to find corresponding response using call-by-reference
 * to a local jvm simulator instance without using the http-protocol.
 */
public class RSimulatorConduit extends AbstractConduit {
    private static final Logger LOG = LogUtils.getL7dLogger(RSimulatorConduit.class);
    private Simulator simulator;
    private Bus bus;
    private final String rootPath;

    RSimulatorConduit(EndpointInfo endpointInfo, Simulator simulator, Bus bus, String rootPath) {
        super(endpointInfo.getTarget());
        this.simulator = simulator;
        this.bus = bus;
        this.rootPath = rootPath;
    }

    @Override
    public void prepare(Message message) {
        message.setContent(OutputStream.class, new CachedOutputStream());
    }

    @Override
    public void close(Message message) throws IOException {
        super.close(message);
        if (!isInbound(message)) {
            // notify caller that a response is received
            this.getMessageObserver().onMessage(createResponseMessage(message));
        }
    }

    private boolean isInbound(Message message) {
        return Boolean.TRUE.equals(message.get("org.apache.cxf.message.inbound"));
    }

    private Message createResponseMessage(Message message) throws IOException {
        Message answer = new MessageImpl();
        OutputStream os = message.getContent(OutputStream.class);
        String encoding = getEncoding(message);
        String request = toString((CachedOutputStream) os, encoding);
        Optional<SimulatorResponse> response = findResponse(getPath(message), request);
        if (response.isPresent()) {
            answer.setContent(InputStream.class, toInputStream(response.get().getResponse(), encoding));
            Optional<Properties> properties = response.get().getProperties();
            Integer responseCode = 200;
            if (properties != null && properties.isPresent() && properties.get().containsKey("responseCode")) {
                responseCode = Integer.valueOf((String) properties.get().get("responseCode"));
            }
            answer.put(Message.RESPONSE_CODE, responseCode);
            updateMessageExchange(message, answer);
        }
        return answer;
    }

    private String getEncoding(Message message) {
        return (String) message.get("org.apache.cxf.message.Message.ENCODING");
    }

    @SuppressWarnings("unchecked")
    private Optional<SimulatorResponse> findResponse(String path, String request) {
        return simulator.service(rootPath, path, request, "xml");
    }

    private InputStream toInputStream(String string, String encoding) {
        return new ByteArrayInputStream(string.getBytes(Charset.forName(encoding)));
    }

    private String toString(CachedOutputStream outputStream, String encoding) throws IOException {
        return new String(outputStream.getBytes(), encoding);
    }

    private String getPath(Message message) {
        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        return address.substring(address.indexOf('/', address.indexOf("//") + 2));
    }

    private void updateMessageExchange(Message message, Message inMessage) {
        Exchange exchange = message.getExchange();
        exchange.setInMessage(inMessage);
        exchange.put(Service.class, message.getExchange().getService());
        exchange.put(Bus.class, bus);
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}