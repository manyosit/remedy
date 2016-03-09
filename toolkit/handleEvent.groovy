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
def myEvent = input.get("event")

def writeData = { recordData ->
  call.bit("remedy:base:update.groovy")         // Provide path for flintbit
      .set("form", form) // Set arguments
      .set("data", JsonOutput.toJson(recordData))
      .sync()
}

//Update Event Record
def recordDataSet = ["ExecutionLog" : "Event execution started.", "Status" : "InProgress"]
def completeRecord = ["${myEvent.RequestId}" : recordDataSet]

writeData(completeRecord)

//Execute Event
try {
  log.debug "Call Flintbit " + myEvent.ExecutionObject
  //throw new IllegalArgumentException( "Kein Alter <= 0 erlaubt!" )
  call.bit(myEvent.ExecutionObject)
      .sync()
} catch (Exception e) {
  log.error "Error handling event " + myEvent + " -> " + e
  def errorRecord = ["${myEvent.RequestId}" : ["ExecutionLog" : "Event error: " + e, "Status" : "Error"]]
  writeData(errorRecord)
}

def completedRecord = ["${myEvent.RequestId}" : ["ExecutionLog" : "Event execution completed", "Status" : "Completed"]]

writeData(completedRecord)

def runtime = new Date().getTime() - start
metaInfo.put("size", size)
metaInfo.put("runtime", runtime)
metaInfo.put("status", status)
metaInfo.put("message", statusMessage)
output.set("meta",metaInfo)
log.debug "Exit Remedy Query"
