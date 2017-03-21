import com.github.bjuvensjo.rsimulator.core.config.Constants

vars.put "a", "b"
def simulatorRequest = vars.get(Constants.SIMULATOR_REQUEST)
vars.put("simulatorResponseOptional", Optional.of(new com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl("Hello Test4, says GlobalRequest.groovy!", Optional.of(new java.util.Properties()), null)))
