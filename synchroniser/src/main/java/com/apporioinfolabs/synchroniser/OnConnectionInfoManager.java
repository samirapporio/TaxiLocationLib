package com.apporioinfolabs.synchroniser;

import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;

import org.json.JSONObject;

public class OnConnectionInfoManager {


    private static final String  TAG = "OnConnectionInfoManager";


    public static JSONObject getDeviceAndApplicationInfo(){
        try{
            AtsApplication.onConnectionObject.put("device_info", DeviceInfoManager.getDeviceInfo());
            AtsApplication.onConnectionObject.put("application_info", AppInfoManager.getApplicafionInfo());
            AtsApplication.onConnectionObject.put("services", AtsApplication.getGson().toJson(AtsApplication.getListofRunningServices()));
            AtsApplication.onConnectionObject.put("device_timestamp",System.currentTimeMillis());
        }catch (Exception e){
            APPORIOLOGS.warningLog(TAG , ""+e.getMessage());
        }
       return AtsApplication.onConnectionObject ;
    }
}
