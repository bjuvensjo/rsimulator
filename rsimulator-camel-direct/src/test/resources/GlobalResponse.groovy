import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl

if (vars.exception) {
    def props = new Properties()
    props.setProperty('responseCode', '404')
    vars.simulatorResponseOptional = new Optional(new SimulatorResponseImpl('"Not Found"', new Optional(props), null))
}                    