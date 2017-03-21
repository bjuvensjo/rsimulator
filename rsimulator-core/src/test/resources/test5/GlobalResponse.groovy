import com.github.bjuvensjo.rsimulator.core.config.Constants

def simulatorResponseOptional = vars.get(Constants.SIMULATOR_RESPONSE_OPTIONAL)
simulatorResponseOptional.get().response = "Hello Test5, says GlobalResponse.groovy!"