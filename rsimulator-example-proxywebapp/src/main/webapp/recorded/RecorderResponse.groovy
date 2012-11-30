/**
 * See SimulatorProperties, a helper for creating simulator properties
 */

import org.rsimulator.proxy.recorder.SimulatorProperties
import javax.servlet.http.HttpServletResponse
import org.rsimulator.proxy.recorder.RecorderServletResponseWrapper

def relativeRecordPath = vars.get("relativeRecordPath")
def basePath = vars.get("basePath")
def filePrefix = vars.get("filePrefix")
def RecorderServletResponseWrapper response = vars.get("response")
def responseCode = response.getStatus();
if (responseCode != 200) {
    SimulatorProperties simulatorProperties = new SimulatorProperties(basePath, relativeRecordPath, filePrefix);
    simulatorProperties.set("responseCode", responseCode)
}