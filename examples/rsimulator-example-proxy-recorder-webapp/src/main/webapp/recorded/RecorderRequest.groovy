/**
 * Available vars are:
 *
 * String "relativeRecordPath"
 * RecorderServletRequestWrapper "request"
 * RecorderServletResponseWrapper "response"
 * String "filePrefix"
 * String "basePath"
 * String "requestBodyToRecord"
 *
 * See RecorderScriptVars
 */
//import com.github.bjuvensjo.rsimulator.recorder.RecorderServletRequestWrapper;
//import com.github.bjuvensjo.rsimulator.recorder.RecorderScriptVars;
//
//def RecorderServletRequestWrapper request = vars.get("request")
//def user = request.getHeader("Utoken")
//if (user) {
//    def basePath = vars.get("basePath")
//    def userBasePath = basePath + File.separator + user
//    vars.put("basePath", userBasePath)
//}
//
//def String requestBody = request.getRequestBody("UTF-8");
//def finalBody = (requestBody =~ /<ClientContext>(?s).*<\/SecurityContext>/).replaceFirst("<ClientContext><Channel>.*</Channel><IP>.*</IP><Reference>(.*)</Reference></ClientContext><SecurityContext><Ticket>.*</Ticket><CallInfo>.*</CallInfo></SecurityContext>")
//vars.put(RecorderScriptVars.REQUEST_BODY_TO_RECORD, finalBody);