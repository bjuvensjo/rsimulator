import com.github.bjuvensjo.rsimulator.core.SimulatorResponse

def sum = vars.simulatorRequest.split(' *, *').collect() { String it -> it.toInteger() }.sum()

SimulatorResponse simulatorResponse = vars.simulatorResponseOptional.get()
simulatorResponse.response = "${simulatorResponse.response} = $sum"