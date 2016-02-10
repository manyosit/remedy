---
title: Query
taxonomy:
    category:
        - docs
---

## Flintbit

**remedy:base:query.groovy**

## Call query flintbit

	//Query a form
	def queryResponse = call.bit("remedy:base:query.groovy")         // Provide path for flintbit
                      .set("form","flint") // Set arguments
                      .set("query","'8' = \"Some Text\"")
                      .sync()                               // To call flintbit asynchronously 	use .async() instead of .sync()
	
	log.info "queryResponse: " + queryResponse

## flintbit Response

	{  
	   "meta":{  
	      "runtime":554,
	      "size":1,
	      "message":"",
	      "status":"success"
	   },
	   "data":{
	      "000000000000020":{
          	"Description" : "Some Text",
            "Status"	: "New",
            "Submitter"	: "Demo",
            "Last modified by" : "Demo",
          }
	   }
	}