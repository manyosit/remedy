import groovy.json.*

def start = new Date().getTime()
def metaInfo = new HashMap()

log.info("Start Remedy...")
//Validate Input

def username = input.get("username") ?: config.global("remedy.username")
def password = input.get("password") ?: config.global("remedy.password")
def server = input.get("server") ?: config.global("remedy.server")
def arapi = input.get("arapi") ?: config.global("remedy.arapi")
def port = input.get("port") ?: config.global("remedy.port")
def connectorName = input.get("connector") ?: config.global("remedy.connector")
def timeout = input.get("timeout") ?: config.global("remedy.timeout") ?: 5000
def form = input.get("form")
def recordData = input.get("data")


// create auth string
def authResponse = call.bit("remedy:util:authorization.groovy")         // Provide path for flintbit
                      .set("username",username) // Set arguments
                      .set("password",password)
                      .sync()                               // To call flintbit asynchronously use .async() instead of .sync()

def header_authorization = authResponse.header_authorization

//create arapi URL
def uriResponse = call.bit("remedy:util:createURL.groovy")         // Provide path for flintbit
                      .set("server",server) // Set arguments
                      .set("arapi",arapi)
                      .set("port",port)
                      .set("form",form)
                      .sync()

def url = uriResponse.url
log.info recordData
//call connector
def wResponse=call.connector(connectorName)
         .set("method","post")
         .set("body", recordData)
         .set("url",url)
         .set("timeout",timeout)
         .set("headers", header_authorization, "Accept: */*")
         .sync()
//Handle response
if (wResponse.body == null) {
  def message = "Remedy connection falied"
  log.error message
  metaInfo.put("status", "error")
  metaInfo.put("message", message)
} else {
  metaInfo.put("status", "success")
  metaInfo.put("message", "")

  def myBody = wResponse.body

  def jsonSlurper = new JsonSlurper()
  def myJSON = jsonSlurper.parseText(myBody)
  output.set("data",myJSON)
  metaInfo.put("size", myJSON.size())
}

log.info "done"
def runtime = new Date().getTime() - start
metaInfo.put("runtime", runtime)
output.set("meta",metaInfo)
