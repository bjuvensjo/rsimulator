/**
 * Available vars are:
 *
 * "relativeRecordPath"
 * "request"
 * "response"
 * "filePrefix"
 * "basePath"
 */

import javax.servlet.http.HttpServletRequest;

def HttpServletRequest request = vars.get("request")
def user = request.getHeader("user")
if (user) {
    def basePath = vars.get("basePath")
    def userBasePath = basePath + File.separator + user
    vars.put("basePath", userBasePath)
}
vars.put("filePrefix", "test")