package com.apporioinfolabs.taxilocationlib;

import android.app.PendingIntent;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.apporioinfolabs.synchroniser.ATSApplication;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainApplication extends ATSApplication {

    public static String targetPlayerIdNotification = "NA";
    public final static String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new MyNotificationOpenHandler())
                .setNotificationReceivedHandler(new MyNotificationReceiverhandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @Override
    public boolean setDeveloperMode() {
        return true;
    }

    @Override
    public int setSmallNotificationIcons() {
        return R.drawable.accuracy_icon;
    }

    @Override
    public int setLargeNotificationIcons() {
        return R.drawable.accuracy_icon;
    }

    @Override
    public long setLocationFetchInterval() {
        return 7000;
    }

    @Override
    public String setNotificationOnlineText() {
        return "You are online now";
    }

    @Override
    public String setNotificationMakingOnlineText() {
        return "Making you online";
    }

    @Override
    public PendingIntent setNotificationClickIntent() {
        return null;
    }

    @Override
    public boolean setIntervalRunningOnVehicleStop() {
        return true;
    }

    @Override
    public String dataSynced(String action) {
        try{sendNotificationToTargetDebuggingDevice(targetPlayerIdNotification,action);}catch (Exception e){}
        return action;
    }

    @Override
    public String dataSyncedError(String s) {
        Toast.makeText(this, "Error in syncing data by library", Toast.LENGTH_SHORT).show();
        return s;
    }


    public void sendNotificationToTargetDebuggingDevice(String playerid , final String action ) throws Exception {
        if (playerid.equals("NA")) {

        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", "89785e84-3330-4e07-b216-be05b666c212");

            JSONObject contentObject = new JSONObject();
            contentObject.put("en", "Message from arget device");
            jsonObject.put("contents", contentObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put("action", "" + action);
            jsonObject.put("data", dataObject);

            JSONArray player_ids_array = new JSONArray();
            player_ids_array.put("" + playerid);
            jsonObject.put("include_player_ids", player_ids_array);



            AndroidNetworking.post("https://app.onesignal.com/api/v1/notifications").addJSONObjectBody(jsonObject).build().getAsString(new StringRequestListener() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG , ""+response);
                    Toast.makeText(mContext, "Synced :"+action, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(mContext, "Sync Error for:"+action+"  with Error Code:"+anError.getErrorCode(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ""+anError.getErrorDetail());
                }
            });
        }


    }

}
