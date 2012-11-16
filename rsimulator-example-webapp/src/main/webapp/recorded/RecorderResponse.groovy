/**
 * See SimulatorProperties, a helper for creating simulator properties
 */

import org.rsimulator.proxy.recorder.SimulatorProperties

def relativeRecordPath = vars.get("relativeRecordPath")
def basePath = vars.get("basePath")
def filePrefix = vars.get("filePrefix")
//SimulatorProperties simulatorProperties = new SimulatorProperties(basePath, relativeRecordPath, filePrefix);
//simulatorProperties.set("responseCode", "400")