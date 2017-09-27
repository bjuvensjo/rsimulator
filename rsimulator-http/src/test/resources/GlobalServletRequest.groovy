import javax.servlet.http.HttpServletRequest;

def HttpServletRequest request = vars.get("servletRequest")
def user = request.getHeader("user")
if (user) {
    def rootPath = vars.get("rootPath")
    def userRootPath = rootPath + File.separator + user
    vars.put("rootPath", userRootPath)
}

