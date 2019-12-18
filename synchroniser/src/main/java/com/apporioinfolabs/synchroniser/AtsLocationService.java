package com.apporioinfolabs.synchroniser;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apporioinfolabs.synchroniser.db.OfflineLogModel;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.google.gson.Gson;
import com.hypertrack.hyperlog.HyperLog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public abstract  class AtsLocationService extends Service  {


    private final static String TAG = "AtsLocationService";
    private AtsLocationManager atsLocationManager ;
    private AtsNotification atsNotification ;
    private JSONObject emiting_object ;
    private Gson gson ;
    ConnectivityManager conMgr ;
    private Timer mTimer = null;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public void onCreate() {
        gson = new Gson();
        emiting_object = new JSONObject();

        atsLocationManager = new AtsLocationManager(this);
        atsNotification = new AtsNotification(this);
        conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

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

        ATSApplication.syncActions("start_location_service");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        APPORIOLOGS.debugLog(TAG , "Service is starting ");
        try { atsNotification.startNotificationView(this); }
        catch (Exception e){ APPORIOLOGS.errorLog(TAG, ""+e.getMessage()); }
        atsLocationManager.startLocationUpdates();
        return START_NOT_STICKY;
    }

    @SuppressLint("LongLogTag")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AtsLocationEvent event) {

        try{ atsNotification.updateNotificationView(event); }
        catch (Exception e){ APPORIOLOGS.warningLog(TAG , ""+e.getMessage()); }

        Log.d("********* BATTERY LEVEL ", ""+ATSApplication.BatteryLevel);

        APPORIOLOGS.locationLog(""+event.pojolocation.getLatitude()+
                "__"+event.pojolocation.getLongitude()+
                "__"+event.pojolocation.getAccuracy()+
                "__"+event.pojolocation.getBearing()+
                "__"+ATSApplication.BatteryLevel+
                "__"+event.pojolocation.getTime());



        if(!ATSApplication.setIntervalRunningWhenVehicleStops){
            onReceiveLocation(event.pojolocation);
//            APPORIOLOGS.debugLog(""+TAG,"Sending location to developer code for running API : (When setIntervalRunningWhenVehicleStops = false)");
        }


        ATSApplication.saveLocationInTempSession(event.getPojolocation().getLatitude(), event.getPojolocation().getLongitude() , event.getPojolocation().getAccuracy() , event.getPojolocation().getBearing() );


        if(ATSApplication.getSocket().connected()){
            SocketListeners.emitLocation(getJSONObjectToEmit(event));
        }

        if(HyperLog.getDeviceLogsCount() >= 25){
            try{ syncLogs(gson.toJson(HyperLog.getDeviceLogs(false)));}
            catch (Exception e){ Log.e(TAG, "Exception while syncing: "+e.getMessage()); }
        }



        if(ATSApplication.getSqlLite().getLogTableCount() > 0){
            // sync it in a sequential manner
            List<OfflineLogModel> offlineLogs = ATSApplication.getSqlLite().getAllLogsFromTable();
            try{ syncAndDeleteLogsFromDatabase(offlineLogs.get(0).get_log(),offlineLogs.get(0).get_id()); }catch (Exception e){ Log.e(TAG , ""+e.getMessage());}
        }

    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventLocationSyncSuccess event) {
        if(event.isSync()){
//            sqliteDBHelper.clearOfflineStats();
        }
    };

    private JSONObject getJSONObjectToEmit (AtsLocationEvent event){
        try{
//            emiting_object.put("unique_id",""+DeviceInfoManager.getDeviceInfo().get("device_id"));
            emiting_object.put("location", event.getLocation());
//            emiting_object.put("battery_stats", deviceInfoManager.getBatteryStat());
//            emiting_object.put("permissions",AppInfoManager.getPermissionWithStatus());
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
        ATSApplication.syncActions("stop_location_service");
    }

    public abstract void onReceiveLocation(Location location);

    public void syncLogs(String jsondata) throws Exception{
        AndroidNetworking.post(""+ATSApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key",jsondata)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        HyperLog.deleteLogs();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced now saving things to Database"+error.getLocalizedMessage());
                        ATSApplication.getSqlLite().addLogBunch(""+gson.toJson(HyperLog.getDeviceLogs(false)));
                        HyperLog.deleteLogs();
                    }
                });
    }

    public void syncAndDeleteLogsFromDatabase(String jsonData, final int id) throws Exception{
        AndroidNetworking.post(""+ATSApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key",jsonData)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ATSApplication.getSqlLite().deleteLogsbyId(id);
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced now saving things to Database"+error.getLocalizedMessage());
                        ATSApplication.getSqlLite().addLogBunch(""+gson.toJson(HyperLog.getDeviceLogs(false)));
                        HyperLog.deleteLogs();
                    }
                });

    }

     class TimeDisplayTimerTask extends TimerTask{
        @Override
        public void run() {
            HashMap<String, String> locationSession = ATSApplication.getLocationFromTempSession();
            Location location = new Location(""+locationSession.get(""+AtsConstants.SessionKeys.Provider));
            location.setLatitude(Double.parseDouble(""+ locationSession.get(""+AtsConstants.SessionKeys.Latitude)));
            location.setLongitude(Double.parseDouble(""+ locationSession.get(""+AtsConstants.SessionKeys.Longitude)));
            location.setAccuracy(Float.parseFloat(""+ locationSession.get(""+AtsConstants.SessionKeys.Accuracy)));
            location.setBearing(Float.parseFloat(""+ locationSession.get(""+AtsConstants.SessionKeys.Bearing)));
            onReceiveLocation( location);
        }
    }


}
