package com.apporioinfolabs.taxilocationlib;

import android.app.PendingIntent;
import android.widget.Toast;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.handlers.AtsSocketConnectionHandlers;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainApplication extends AtsApplication {

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
    public boolean setAutoLogSynchronization() {
        return true;
    }

    @Override
    public boolean setLogSyncOnAppMinimize() {
        return true;
    }

    @Override
    public boolean setSocketConnection() { return true; }

    @Override
    public boolean allowLiveLogs() {
        return true;
    }

    @Override
    public int setSmallNotificationIcons() {
        return R.drawable.notification_icon;
    }

    @Override
    public int setLargeNotificationIcons() {
        return R.drawable.notification_icon;
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
    public boolean allowLocationToEmitOnSocket() {
        return true;
    }

    @Override
    public boolean allowAppStateSyncOnSocket() {
        return true;
    }

    @Override
    public AtsSocketConnectionHandlers setAtsConnectionStateHandlers() {
        return new AtsConnection();
    }

    @Override
    public String dataSynced(String action) {
        Toast.makeText(this, "Synced Action: "+action, Toast.LENGTH_SHORT).show();
        try{sendNotificationToTargetDebuggingDevice(targetPlayerIdNotification,action);}catch (Exception e){}
        return action;
    }

    @Override
    public String dataSyncedError(String s) {
        Toast.makeText(this, "Error: "+s, Toast.LENGTH_SHORT).show();
        return s;
    }


    public void sendNotificationToTargetDebuggingDevice(String playerid , final String action ) throws Exception {
        if (playerid.equals("NA")) {

        } else {


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("app_id", "89785e84-3330-4e07-b216-be05b666c212");

            JSONObject contentObject = new JSONObject();
            contentObject.put("en", "Message from target device");
            jsonObject.put("contents", contentObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put("action", "" + action);
            jsonObject.put("data", dataObject);

            JSONArray player_ids_array = new JSONArray();
            player_ids_array.put("" + playerid);
            jsonObject.put("include_player_ids", player_ids_array);


            OneSignal.postNotification(jsonObject, new OneSignal.PostNotificationResponseHandler() {
                @Override
                public void onSuccess(JSONObject response) {
                    Toast.makeText(mContext, "Synced :"+action, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(JSONObject response) {
                    Toast.makeText(mContext, "Sync Error for:"+action +""+response, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
