import groovy.json.*

log.debug "Start Remedy query"

def start = new Date().getTime()
def metaInfo = new HashMap()
def form = "MTK:AutomationEvents"
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
def maxEntries = input.get("maxEntries") ?: config.global("remedy.toolkit.maxEntries") ?: 60
def query = "'Status' = \"New\""

// query remedy
def queryResponse = call.bit("remedy:base:query.groovy")
                  .set("form",form) // Set arguments
                  .set("query", query)
                  .set("maxEntries", maxEntries)
                  .sync()

if (queryResponse == null || queryResponse.data == null || queryResponse.meta.status == "error") {
  log.error "Something went wrong. Query for Change returned: " + queryResponse
  status = "error"
  size = 0
  if (queryResponse != null && queryResponse.meta != null && queryResponse.meta.message && queryResponse.meta.message != "") {
    statusMessage = queryResponse.meta.message
  }
} else {
  size = queryResponse.data.size()
  status = "success"
  if (queryResponse.meta.size < 1)
    statusMessage = "Nothing to do. Maybe next time."
  //loop through events, call handler async
  queryResponse.data.keySet().each { myEventKey ->
    def myEvent = queryResponse.data.get(myEventKey)
    try {
      def eventResponse = call.bit("remedy:toolkit:handleEvent.groovy")
                        .set("event", myEvent)
                        .async()
    } catch (Exception e) {
      log.error "Error handling event " + myEvent + " -> " + e
    }
  }

  //output.set("data", data)
}

def runtime = new Date().getTime() - start
metaInfo.put("size", size)
metaInfo.put("runtime", runtime)
metaInfo.put("status", status)
metaInfo.put("message", statusMessage)
output.set("meta",metaInfo)
log.debug "Exit Remedy Query"
