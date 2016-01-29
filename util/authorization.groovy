//Get input
String username = input.get("username")
String password = input.get("password")
//Create Auth String
String authString = username + ":" + password;
def authorization = authString.bytes.encodeBase64()
def header_authorization = "Authorization: Basic " + authorization
//Return header
output.set("header_authorization", header_authorization)
