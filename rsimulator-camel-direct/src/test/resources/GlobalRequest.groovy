import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl


if (!(vars.contentType in ['txt', 'json', 'xml'])) {
    def props = new Properties()
    props.setProperty('responseCode', '400')
    vars.simulatorResponseOptional = new Optional(new SimulatorResponseImpl("contentType not supported", new Optional(props), null))     
}
