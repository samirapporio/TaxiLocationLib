package com.apporioinfolabs.taxilocationlib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.apporioinfolabs.synchroniser.ATSApplication;
import com.apporioinfolabs.synchroniser.AtsConstants;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

public class OneSignalNotificationSevice extends NotificationExtenderService {

    private final static String TAG = "OneSignalNotificationSevice";

    @SuppressLint("LongLogTag")
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        Log.d(TAG , "Notification Id "+receivedResult.payload.notificationID);
        if(receivedResult.payload.additionalData != null){
            try {
                Log.i(""+TAG,"Received notification "+receivedResult.payload.additionalData.getString("action") );
                Log.i(""+TAG,"Player ID which needs to acknowledge "+receivedResult.payload.additionalData.getString("player_id") );
                MainApplication.targetPlayerIdNotification = ""+receivedResult.payload.additionalData.getString("player_id");
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
            case ""+ AtsConstants.SYNC_APP_STATE:
                Log.i(TAG , "Need to sync App state here accordingly ");
                ATSApplication.syncPhoneState();
                break;
             case ""+AtsConstants.START_LOCATION_SERVICE:
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     startForegroundService(new Intent(this, UpdateServiceClass.class));
                 } else { // normal
                     startService(new Intent(this, UpdateServiceClass.class));
                 }
                 break;
            case ""+AtsConstants.STOP_LOCATION_SERVICE:
                stopService(new Intent(this, UpdateServiceClass.class));
                break;
            case ""+AtsConstants.SYNC_ESISTING_LOGS:
                ATSApplication.syncLogsAccordingly();
                break;
            case ""+AtsConstants.CLEAR_ONE_SIGNAL_NOTIFICATIONS:
                ATSApplication.syncActions("clear_OneSignal_Notifications");
                OneSignal.clearOneSignalNotifications();
                break;
            default:
                Log.i(TAG, "Unable to read action string: "+action);
                break;
        }

    }


}