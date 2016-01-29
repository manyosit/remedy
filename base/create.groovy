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
                      .set("query",query)
                      .sync()

def url = uriResponse.url

//create Body

//call connector
def wResponse=call.connector(connectorName)
         .set("method","post")
         .set("body", recordData)
         .set("url",url)
         .set("timeout",timeout)
         .set("headers", header_authorization, "Accept: */*")
         .sync()

if (wResponse.exitcode != 0) {
  def message = "Remedy connection falied"
  log.error message
  metaInfo.put("status", "error")
  metaInfo.put("message", message)
} else {
  metaInfo.put("status", "success")
  metaInfo.put("message", "")

  log.info ""+wResponse

  def myBody = wResponse.body

  def jsonSlurper = new JsonSlurper()
  def myJSON = jsonSlurper.parseText(myBody)
  output.set("remedy",myJSON)
  //Loop through all records
  myJSON.keySet().each {
    def myRecord = myJSON.get(it)
    log.info "" + myRecord.keySet()
    log.info myRecord.Description
  }
}
//Create entry
/*
def myRecord = myJSON.get("000000000000001")
log.info "" + myRecord.Description

myJSON.each {
  //def myEntry = jsonSlurper.parseText(it)
  //log.info myEntry
  log.info it.getClass().getSimpleName()
  log.info it.getClass().getName()
  log.info "" + it
}

wResponse=call.connector("OpenWheather")
         .set("method","post")
         .set("body", "{ \"refid01\": { \"Description\": \"Hi there\" } }")
         .set("url",url)
         .set("timeout",5000)*/
         //.set("headers", "Authorization: Basic cmhhbm5lbWFubjpwYXNzd29yZA==", "Accept: */*")
         /*.sync()

         myBody = wResponse.body

         log.info("Remedy " + myBody)

/*
log.info("Humidity" + myJSON.main.humidity)

log.info "" + myJSON.weather
log.info "" + myJSON.main
log.info "" + myJSON.main.temp

output.set("Wetter", "${myJSON.weather.main}")
output.set("Temp", myJSON.main.temp.toString())
*/
log.info "done"
def runtime = new Date().getTime() - start
metaInfo.put("runtime", runtime)
output.set("meta",metaInfo)