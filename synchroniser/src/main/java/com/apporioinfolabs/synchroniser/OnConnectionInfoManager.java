package com.apporioinfolabs.synchroniser;

import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;

import org.json.JSONObject;

public class OnConnectionInfoManager {


    private static final String  TAG = "OnConnectionInfoManager";


    public static JSONObject getDeviceAndApplicationInfo(){
        try{
            ATSApplication.onConnectionObject.put("device_info", DeviceInfoManager.getDeviceInfo());
            ATSApplication.onConnectionObject.put("application_info", AppInfoManager.getApplicafionInfo());
            ATSApplication.onConnectionObject.put("services",ATSApplication.getGson().toJson(ATSApplication.getListofRunningServices()));
            ATSApplication.onConnectionObject.put("device_timestamp",System.currentTimeMillis());
        }catch (Exception e){
            APPORIOLOGS.warningLog(TAG , ""+e.getMessage());
        }
       return ATSApplication.onConnectionObject ;
    }
}
