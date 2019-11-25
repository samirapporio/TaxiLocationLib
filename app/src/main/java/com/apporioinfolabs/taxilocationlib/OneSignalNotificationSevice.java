package com.apporioinfolabs.taxilocationlib;

import android.annotation.SuppressLint;
import android.util.Log;

import com.apporioinfolabs.synchroniser.ATSApplication;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class OneSignalNotificationSevice extends NotificationExtenderService {

    private final static String TAG = "OneSignalNotificationSevice";

    @SuppressLint("LongLogTag")
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        if(receivedResult.payload.additionalData != null){
            try {
                Log.i(""+TAG,"Received notification "+receivedResult.payload.additionalData.getString("action") );
                doActioAccordingToState (""+receivedResult.payload.additionalData.getString("action"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return true;
    }

    @SuppressLint("LongLogTag")
    private void doActioAccordingToState(String action) throws Exception {

        switch (action){
            case "sync_app_state":
                Log.i(TAG , "Need to sync App state here accordingly ");
                ATSApplication.syncPhoneState();
                break;
            default:
                Log.i(TAG, "Unable to read action string: "+action);
                break;
        }

    }
}