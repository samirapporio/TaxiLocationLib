package com.apporioinfolabs.synchroniser;

public class ApporioTrackingSystem {

    public static boolean isAutoLogSynchroniserEnable (){
       return AtsApplication.autoLogSynchronization ;
    }

    public static boolean isLiveLogsEnable(){
        return AtsApplication.isLiveLogsAllowed;
    }

    public static boolean isSocketConnectionAllowed(){
        return AtsApplication.isSocketConnection_allowed ;
    }

    public static boolean isLogsSyncOnAppMinimise(){
        return AtsApplication.logSyncOnAppMinimize ;
    }




    public static void setAutoLogSyncable(boolean value){
        AtsApplication.autoLogSynchronization = value ;
    }

}
