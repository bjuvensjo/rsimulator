/**
 * See SimulatorProperties, a helper for creating simulator properties
 *
 * Available vars are:
 *
 * String "relativeRecordPath"
 * RecorderServletRequestWrapper "request"
 * RecorderServletResponseWrapper "response"
 * String "filePrefix"
 * String "basePath"
 * String "responseBodyToRecord"
 *
 * See RecorderScriptVars
 */

//import com.github.bjuvensjo.rsimulator.recorder.SimulatorProperties
//import com.github.bjuvensjo.rsimulator.recorder.RecorderServletResponseWrapper
//import com.github.bjuvensjo.rsimulator.recorder.RecorderScriptVars
//
//def relativeRecordPath = vars.get("relativeRecordPath")
//def basePath = vars.get("basePath")
//def filePrefix = vars.get("filePrefix")
//def RecorderServletResponseWrapper response = vars.get("response")
//def responseCode = response.getStatus();
//if (responseCode != 200) {
//    SimulatorProperties simulatorProperties = new SimulatorProperties(basePath, relativeRecordPath, filePrefix);
//    simulatorProperties.set("responseCode", responseCode)
//}
//
//def String responseBody = response.getResponseAsString("UTF-8")
//def String finalBody = responseBody.replaceFirst("<ns:Id>.*</ns:Id>", "<ns:Id>\\\${1}</ns:Id>")
//vars.put(RecorderScriptVars.RESPONSE_BODY_TO_RECORD, finalBody);