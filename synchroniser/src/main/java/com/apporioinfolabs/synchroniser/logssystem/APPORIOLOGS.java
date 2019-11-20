package com.apporioinfolabs.synchroniser.logssystem;

import com.hypertrack.hyperlog.HyperLog;

public class APPORIOLOGS {


    public static void debugLog(String tag , String message){
        HyperLog.d(tag, message);
    }

    public static void verboseLog (String tag , String message){
        HyperLog.v(tag , message);
    }


    public static void informativeLog (String tag , String message){
        HyperLog.i(tag , message);
    }

    public static void errorLog (String tag , String message) {
        HyperLog.e(tag, message);
    }


    public static void exceptionLog (String tag , String message){
        HyperLog.w(tag , message);
    }

    public static void locationLog(String locationJson){
        HyperLog.i("LOCATION_LOG", locationJson);
    }

    public static void playerId (String player_id){
        HyperLog.i("PLAYER_ID", player_id);
    }

    public static void extraData (String extra_data){
        HyperLog.i("EXTRA_DATA",""+extra_data);
    }


    public static void assertLog (String tag , String message){
        HyperLog.a(tag +" --> "+message);
    }




}

