package com.apporioinfolabs.synchroniser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apporioinfolabs.synchroniser.db.SqliteDBHelper;
import com.apporioinfolabs.synchroniser.handlers.AtsSocketConnectionHandlers;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.apporioinfolabs.synchroniser.logssystem.CustomLogMessageFormat;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AtsApplication extends Application  implements Application.ActivityLifecycleCallbacks, AtsApiSynchroniesr.OnSync  {

    private static Socket mSocket;
    private static final String TAG = "AtsApplication";
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
    public static boolean autoLogSynchronization = false ;
    public static boolean logSyncOnAppMinimize = true ;
    public static boolean isLiveLogsAllowed = true ;
    public static boolean isSyncLocationOnSocket = false;
    public static JSONObject onConnectionObject ;
    public static AtsSocketConnectionHandlers atsSocketConnectionHandlers ;
    public static String TOKEN = "NA";
    public static String UNIQUE_NO  = "";
    public static boolean IS_SOCKET_CONNECTED = false ;
    public static int BatteryLevel ;
    private int activityReferences = 0;
    private boolean isActivityChangingConfigurations = false;
    private static Gson gson ;
    private static boolean app_foreground = false ;
    private static AtsApiSynchroniesr atsApiSynchroniesr = null;
    private static SqliteDBHelper sqliteDBHelper = null ;


//    public static final String EndPoint_add_logs = "http://localhost:3108/api/v1/logs/add_log";
    public static final String EndPoint_add_logs = "http://13.233.98.63:3108/api/v1/logs/save_log";
    public static final String EndPoint_sync_log_file = "http://13.233.98.63:3108/api/v1/logs/savefile";
    public static final String EndPoint_sync_App_State = "http://13.233.98.63:3108/api/v1/logs/sync_app_state";
//    public static final String EndPoint_socket = "http://192.168.1.33:3005"; // development server Apporio Airtel_5Ghz
    public static final String EndPoint_socket = "http://13.233.98.63:3005";  // live server TBPO
    public static final String EndPoint_set_trip = "http://13.233.98.63:3005/api/v1/sockets_api/set_trip_flag";  // live server TBPO



    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            BatteryLevel = level ;
//            batteryTxt.setText(String.valueOf(level) + "%");
        }
    };


    @Override
    public void onCreate() {
        small_Icon = R.drawable.apporio_logo ;
        mContext = this;
        HyperLog.initialize(this);
        HyperLog.setLogLevel(Log.DEBUG);
        HyperLog.setLogFormat(new CustomLogMessageFormat(this));
        sharedPref = this.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
        sqliteDBHelper = new SqliteDBHelper(this);
        editor = sharedPref.edit();
        onConnectionObject = new JSONObject();
        UNIQUE_NO = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        selDevelopmentModeAccordingly(setDeveloperMode());
        gson = new GsonBuilder().create();
        atsApiSynchroniesr = new AtsApiSynchroniesr(this);
        small_Icon = setSmallNotificationIcons();
        large_icon = setLargeNotificationIcons() ;
        long minterval = setLocationFetchInterval();
        if(minterval <= 6000 ){ locationFetchInterval = 6000 ; }
        else{ locationFetchInterval = minterval; }
        notificatioOnlineText = setNotificationOnlineText();
        notificatioMakingOnlineText = setNotificationMakingOnlineText();
        notificationClickIntent = setNotificationClickIntent() ;
        setIntervalRunningWhenVehicleStops = setIntervalRunningOnVehicleStop();
        autoLogSynchronization = setAutoLogSynchronization();
        logSyncOnAppMinimize = setLogSyncOnAppMinimize();
        isLiveLogsAllowed = allowLiveLogs();
        isSyncLocationOnSocket = allowLocationToEmitOnSocket() ;
        atsSocketConnectionHandlers = setAtsConnectionStateHandlers();
        TOKEN = setTokenForConnection();

        try{ connectToSocketServer(); }
        catch (Exception e){ Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show(); }



        super.onCreate();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerActivityLifecycleCallbacks(this);
    }


    public static Socket getSocket (){
        return mSocket ;
    }

    public static Gson getGson(){
        if(gson == null){
            gson = new GsonBuilder().create();
            return gson ;
        }else{
            return gson ;
        }
    }

    public static SqliteDBHelper getSqlLite(){
        if(sqliteDBHelper == null){
            sqliteDBHelper = new SqliteDBHelper(mContext);
            return sqliteDBHelper ;

        }else{
            return sqliteDBHelper ;
        }
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
    public abstract String dataSynced(String action);
    public abstract String dataSyncedError(String error);
    public abstract boolean setAutoLogSynchronization ();
    public abstract boolean setLogSyncOnAppMinimize ();
    public abstract boolean allowLiveLogs();
    public abstract boolean allowLocationToEmitOnSocket();
    public abstract AtsSocketConnectionHandlers setAtsConnectionStateHandlers();
    public abstract String setTokenForConnection();




    public void connectToSocketServer() throws Exception{
        mSocket = IO.socket(""+EndPoint_socket);
        mSocket.on("connect", SocketListeners.onConnect);
        mSocket.on("disconnect", SocketListeners.onDisconnected);
        mSocket.connect();
    }

    private static void reConnectToBasicRequiredListeners(){
        mSocket.on("connect", SocketListeners.onConnect);
        mSocket.on("disconnect", SocketListeners.onDisconnected);
    }

    public static void removePreviousListeners(){
        mSocket.off();
        reConnectToBasicRequiredListeners();
    }

    public static void setPlayerId(String data){
        APPORIOLOGS.playerId(data);
    }

    public static void setExtraData (String data){
        APPORIOLOGS.extraData(data);
    }

    public static void setCriteria(String data , OnAtsEmissionListeners onAtsEmissionListeners) throws Exception{
        if(isSocketConnected()){
            SocketListeners.emitCriteria(data, onAtsEmissionListeners);
        }else{
            onAtsEmissionListeners.onFailed("Socket is not connected for setting criteria");
            Log.e(TAG, "Socket is not connected for setting criteria");
        }
    }

    public static void removeCriteria(OnAtsEmissionListeners onAtsEmissionListeners) throws  Exception{
        if(isSocketConnected()){
            SocketListeners.removeCriteria(onAtsEmissionListeners);
        }else{
            onAtsEmissionListeners.onFailed("Socket is not connected for setting criteria");
            Log.e(TAG, "Socket is not connected for removing criteria");
        }
    }


    public static void setScreenId(String screenId, OnAtsEmissionListeners onAtsEmissionListeners){
        if(isSocketConnected()){
            SocketListeners.emitAddScreenId(screenId, onAtsEmissionListeners);
        }else{
            onAtsEmissionListeners.onFailed("Socket is not connected for emitting add screen id");
            Log.e(TAG, "Socket is not connected for emitting add screen id");
        }

    }


    public static void removeScreenId(OnAtsEmissionListeners onAtsEmissionListeners){
        if(isSocketConnected()){
            SocketListeners.emitRemoveScreenId(onAtsEmissionListeners);
        }else{
            onAtsEmissionListeners.onFailed("Socket is not connected for emitting remove screen id");
            Log.e(TAG, "Socket is not connected for emitting remove screen id");
        }

    }



    public static SharedPreferences getAtsPrefrences (){
        if(sharedPref == null){
            sharedPref = mContext.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
            return sharedPref ;
        }else{
            return sharedPref;
        }
    }

    public static void saveLocationInTempSession(double latitude, double longitude, float accuracy, float bearing){
        editor.putString(""+AtsConstants.SessionKeys.Latitude,""+latitude);
        editor.putString(""+AtsConstants.SessionKeys.Longitude,""+longitude);
        editor.putString(""+AtsConstants.SessionKeys.Accuracy,""+accuracy);
        editor.putString(""+AtsConstants.SessionKeys.Bearing,""+bearing);
        editor.commit();
    }

    public static HashMap<String, String> getLocationFromTempSession(){
        HashMap<String, String > temp_location = new HashMap<>();
        SharedPreferences sp = getAtsPrefrences();
        temp_location.put(""+AtsConstants.SessionKeys.Provider,""+sp.getString("PROVIDER", "NA"));
        temp_location.put(""+AtsConstants.SessionKeys.Latitude,""+sp.getString("LAT","0.0"));
        temp_location.put(""+AtsConstants.SessionKeys.Longitude,""+sp.getString("LONG","0.0"));
        temp_location.put(""+AtsConstants.SessionKeys.Accuracy,""+sp.getString("ACCURACY","0.0"));
        temp_location.put(""+AtsConstants.SessionKeys.Bearing,""+sp.getString("BEARING","0.0"));
        return temp_location ;
    }

    public  static void syncLogsAccordingly() throws Exception{
        Log.d(TAG, "Syncing Logs to Log panel");
        atsApiSynchroniesr.syncLogsAccordingly();
    }

    public static boolean isServiceRunning() {

        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().equals("com.apporioinfolabs.taxilocationlib.UpdateServiceClass")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSocketConnected(){
        if(mSocket.connected()){
            return true ;
        }else{
            return false;
        }
    }

    public static void startTrip(String flag, String tag,String locationSubmissionUrl, OnAtsTripSetterListeners onAtsTripSetterListeners)throws Exception{
        Log.d(TAG, "Setting trip with flag:"+flag);
        atsApiSynchroniesr.setTrip(flag, tag, locationSubmissionUrl, onAtsTripSetterListeners);
    }

    public static void endTrip(String flag, String tag,String locationSubmissionUrl, OnAtsTripSetterListeners onAtsTripSetterListeners)throws Exception{
        Log.d(TAG, "Setting trip with flag:"+flag);
        atsApiSynchroniesr.setTrip(flag, tag, locationSubmissionUrl,  onAtsTripSetterListeners);
    }

    public static List<String> getListofRunningServices(){
        List<String> services = new ArrayList<>();
        ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> data = manager.getRunningServices(Integer.MAX_VALUE)  ;

        for(int i =0 ; i < data.size() ; i++){
            services.add(""+data.get(i).service.getClassName().replace("com.apporioinfolabs.taxilocationlib.",""));
        }

        return  services;
    }

    @SuppressLint("LongLogTag")
    public static void syncPhoneState() throws  Exception{

//        Log.d(""+TAG,"----> "+gson.toJson(getListofRunningServices()));
//
//        JSONObject app_state_jsno = new JSONObject();
//        app_state_jsno.put("location_service_running",isServiceRunning());
//        app_state_jsno.put("running_services", gson.toJson(getListofRunningServices()) );
//        app_state_jsno.put("foreground",app_foreground);

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("unique_no",""+Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
//        jsonObject.put("package_name",mContext.getApplicationContext().getPackageName());
//        jsonObject.put("permissions",AppInfoManager.getPermissionWithStatus());
        jsonObject.put("app_state",AppInfoManager.getAppStatusEncode());


        atsApiSynchroniesr.syncPhoneState(jsonObject);
    }

    public static void syncActions(String action){
        atsApiSynchroniesr.syncStraightAction(action);
    }

    public static void syncHyperLogsStach(String jsondata) throws Exception{
        atsApiSynchroniesr.syncHyperLogStashFromService(jsondata, AtsConstants.SYNC_EXISTING_LOGS);

    }

    public static void syncAndDeleteLogsFromDatabse(String jsonData, int log_stach_id) throws Exception{
        atsApiSynchroniesr.syncAndDeleteLogsFromDatabse(jsonData, log_stach_id, AtsConstants.SYNC_EXISTING_LOGS_FROM_DATABASE);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
//            Toast.makeText(activity, "Enters in foreground | Pending logs:"+HyperLog.hasPendingDeviceLogs()+" | Log count:"+HyperLog.getDeviceLogsCount(), Toast.LENGTH_LONG).show();
            Log.d(TAG , "Enters in foreground");
            app_foreground = true ;
            try{
                atsApiSynchroniesr.syncPhoneState(new JSONObject().put("app_state", AppInfoManager.getAppStatusEncode()));
                SocketListeners.emitAppState();
            }catch (Exception e){
                Toast.makeText(activity, "Exception while extracting app state: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            app_foreground = false ;
            removePreviousListeners();
            if(logSyncOnAppMinimize){
                Log.i(TAG , "Syncing logs on minimise");
                try{syncLogsAccordingly();}catch (Exception e){
                APPORIOLOGS.warningLog(TAG, ""+e.getMessage());
            }}
            else{Log.i(TAG , "On minimise logs is not synced");}
        }
    }

    public static boolean isAppInForegorund(){
        return app_foreground ;
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
        Log.i(TAG +"APP_STATE", "Activity destroyed");
    }

    @Override
    public void onTerminate() {
        Log.i(TAG +"APP_STATE", "App is terminated");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG +"APP_STATE", "App is on low memory");
        super.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.i(TAG +"APP_STATE", "On app configuration changed");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSyncSuccess(String action) {
        dataSynced(action);
    }

    @Override
    public void onSyncError(String error) {
        dataSyncedError(error);
    }



}
