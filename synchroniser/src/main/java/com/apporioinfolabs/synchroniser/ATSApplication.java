package com.apporioinfolabs.synchroniser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.apporioinfolabs.synchroniser.logssystem.CustomLogMessageFormat;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypertrack.hyperlog.DeviceLogModel;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public abstract class ATSApplication extends Application  implements Application.ActivityLifecycleCallbacks  {

    private static Socket mSocket;
    private static final String TAG = "ATSApplication";
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
    public static int CashLength = 25 ;
    public static boolean setIntervalRunningWhenVehicleStops = true ;
    public static JSONObject onConnectionObject ;
    public static String UNIQUE_NO  = "";
    private static RequestQueue requestQueue; ;

    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private static Gson gson ;

    private static boolean app_foreground = false ;


//    public static final String EndPoint_add_logs = "http://localhost:3108/api/v1/logs/add_log";
    public static final String EndPoint_add_logs = "http://13.233.98.63:3108/api/v1/logs/add_log";
    public static final String EndPoint_sync_App_State = "http://13.233.98.63:3108/api/v1/logs/sync_app_state";


    @Override
    public void onCreate() {
        small_Icon = R.drawable.apporio_logo ;
        mContext = this;
        HyperLog.initialize(this);
        HyperLog.setLogLevel(Log.VERBOSE);
        HyperLog.setLogFormat(new CustomLogMessageFormat(this));
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
            APPORIOLOGS.exceptionLog(TAG , ""+e.getMessage());
        }
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
        registerActivityLifecycleCallbacks(this);
    }


    private void selDevelopmentModeAccordingly(boolean setDeveloperMode) {

//        if(setDeveloperMode){ APPORIOLOGS.informativeLog(TAG , "Application is in development mode"); }
//        else{ APPORIOLOGS.informativeLog(TAG , "Application is not in development mode"); }

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
        APPORIOLOGS.playerId(data);
    }

    public static void setExtraData (String data){
        APPORIOLOGS.extraData(data);
    }

    public static SharedPreferences getAtsPrefrences (){
        if(sharedPref == null){
            sharedPref = mContext.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
            return sharedPref ;
        }else{
            return sharedPref;
        }
    }

    @SuppressLint("LongLogTag")
    public static void syncPhoneState() throws  Exception{

        Log.d(""+TAG,"----> "+gson.toJson(getListofRunningServices()));

        JSONObject app_state_jsno = new JSONObject();
        app_state_jsno.put("location_service_running",isServiceRunning());
        app_state_jsno.put("running_services", gson.toJson(getListofRunningServices()) );
        app_state_jsno.put("foreground",app_foreground);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unique_no",""+Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
        jsonObject.put("package_name",mContext.getApplicationContext().getPackageName());
        jsonObject.put("permissions",AppInfoManager.getPermissionWithStatus());
        jsonObject.put("app_state",app_state_jsno);


        AndroidNetworking.post(""+ATSApplication.EndPoint_sync_App_State)
                .addJSONObjectBody(jsonObject)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "State Synced Successfully ");
                    }
                    @Override
                    public void onError(ANError error) {
                        APPORIOLOGS.errorLog(TAG, "Phone state not synced in library "+error.getLocalizedMessage());
                    }
                });



    }


    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
//            Toast.makeText(activity, "Enters in foreground | Pending logs:"+HyperLog.hasPendingDeviceLogs()+" | Log count:"+HyperLog.getDeviceLogsCount(), Toast.LENGTH_LONG).show();
            Log.d(TAG , "Enters in foreground");
            app_foreground = true ;
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
//            Toast.makeText(activity, "Enters in background | Pending logs"+HyperLog.hasPendingDeviceLogs()+" | Log count:"+HyperLog.getDeviceLogsCount(), Toast.LENGTH_LONG).show();
            app_foreground = false ;
            try{syncLogsAccordingly();}catch (Exception e){
                APPORIOLOGS.exceptionLog(TAG, ""+e.getMessage());
            }
        }
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    private void syncLogsAccordingly() throws Exception{

//        Toast.makeText(mContext, ""+HyperLog.getDeviceLogsInFile(this), Toast.LENGTH_SHORT).show();

        //Extra header to post request
        HashMap<String, String> params = new HashMap<>();
        params.put("timezone", TimeZone.getDefault().getID());
        List<DeviceLogModel> deviceLogModels = HyperLog.getDeviceLogs(false) ;


        JSONObject jsonObject  = new JSONObject();

        try{
            jsonObject.put("key",gson.toJson(deviceLogModels));
        }catch (Exception e){
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.d(TAG, "Syncing Logs to Log panel");
        AndroidNetworking.post("" + EndPoint_add_logs)
                .addJSONObjectBody(jsonObject)
                .setTag(this)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject jsonObject) {
                        HyperLog.deleteLogs();
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Toast.makeText(ATSApplication.this, "ERROR :  "+anError.getErrorBody(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mContext);
        }
        return requestQueue ;
    }

    private static boolean isServiceRunning() {

        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.apporioinfolabs.taxilocationlib.UpdateServiceClass")) {
                return true;
            }
        }
        return false;
    }


    private static List<String> getListofRunningServices(){
        List<String> services = new ArrayList<>();
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> data = manager.getRunningServices(Integer.MAX_VALUE)  ;



        for(int i =0 ; i < data.size() ; i++){
            services.add(""+data.get(i).service.getClassName().replace("com.apporioinfolabs.taxilocationlib.",""));
        }
        return  services;
    }




}
