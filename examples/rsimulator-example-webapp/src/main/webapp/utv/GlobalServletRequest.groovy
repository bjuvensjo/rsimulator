import javax.servlet.http.HttpServletRequest;

def HttpServletRequest request = vars.get("servletRequest")
def cToken = request.getHeader("Ctoken")
if (cToken) {
    def rootPath = vars.get("rootPath")
    def userRootPath = rootPath + File.separator + cToken
    vars.put("rootPath", userRootPath)
}

