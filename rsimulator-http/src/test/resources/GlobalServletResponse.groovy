import com.github.bjuvensjo.rsimulator.core.SimulatorResponse
import javax.servlet.http.HttpServletResponse

def SimulatorResponse simulatorResponse = vars.get("simulatorResponse")
def Properties properties = simulatorResponse.getProperties().orElse(null);

def HttpServletResponse response = vars.get("servletResponse")
if (properties != null) {
    if (properties.getProperty("header.Error-Code")) {
        response.setHeader("Error-Code", properties.getProperty("header.Error-Code"))
    }
    if (properties.getProperty("header.Error-Message")) {
        response.setHeader("Error-Message", properties.getProperty("header.Error-Message"))
    }
}