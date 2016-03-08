import groovy.json.*

log.debug "Start Remedy query"

def start = new Date().getTime()
def metaInfo = new HashMap()
def form = "CHG:Infrastructure Change"

//Validate Input

def username = input.get("username") ?: config.global("remedy.username")
def password = input.get("password") ?: config.global("remedy.password")
def server = input.get("server") ?: config.global("remedy.server")
def arapi = input.get("arapi") ?: config.global("remedy.arapi")
def port = input.get("port") ?: config.global("remedy.port")
def connectorName = input.get("connector") ?: config.global("remedy.connector")
def timeout = input.get("timeout") ?: config.global("remedy.timeout") ?: 5000
def changeID = input.get("ChangeID") ?: "1"

def query = "'Infrastructure Change ID' = \"${changeID}\""
log.debug query
// create auth string

def queryResponse = call.bit("remedy:base:query.groovy")         // Provide path for flintbit
                  .set("form",form) // Set arguments
                  .set("query", query)
                  .sync()
log.debug "Response: " + queryResponse

def runtime = new Date().getTime() - start
metaInfo.put("runtime", runtime)
output.set("meta",metaInfo)
log.debug "Exit Remedy Query"
