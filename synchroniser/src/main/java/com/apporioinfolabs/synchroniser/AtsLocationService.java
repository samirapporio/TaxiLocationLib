package com.apporioinfolabs.synchroniser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.github.abara.library.batterystats.BatteryStats;
import com.google.gson.Gson;
import com.hypertrack.hyperlog.HyperLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

public abstract  class AtsLocationService extends Service  {

    private final static String TAG = "AtsLocationService";
    private AtsLocationManager atsLocationManager ;
    private AtsNotification atsNotification ;
    private JSONObject emiting_object ;
    private DeviceInfoManager deviceInfoManager ;
    private Gson gson ;
    private JSONObject jsonObject ;
    private BatteryStats batteryStats ;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref ;
    public static final String SHARED_PREFRENCE = "com.apporio.location";

    ConnectivityManager conMgr ;
    NetworkInfo netInfo ;



    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    @Override
    public void onCreate() {
        gson = new Gson();
        jsonObject = new JSONObject();
        emiting_object = new JSONObject();
        batteryStats = new BatteryStats(this);
        deviceInfoManager = new DeviceInfoManager(this);
        atsLocationManager = new AtsLocationManager(this);
        atsNotification = new AtsNotification(this);
        conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        sharedPref = this.getSharedPreferences(SHARED_PREFRENCE, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        EventBus.getDefault().register(this);


        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }


        if(ATSApplication.setIntervalRunningWhenVehicleStops){
            APPORIOLOGS.debugLog(TAG, "Starting inner for updating location when driver is at stationary Point.");
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, ATSApplication.locationFetchInterval);
        }


        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        APPORIOLOGS.debugLog(TAG , "Service is starting ");
        try {
            atsNotification.startNotificationView(this);
        }catch (Exception e){
            APPORIOLOGS.errorLog(TAG, ""+e.getMessage());
        }
        atsLocationManager.startLocationUpdates();
        return START_NOT_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AtsLocationEvent event) {
        APPORIOLOGS.locationLog(""+event.pojolocation.getLatitude()+
                "__"+event.pojolocation.getLongitude()+
                "__"+event.pojolocation.getAccuracy()+
                "__"+event.pojolocation.getBearing()+
                "__"+batteryStats.getLevel()+
                "__"+event.pojolocation.getTime());


        if(!ATSApplication.setIntervalRunningWhenVehicleStops){
            onReceiveLocation(event.pojolocation);
//            APPORIOLOGS.debugLog(""+TAG,"Sending location to developer code for running API : (When setIntervalRunningWhenVehicleStops = false)");
        }

        editor.putString("LAT",""+event.getPojolocation().getLatitude());
        editor.putString("LONG",""+event.getPojolocation().getLongitude());
        editor.putString("ACCURACY",""+event.getPojolocation().getAccuracy());
        editor.putString("BEARING",""+event.getPojolocation().getBearing());
        editor.commit();




        if(ATSApplication.getSocket().connected()){
            APPORIOLOGS.debugLog(TAG , "Sending data from socket");
//            List<OfflineDataTable> cashed_enteries =  sqliteDBHelper.getAllOfflineStats();
//            if(cashed_enteries.size() > 0){
//                try{
//                    SocketListeners.emitCashedLocation(jsonObject.put("cashed_data",gson.toJson(cashed_enteries)));
//                }catch (Exception e){
//                    APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
//                }
//            }
//            SocketListeners.emitLocation(getJSONObjectToEmit(event));
        }
        //        else {
//
//            if(cashed_enteries.size()> ATSApplication.CashLength && conMgr.getActiveNetworkInfo() != null){
//                try{
//                    jsonObject.put("device_info",DeviceInfoManager.getDeviceInfo());
//                    jsonObject.put("application_info",AppInfoManager.getApplicafionInfo());
//                    jsonObject.put("location_data",gson.toJson(cashed_enteries));
//                   // apiManager.execution_method_post("SAVE_LOCATION","http://demo.myvistor.com:3002/api/v1/location/saveLocations",jsonObject);
//                    APPORIOLOGS.informativeLog(TAG, "Sending data from API ");
//                }catch (Exception e){
//                    APPORIOLOGS.errorLog(TAG , "Caught error in sending location "+e.getMessage());
//                }
//            }
//            else{
//                sqliteDBHelper.addOfflineEntery(new OfflineDataTable(
//                        event.pojolocation.getLatitude(),
//                        event.pojolocation.getLongitude(),
//                        event.pojolocation.getAccuracy(),
//                        event.pojolocation.getBearing(),
//                        event.pojolocation.getSpeed(),
//                        ""+batteryStats.getLevel(),
//                        ""+event.pojolocation.getTime(),
//                        "NA"
//                ));
//
//            }
//        }

        if(HyperLog.getDeviceLogsCount() >= 25){
            AndroidNetworking.post("" + ATSApplication.EndPoint)
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
                        }
                    });



        }


        try{ atsNotification.updateNotificationView(event); }
        catch (Exception e){ APPORIOLOGS.exceptionLog(TAG , ""+e.getMessage()); }

    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventLocationSyncSuccess event) {
        if(event.isSync()){
//            sqliteDBHelper.clearOfflineStats();
        }
    };


    private JSONObject getJSONObjectToEmit (AtsLocationEvent event){
        try{
            emiting_object.put("unique_id",""+DeviceInfoManager.getDeviceInfo().get("device_id"));
            emiting_object.put("location", event.getLocation());
            emiting_object.put("battery_stats", deviceInfoManager.getBatteryStat());
            emiting_object.put("permissions",AppInfoManager.getPermissionWithStatus());
        }catch (Exception e){
            APPORIOLOGS.errorLog(TAG , ""+e.getMessage());
        }
       return emiting_object;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        APPORIOLOGS.debugLog(TAG , "ATS Service is Stopped ");
        atsLocationManager.stopLocationUpdates();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public abstract void onReceiveLocation(Location location);



     class TimeDisplayTimerTask extends TimerTask{
        @Override
        public void run() {
            Location location = new Location(""+sharedPref.getString("PROVIDER", "NA"));
            location.setLatitude(Double.parseDouble(""+sharedPref.getString("LAT","0.0")));
            location.setLongitude(Double.parseDouble(""+sharedPref.getString("LONG","0.0")));
            location.setAccuracy(Float.parseFloat(""+sharedPref.getString("ACCURACY","0.0")));
            location.setBearing(Float.parseFloat(""+sharedPref.getString("BEARING","0.0")));
            onReceiveLocation( location);
//            APPORIOLOGS.debugLog(""+TAG,"Sending location to developer code for running API : (When setIntervalRunningWhenVehicleStops = True)");
        }
    }






}
