//Get input
def server = input.get("server") ?: config.global("remedy.server")
def arapi = input.get("arapi") ?: config.global("remedy.arapi")
def port = input.get("port") ?: config.global("remedy.port")
def form = input.get("form")
def query = input.get("query")
//Create URL String
String url = arapi + "/" + server + "/" + form + "/";
if (query)
  url += query
url += "?port=" + port
//Return header
output.set("url", url)
