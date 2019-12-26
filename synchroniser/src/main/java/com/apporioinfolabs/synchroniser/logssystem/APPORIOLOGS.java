package com.apporioinfolabs.synchroniser.logssystem;

import com.apporioinfolabs.synchroniser.SocketListeners;
import com.hypertrack.hyperlog.HyperLog;

public class APPORIOLOGS {

    private final static String TAG = "APPORIOLOGS";

    public static void debugLog(String tag , String message){
        HyperLog.d(tag, message);
        emitLogs(tag, message, "DEBUG");
    }

    public static void verboseLog (String tag , String message){
        HyperLog.v(tag , message);
        emitLogs(tag, message , "VERBOSE");
    }

    public static void informativeLog (String tag , String message){
        HyperLog.i(tag , message);
        emitLogs(tag, message, "INFO");
    }

    public static void errorLog (String tag , String message) {
        HyperLog.e(tag, message);
        emitLogs(tag, message, "ERROR");
    }

    public static void warningLog(String tag , String message){
        HyperLog.w(tag , message);
        emitLogs(tag, message, "WARN");
    }

    public static void locationLog(String locationJson){
        HyperLog.i("LOCATION_LOG", locationJson);
    }

    public static void playerId (String player_id){
        HyperLog.i("PLAYER_ID", player_id);
        emitLogs("PLAYER_ID", player_id, "PLAYER_ID");
    }

    public static void extraData (String extra_data){
        HyperLog.i("EXTRA_DATA",""+extra_data);
        emitLogs("EXTRA_DATA", ""+extra_data, "EXTRA_DATA");
    }

    public static void appStateLog (String app_State){
        HyperLog.i("APP_STATE",""+app_State);
        emitLogs("APP_STATE", ""+app_State, "APP_STATE");
    }


    public static void assertLog (String tag , String message){
        HyperLog.a(tag +" --> "+message);
        emitLogs(tag, message, "ASSERT");
    }

    public static void emitLogs(String tag , String message , String level){
        SocketListeners.emitLog(tag+"_:_"+message+"_:_"+level+"_:_"+System.currentTimeMillis());
    }

}

