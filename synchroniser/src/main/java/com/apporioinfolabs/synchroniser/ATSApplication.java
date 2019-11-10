package com.apporioinfolabs.synchroniser;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apporioinfolabs.apporiologsystem.APPORIOLOGS;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.HashMap;

import java.util.TimeZone;

public abstract class ATSApplication extends Application   {

    private static Socket mSocket;
    private final String TAG = "ATSApplication";
    public static Context mContext  = null;
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPref ;
    public static final String DEVELOPER_MODE_KEY = "developer_mode_key";
    public static final String SHARED_PREFRENCE = "com.apporio.location";
    public static  int small_Icon = 0 ;
    public static  int large_icon = 0 ;
    public static long locationFetchInterval = 6000 ;  // 6000 is default interval
    public static PendingIntent notificationClickIntent = null;
    public static String notificatioOnlineText = "You are on duty now.";
    public static String notificatioMakingOnlineText = "Making you online . . . ";
    public static int CashLength = 21 ;
    public static boolean setIntervalRunningWhenVehicleStops = true ;
    public static JSONObject onConnectionObject ;
    public static String UNIQUE_NO  = "";


    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private Gson gson ;

    private static final String EndPoint = "http://13.233.98.63:3108/api/v1/logs/add_log";

    @Override
    public void onCreate() {
        small_Icon = R.drawable.apporio_logo ;
        mContext = this;
        sharedPref = this.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        onConnectionObject = new JSONObject();
        UNIQUE_NO = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try{
            connectToSocketServer();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                this.startForegroundService(new Intent(this, AtsLocationService.class));
//            }else{ // normal
//                this.startService(new Intent(this, AtsLocationService.class));
//            }
        }
        catch (Exception e){
            APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
        }

        APPORIOLOGS.debugLog(TAG , String.valueOf(AppInfoManager.getApplicafionInfo()));
        APPORIOLOGS.debugLog(TAG , String.valueOf(NetworkInfoManager.getNetworkConnectionState(this)));
        selDevelopmentModeAccordingly(setDeveloperMode());
        gson = new GsonBuilder().create();
        small_Icon = setSmallNotificationIcons();
        large_icon = setLargeNotificationIcons() ;
        long minterval = setLocationFetchInterval();
        if(minterval <= 6000 ){ locationFetchInterval = 6000 ; }
        else{ locationFetchInterval = minterval; }
        notificatioOnlineText = setNotificationOnlineText();
        notificatioMakingOnlineText = setNotificationMakingOnlineText();
        notificationClickIntent = setNotificationClickIntent() ;
        setIntervalRunningWhenVehicleStops = setIntervalRunningOnVehicleStop();
        super.onCreate();
    }

    private void selDevelopmentModeAccordingly(boolean setDeveloperMode) {

        if(setDeveloperMode){ APPORIOLOGS.informativeLog(TAG , "Application is in development mode"); }
        else{ APPORIOLOGS.informativeLog(TAG , "Application is not in development mode"); }

        editor.putBoolean(DEVELOPER_MODE_KEY, setDeveloperMode);
        editor.commit();

    }
    public abstract boolean setDeveloperMode();
    public abstract int setSmallNotificationIcons() ;
    public abstract int setLargeNotificationIcons() ;
    public abstract long setLocationFetchInterval() ;
    public abstract String setNotificationOnlineText() ;
    public abstract String setNotificationMakingOnlineText() ;
    public abstract PendingIntent setNotificationClickIntent() ;
    public abstract boolean setIntervalRunningOnVehicleStop() ;
    public static boolean isSocketConnected(){
        return mSocket.connected();
    }


    public static Socket getSocket (){
        return mSocket ;
    }

    private void connectToSocketServer() throws Exception{
        mSocket = IO.socket("http://13.234.252.30:3002");
        mSocket.on("connect", SocketListeners.onConnect);
        mSocket.on("new message", SocketListeners.onNewMessage);
        mSocket.on("disconnect", SocketListeners.onDisconnected);
        mSocket.on(""+SocketListeners.TRIAL_LISTENER, SocketListeners.onTrialEvent);
        mSocket.on(""+SocketListeners.ADD_DEVICE_IN_LIST, SocketListeners.onAddDeviceInList);
        mSocket.on(""+SocketListeners.REMOVE_DEVICE_FROM_LIST, SocketListeners.onRemoveDeviceFromList);
        mSocket.connect();
    }


    public static void setPlayerId(String data){
        editor.putString("player_id",""+data);
        editor.commit();
    }

    public static void setExtraData (String data){
        editor.putString("extra_data",""+data);
        editor.commit();
    }

    public static SharedPreferences getAtsPrefrences (){
        if(sharedPref == null){
            sharedPref = mContext.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
            return sharedPref ;
        }else{
            return sharedPref;
        }
    }


}
