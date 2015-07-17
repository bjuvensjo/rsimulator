vars.put "a", "b"
def request = vars.get("request")
vars.put("simulatorResponseOptional", Optional.of(new com.github.bjuvensjo.rsimulator.core.SimulatorResponseImpl("Hello Test4, says GlobalRequest.groovy!", Optional.of(new java.util.Properties()), null)))
