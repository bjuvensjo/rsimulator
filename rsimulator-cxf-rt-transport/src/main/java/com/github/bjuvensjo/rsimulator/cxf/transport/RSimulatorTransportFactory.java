package com.github.bjuvensjo.rsimulator.cxf.transport;

import com.github.bjuvensjo.rsimulator.core.Simulator;
import com.github.bjuvensjo.rsimulator.core.config.CoreModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.cxf.Bus;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.transport.AbstractTransportFactory;
import org.apache.cxf.transport.Conduit;
import org.apache.cxf.transport.ConduitInitiator;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.ws.addressing.EndpointReferenceType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom transport factory for CXF that provides Conduits which uses RSimulator CoreModule to match outgoing requests
 * with responses.
 * <p>
 * The purpose is to be able to use the simulator easily and efficient without the requirement of using the http-protocol.
 */
public class RSimulatorTransportFactory extends AbstractTransportFactory implements ConduitInitiator {
    @Inject
    private Simulator simulator;

    private final Set<String> uriPrefixes;
    private final String rootPath;

    /**
     * Minimal constructor that registers itself as a factory for the "http://"-protocol.
     *
     * @param rootPath absolute path where the simulator should use as root when trying to find response
     */
    public RSimulatorTransportFactory(String rootPath) {
        this(rootPath, Collections.singletonList("http://"));
    }

    /**
     * Constructor that accepts a list of protocols and a rootPath.
     *
     * @param rootPath  absolute path where the simulator should use as root when trying to find response
     * @param protocols list of protocols for which this transport factory should be used
     */
    public RSimulatorTransportFactory(String rootPath, List<String> protocols) {
        super(HTTPTransportFactory.DEFAULT_NAMESPACES);
        Injector injector = Guice.createInjector(new CoreModule());
        injector.injectMembers(this);
        this.rootPath = rootPath;
        this.uriPrefixes = Collections.unmodifiableSet(new HashSet<>(protocols));
    }

    @Override
    public Conduit getConduit(EndpointInfo endpointInfo, Bus bus) {
        return this.getConduit(endpointInfo, endpointInfo.getTarget(), bus);
    }

    @Override
    public Conduit getConduit(EndpointInfo endpointInfo, EndpointReferenceType endpointReferenceType, Bus bus) {
        return new RSimulatorConduit(endpointInfo, simulator, bus, rootPath);
    }

    @Override
    public Set<String> getUriPrefixes() {
        return uriPrefixes;
    }
}
