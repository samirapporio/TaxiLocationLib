package com.apporioinfolabs.synchroniser;

import com.apporioinfolabs.apporiologsystem.APPORIOLOGS;

import org.json.JSONObject;

public class OnConnectionInfoManager {


    private static final String  TAG = "OnConnectionInfoManager";


    public static JSONObject getDeviceAndApplicationInfo(){
        try{
            ATSApplication.onConnectionObject.put("device_info", DeviceInfoManager.getDeviceInfo());
            ATSApplication.onConnectionObject.put("application_info", AppInfoManager.getApplicafionInfo());
        }catch (Exception e){
            APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
        }
       return ATSApplication.onConnectionObject ;
    }
}
