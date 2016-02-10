---
title: Update
taxonomy:
    category:
        - docs
---

## Flintbit

**remedy:base:update.groovy**

## Request format

	{  
	   "RequestID":{  
	      "<fieldname>":"<value>",
	      "<fieldid>":"<value>"
	   },
	   "000000000000020":{
	      "Description":"New Record",
          "Status":"New"
	   }
	}
    
    
## Call update flintbit

	//Update some data
	def newRecordData = "{ \"MyRefId01\": { \"Description\": \"New Description for not existing record\" },  \"000000000000020\": { \"Description\": \"New Description for existing record\" },}"
	def updateResponse = call.bit("remedy:base:update.groovy")         // Provide path for flintbit
	                      .set("form","flint") // Set arguments
	                      .set("data", newRecordData)
	                      .sync()
						  
	log.info "updateResponse: " + updateResponse

## flintbit Response

	{  
	   "meta":{  
	      "runtime":554,
	      "size":2,
	      "message":"",
	      "status":"success"
	   },
	   "data":{  
	      "000000000000020":"success",
	      "MyRefId01":"ERROR (302): Entry does not exist in database; "
	   }
	}
