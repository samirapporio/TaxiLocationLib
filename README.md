[![N|Solid](https://www.apporio.com/wp-content/uploads/2018/03/logo-1.png)](https://www.apporio.com/)

# ATS (Apporio Tracking System)
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
implementation 'com.github.samirapporio:TaxiLocationLib:1.1.5'
```
