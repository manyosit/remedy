import groovy.json.*

log.debug "Start Remedy query"

def start = new Date().getTime()
def metaInfo = new HashMap()
def form = "HPD:WorkLog"
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
Boolean getTasks = input.get("getTasks")?.equalsIgnoreCase("true") ?: config.global("remedy.change.getTasks")?.equalsIgnoreCase("true") ?: false
def query = ""

//Check if query by InstanceID or by ChangeID
if (input.get("InstanceId")) {
  query = "'InstanceId' = \"" + input.get("InstanceId") + "\""
} else {
  def incidentID = input.get("IncidentId") ?: "1"
  query = "'Incident Number' = \"${incidentID}\""
}

log.debug "Get Tasks: " + getTasks

// query remedy
def queryResponse = call.bit("remedy:base:query.groovy")
                        .set("server",server) // Set arguments
                        .set("arapi",arapi)
                        .set("port",port)
                        .set("username",username)
                        .set("password",password)
                        .set("timeout",timeout)
                        .set("connector", connectorName)
                        .set("form",form) // Set arguments
                        .set("query", query)
                        .sync()

if (queryResponse == null || queryResponse.data == null || queryResponse.meta.status == "error" || queryResponse.meta.size < 1) {
  log.error "Something went wrong. Query for Incident returned: " + queryResponse
  status = "error"
  size = 0
  if (queryResponse != null && queryResponse.meta != null && queryResponse.meta.message && queryResponse.meta.message != "") {
    statusMessage = queryResponse.meta.message
  }
} else {
  size = queryResponse.data.size()
  status = "success"
  //check for multiple responses
  def worklogs = new ArrayList()
  queryResponse.data.keySet().each {
    worklogs.add(queryResponse.data.get(it))
  }

  data = new HashMap()
  data.put("worklogs", worklogs)
  output.set("data", data)

}

def runtime = new Date().getTime() - start
metaInfo.put("size", size)
metaInfo.put("runtime", runtime)
metaInfo.put("status", status)
metaInfo.put("message", statusMessage)
output.set("meta",metaInfo)
log.debug "Exit Remedy Query"
