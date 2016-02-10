---
title: Create
taxonomy:
    category:
        - docs
---

## Flintbit

**remedy:base:create.groovy**

## Request format

	{  
	   "<referenceID>":{  
	      "<fieldname>":"<value>",
	      "<fieldid>":"<value>"
	   },
	   "MyRefId01":{
	      "Description":"New Record",
          "Status":"New"
	   }
	}
    
>>>>>> The reference ID can be used to identify record later. In the response the provided reference ID will be mapped with the Entry ID of the new created remedy record.

## Call create flintbit

	//Create some data
	def recordData = "{ \"MyRefId01\": { \"Description\": \"New Record\" } }"
	def createResponse = call.bit("remedy:base:create.groovy")         // Provide path for flintbit
                      .set("form","flint") // Set arguments
                      .set("data", recordData)
                      .sync()

	log.info "createResponse: " + createResponse

## flintbit Response

	{  
	   "meta":{  
	      "runtime":554,
	      "size":1,
	      "message":"",
	      "status":"success"
	   },
	   "data":{
	      "MyRefId01":"000000000000020"
	   }
	}