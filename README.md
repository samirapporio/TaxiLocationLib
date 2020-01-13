# ATS (Apporio Tracking System)

[![N|Solid](https://www.topdesignfirms.com/pub/companyLogo/20170506073826_1.png)](https://www.apporio.com/)

Ats is a universal solution for the developer that is a basic need of creating taxi like application. This SDK covers three main section as follows

  - ###### Handling location service
  - ###### Log Management ( live remote debugging and Log Collector  )
  - ###### Live Tracking and data sharing over sockets (Ats-Sockets)      
  
 


### Implementation in Android
In your project level gradle add jitpack server for the implementation of sdk 
```sh
allprojects {
	repositories {
	maven { url 'https://jitpack.io' }
	}
}
```

In app level gradle add the implementation 
```sh
implementation 'com.github.samirapporio:TaxiLocationLib:Tag'
```
