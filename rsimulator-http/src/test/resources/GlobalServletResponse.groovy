import org.rsimulator.core.SimulatorResponse
import javax.servlet.http.HttpServletResponse

def SimulatorResponse simulatorResponse = vars.get("simulatorResponse")
def Properties properties = simulatorResponse.getProperties()
def HttpServletResponse response = vars.get("response")
if (properties != null) {
    /*if (properties.get("header.Error-Code")) {
        response.setHeader("Error-Code", properties.get("header.Error-Code"))
    }
    if (properties.get("header.Error-Message")) {
        response.setHeader("Error-Message", properties.get("header.Error-Message"))
    }
    */
}

