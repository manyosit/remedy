---
title: Configuration
taxonomy:
    category: docs
---
The Remedy Connector can use a global configuration. The name of the configuration object in flint is remedy. The following values can be configured.

>>> The global configuration will be used as defaults whenever a parameter is not set. But it can always be overwritten if the parameter is provided in the flinbit call.

	{
	  "arapi": "http://192.168.0.52:32769/arapi",
	  "connector": "remedy",
	  "password": "password",
	  "port": 9988,
	  "server": "192.168.0.52",
	  "username": "rhannemann"
	}
	

+ arapi

The URL of the arapi used to connect to the Remedy Server. 

+ connector

The name of the http connector that should be used to connect with the arapi.

+ username

The username of the Remedy User that should be used.

+ password

The password of the Remedy User that should be used.

+ server

The FQDN or IP of the Remedy Server that should be used.

+ port

The API Port of the Remedy Server that should be used.