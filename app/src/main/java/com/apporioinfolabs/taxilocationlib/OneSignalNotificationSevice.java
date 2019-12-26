package com.apporioinfolabs.taxilocationlib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.AtsConstants;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OneSignalNotificationSevice extends NotificationExtenderService {

    private final static String TAG = "OneSignalNotificationSevice";

    @SuppressLint("LongLogTag")
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
//        Log.d(TAG , "Notification"+receivedResult.payload.notificationID);
        String action = "default";
        if(receivedResult.payload.additionalData != null){

            Log.d(TAG , "---------> "+receivedResult.payload.additionalData);

            try { parseLibraryAction(""+receivedResult.payload.additionalData.getString("data" )); }
            catch (Exception e) { Log.e(TAG , ""+e.getMessage()); }
        }
        return false ;
    }



    @SuppressLint("LongLogTag")
    private void parseLibraryAction (String notificatoin_data) throws Exception{
        try {
            JSONObject jsonObject = new JSONObject(notificatoin_data);
            doActioAccordingToState (""+jsonObject.getString("action"));
            MainApplication.targetPlayerIdNotification = ""+jsonObject.getString("player_id");
        } catch (Exception e) {
            Log.e(TAG , ""+e.getMessage());
        }
    }


    @SuppressLint("LongLogTag")
    private void doActioAccordingToState(String action) throws Exception {
        OneSignal.clearOneSignalNotifications();
        switch (action){
            case ""+ AtsConstants.SYNC_APP_STATE:
                Log.i(TAG , "Need to sync App state here accordingly ");
                AtsApplication.syncPhoneState();
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
            case ""+AtsConstants.SYNC_EXISTING_LOGS:
                AtsApplication.syncLogsAccordingly();
                break;
            case ""+AtsConstants.CLEAR_ONE_SIGNAL_NOTIFICATIONS:
                AtsApplication.syncActions("clear_OneSignal_Notifications");
                OneSignal.clearOneSignalNotifications();
                break;
            default:
                Log.i(TAG, "Unable to read action string: "+action);
                break;
        }

    }


}