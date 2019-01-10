# rsimulator-cxf-rt-transport

Custom transport for cxf that enables a cxf client to invoke the rsimulator by reference and hence avoiding the usage of a port.

## Usage

    // create factory that handles http:// protocol, there is an optional argument to the factory
    // that allows to set other protocols, could be valuable if there is a need to use real http calls
    // for some services and simulated ones for this, then for example rs:// could be used as protocol
    // for services that should be mocked
    RSimulatorTransportFactory rSimulatorTransportFactory = new RSimulatorTransportFactory(getRootPath())
    // get cxf bus
    Bus bus = BusFactory.getThreadDefaultBus()
    ConduitInitiatorManager extension = bus.getExtension(ConduitInitiatorManager.class)
    HTTPTransportFactory.DEFAULT_NAMESPACES.each {
        // overwrite all existing ones
        extension.registerConduitInitiator(it, rSimulatorTransportFactory)
    }
    
After this setup it is possible to call the web service normally and just provided the protocol for which the RSimulator has been configured. In the case above http:// should be used. See `com.github.bjuvensjo.rsimulator.cxf.transport.ExampleIT.groovy` for a complete example.
