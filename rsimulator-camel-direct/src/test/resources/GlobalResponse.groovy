import com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl
import com.github.bjuvensjo.rsimulator.core.config.Constants

if (vars.exception ||vars.get(Constants.SIMULATOR_RESPONSE_OPTIONAL).isEmpty()) {
    def props = new Properties()
    props.setProperty('responseCode', '404')
    vars.simulatorResponseOptional = Optional.of(new SimulatorResponseImpl('"Not Found"', props, null))
}