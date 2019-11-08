package com.apporioinfolabs.synchroniser;

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


    public static void assertLog (String tag , String message){
        HyperLog.a(tag +" --> "+message);
    }




}

