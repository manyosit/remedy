import groovy.json.*

log.debug "Start Remedy query"

def start = new Date().getTime()
def metaInfo = new HashMap()
def form = "TMS:Task"
def status = "error"
def statusMessage = ""
def size = 0

//Validate Input

def username = input.get("username") ?: config.global("remedy.username")
def password = input.get("password") ?: config.global("remedy.password")
def server = input.get("server") ?: config.global("remedy.server")
def arapi = input.get("arapi") ?: config.global("remedy.arapi")
def port = input.get("port") ?: config.global("remedy.port")
def connectorName = input.get("connector") ?: config.global("remedy.connector")
def timeout = input.get("timeout") ?: config.global("remedy.timeout") ?: 5000
def rootRequestInstanceId = input.get("RootRequestInstanceID") ?: "1"

def query = "'RootRequestInstanceID' = \"${rootRequestInstanceId}\""
log.debug query
// create auth string

def queryResponse = call.bit("remedy:base:query.groovy")         // Provide path for flintbit
                  .set("form",form) // Set arguments
                  .set("query", query)
                  .sync()

if (queryResponse == null || queryResponse.data == null || queryResponse.meta.status == "error") {
  log.error "Something went wrong: " + queryResponse
  status = "error"
  size = 0
  if (queryResponse != null && queryResponse.meta != null && queryResponse.meta.message && queryResponse.meta.message != "") {
    statusMessage = queryResponse.meta.message
  }
} else {
  size = queryResponse.data.size()
  status = "success"

  def tasks = new ArrayList()
  queryResponse.data.keySet().each {
    tasks.add(queryResponse.data.get(it))
  }

  data = new HashMap()
  data.put("tasks", tasks)
  output.set("data", data)
}

def runtime = new Date().getTime() - start
metaInfo.put("size", size)
metaInfo.put("runtime", runtime)
metaInfo.put("status", status)
metaInfo.put("message", statusMessage)
output.set("meta",metaInfo)
log.debug "Exit Remedy Query"
