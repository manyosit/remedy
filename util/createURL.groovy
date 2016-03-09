//Get input
def server = input.get("server") ?: config.global("remedy.server")
def arapi = input.get("arapi") ?: config.global("remedy.arapi")
def port = input.get("port") ?: config.global("remedy.port")
def form = input.get("form")
def query = input.get("query")
def maxEntries = input.get("maxEntries") ?: 0
//Create URL String

String url = arapi + "/" + server + "/" + form + "/";
if (query)
  url += query
url += "?port=" + port

//handle maxEntries
if (query && maxEntries && maxEntries >0)
  url += "&maxEntries=" + maxEntries

log.debug "Generated URL: " + url
//Return header
output.set("url", url)
