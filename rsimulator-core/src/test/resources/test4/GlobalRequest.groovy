vars.put "a", "b"
def request = vars.get("request")
vars.put("controllerResponse", new org.rsimulator.core.controller.ControllerResponseImpl("Hello Test4, says GlobalRequest.groovy!", new java.util.Properties(), null))
