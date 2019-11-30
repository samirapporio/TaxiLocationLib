package com.apporioinfolabs.taxilocationlib;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.apporioinfolabs.synchroniser.ATSApplication;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OneSignalNotificationSevice extends NotificationExtenderService {

    private final static String TAG = "OneSignalNotificationSevice";

    @SuppressLint("LongLogTag")
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        Log.d(TAG , "Notification Id "+receivedResult.payload.notificationID);
        if(receivedResult.payload.additionalData != null){
            try {
                Log.i(""+TAG,"Received notification "+receivedResult.payload.additionalData.getString("action") );
                doActioAccordingToState (""+receivedResult.payload.additionalData.getString("action"));
            } catch (Exception e) {
                Log.e(TAG , ""+e.getMessage());
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
             case "start_location_service":
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     startForegroundService(new Intent(this, UpdateServiceClass.class));
                 } else { // normal
                     startService(new Intent(this, UpdateServiceClass.class));
                 }
                 break;
            case "stop_location_service":
                stopService(new Intent(this, UpdateServiceClass.class));
                break;
            case "sync_existing_logs":
                ATSApplication.syncLogsAccordingly();
                break;
            case "clear_OneSignal_Notifications":
                OneSignal.clearOneSignalNotifications();
                break;
            default:
                Log.i(TAG, "Unable to read action string: "+action);
                break;
        }

    }


}