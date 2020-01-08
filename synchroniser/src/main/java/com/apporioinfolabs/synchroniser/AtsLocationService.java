package com.apporioinfolabs.synchroniser;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import com.apporioinfolabs.synchroniser.db.OfflineLogModel;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.google.gson.Gson;
import com.hypertrack.hyperlog.HyperLog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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


        if(AtsApplication.setIntervalRunningWhenVehicleStops){
            Log.d(TAG, "Starting inner for updating location when driver is at stationary Point.");
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, AtsApplication.locationFetchInterval);
        }

        AtsApplication.syncActions(""+AtsConstants.STOP_LOCATION_SERVICE);

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

        APPORIOLOGS.informativeLog(TAG , "LAT:"+event.pojolocation.getLatitude()+", LONG:"+event.pojolocation.getLongitude());

        try{ atsNotification.updateNotificationView(event); }
        catch (Exception e){ Log.e(TAG , ""+e.getMessage()); }

        Log.d("********* BATTERY LEVEL ", ""+ AtsApplication.BatteryLevel);

        APPORIOLOGS.locationLog(""+event.pojolocation.getLatitude()+
                "__"+event.pojolocation.getLongitude()+
                "__"+event.pojolocation.getAccuracy()+
                "__"+event.pojolocation.getBearing()+
                "__"+ AtsApplication.BatteryLevel+
                "__"+event.pojolocation.getTime());



        if(!AtsApplication.setIntervalRunningWhenVehicleStops){
            onReceiveLocation(event.pojolocation);
//            APPORIOLOGS.debugLog(""+TAG,"Sending location to developer code for running API : (When setIntervalRunningWhenVehicleStops = false)");
        }


        AtsApplication.saveLocationInTempSession(event.getPojolocation().getLatitude(), event.getPojolocation().getLongitude() , event.getPojolocation().getAccuracy() , event.getPojolocation().getBearing() );


        if(AtsApplication.isSocketConnected()){
            SocketListeners.emitLocation(getJSONObjectToEmit(event));
        }

        if(HyperLog.getDeviceLogsCount() >= 25){
            if(AtsApplication.autoLogSynchronization){
                try{
                    AtsApplication.syncHyperLogsStach(gson.toJson(HyperLog.getDeviceLogs(false)));
                }
                catch (Exception e){
                    Log.e(TAG, "Exception while syncing: "+e.getMessage());
                }
            }else{
                HyperLog.deleteLogs();
                Log.i(TAG, "Logs Ignored from sync as auto sync mode is off, also hyper og stack is clear");
            }
        }



        if(getSuccessSqlStach().size() > 0){
            if(AtsApplication.autoLogSynchronization){
                // sync it in a sequential manner
                OfflineLogModel offlineLogModel = getSuccessSqlStach().get(0);
                try{
                    AtsApplication.syncAndDeleteLogsFromDatabse(offlineLogModel.get_log() ,offlineLogModel.get_id());
                }
                catch (Exception e){ Log.e(TAG , ""+e.getMessage());}
            }
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
        AtsApplication.syncActions("stop_location_service");
    }

    private List<OfflineLogModel> getSuccessSqlStach(){
        List<OfflineLogModel> data = AtsApplication.getSqlLite().getAllLogsFromTable();
        List<OfflineLogModel> success_stash = new ArrayList<>();
        List<OfflineLogModel> failed_stash = new ArrayList<>();

        for(int i =0 ; i < data.size() ; i++){

            if(data.get(i).get_log() == null || data.get(i).get_log().equals(null) || data.get(i).get_log().startsWith("null") ){
                failed_stash.add(data.get(i));
            }else{
                success_stash.add(data.get(i));
            }
        }
        return success_stash ;
    }

    public abstract void onReceiveLocation(Location location);


     class TimeDisplayTimerTask extends TimerTask{
        @Override
        public void run() {
            HashMap<String, String> locationSession = AtsApplication.getLocationFromTempSession();
            Location location = new Location(""+locationSession.get(""+AtsConstants.SessionKeys.Provider));
            location.setLatitude(Double.parseDouble(""+ locationSession.get(""+AtsConstants.SessionKeys.Latitude)));
            location.setLongitude(Double.parseDouble(""+ locationSession.get(""+AtsConstants.SessionKeys.Longitude)));
            location.setAccuracy(Float.parseFloat(""+ locationSession.get(""+AtsConstants.SessionKeys.Accuracy)));
            location.setBearing(Float.parseFloat(""+ locationSession.get(""+AtsConstants.SessionKeys.Bearing)));
            onReceiveLocation( location);
        }
    }


}
