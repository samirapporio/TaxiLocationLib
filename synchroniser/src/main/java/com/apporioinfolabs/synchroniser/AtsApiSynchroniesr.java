package com.apporioinfolabs.synchroniser;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONObject;

import java.util.TimeZone;

public class AtsApiSynchroniesr {

    private OnSync onSync ;

    private final static String TAG = "AtsApiSynchroniesr";

    public AtsApiSynchroniesr(OnSync onSync){
        this.onSync = onSync ;
    }

    // (1) when app minimise  (2) When user asked it via sending notification.
    public void syncLogsAccordingly( ){
        Log.d(TAG, "Syncing Logs to Log panel");

        try{
            Log.d(TAG , "STATUS : "+AppInfoManager.getAppStatusEncode());
            APPORIOLOGS.appStateLog(AppInfoManager.getAppStatusEncode());}catch (Exception e){}
        AndroidNetworking.post(""+ AtsApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key", AtsApplication.getGson().toJson(HyperLog.getDeviceLogs(false)))
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response,ModelResultChecker.class);
                            if(modelResultChecker.getResult() == 1 ){
                                Log.i(TAG, "Logs Synced Successfully ");
                                HyperLog.deleteLogs();
                                onSync.onSyncSuccess(""+AtsConstants.SYNC_EXISTING_LOGS);
                            }else{
                                Log.e(TAG, "Logs Not synced from server got message "+modelResultChecker.getMessage());
                                onSync.onSyncError(""+AtsConstants.SYNC_EXISTING_LOGS_ERROR);
                            }
                        }catch (Exception e){
                            Log.e(TAG, "Logs Not synced with error code: "+e.getMessage());
                            onSync.onSyncError(""+AtsConstants.SYNC_EXISTING_LOGS_ERROR);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced with error code: "+error.getErrorCode());
                        onSync.onSyncError(""+AtsConstants.SYNC_EXISTING_LOGS_ERROR);
                    }
                });
    }

    public void syncPhoneState(JSONObject jsonObject){
        Log.d(TAG, "Syncing Phone state to panel");
        AndroidNetworking.post(""+ AtsApplication.EndPoint_sync_App_State)
                .addJSONObjectBody(jsonObject)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response,ModelResultChecker.class);
                if(modelResultChecker.getResult() == 1){
                    Log.i(TAG, "State Synced Successfully ");
                    onSync.onSyncSuccess(""+AtsConstants.SYNC_APP_STATE);
                }else{
                    onSync.onSyncError("Result:0  State Not Synced:"+modelResultChecker.getMessage());
                    Log.e(TAG , "Result:0  State Not Synced:"+modelResultChecker.getMessage());
                }
            }

            @Override
            public void onError(ANError error) {
                Log.e(TAG, "Phone state not synced in library "+error.getLocalizedMessage());
                onSync.onSyncError(""+AtsConstants.SYNC_APP_STATE_ERROR+" "+error.getMessage());
            }
        });


    }

    public void syncStraightAction(final String action ){
        Log.d(TAG, "Syncing straight action :"+action);
        AndroidNetworking.post(""+ AtsApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("action",""+action)
                .setTag("syncing")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response, ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            onSync.onSyncSuccess(""+action);
                            Log.i(TAG , "Action synced successfully: "+action);
                        }else{
                            Log.e(TAG , "Result:0 Action Not Synced:"+ ""+action+ "  Error:"+modelResultChecker.getMessage());
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        onSync.onSyncError("Error while syncing "+action+",   Error: "+error.getErrorCode());
                        Log.e(TAG, "Action state not synced from library "+error.getLocalizedMessage());
                    }
                });

    }

    public void syncHyperLogStashFromService(String jsondata, final String action){
        Log.d(TAG, "Syncing Logs via service :"+action);
        try{APPORIOLOGS.appStateLog(AppInfoManager.getAppStatusEncode());}catch (Exception e){}
        AndroidNetworking.post(""+ AtsApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key",jsondata)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response, ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            onSync.onSyncError(action);
                            HyperLog.deleteLogs();
                            Log.i(TAG , "Logs Synced Successfully with action: "+action);
                        }else{
                            Log.e(TAG , "Result:0  Logs Not Synced:"+action+ "  Error:"+modelResultChecker.getMessage());
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        AtsApplication.getSqlLite().addLogBunch(""+ AtsApplication.getGson().toJson(HyperLog.getDeviceLogs(false)));
                        HyperLog.deleteLogs();
                        onSync.onSyncError("Logs Not synced now saving things to Database"+error.getLocalizedMessage());

                    }
                });

    }

    public void syncAndDeleteLogsFromDatabse(String jsonData, final int log_stach_id, final String action){
        Log.d(TAG , "Syncing logs from database Stack");
        AndroidNetworking.post(""+ AtsApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key",jsonData)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response, ModelResultChecker.class);
                        if(modelResultChecker.getResult() == 1){
                            AtsApplication.getSqlLite().deleteLogsbyId(log_stach_id);
                            onSync.onSyncError(action);
                            Log.i(TAG , "Logs Synced Successfully from database stack_id:"+log_stach_id+"  action:"+action);
                        }else{
                            Log.e(TAG , "Result:0   Logs Not Synced from database:"+action+ " Error:"+modelResultChecker.getMessage());
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        onSync.onSyncError("Logs not synced via API from database");
                        Log.e(TAG, "Logs not synced so transferred it into SQL rate Database"+"  |  ERROR: "+ error.getMessage());
                    }
                });

    }

    public void setTrip(final String flag, final String tag, final String locationSubmissionURL, final OnAtsTripSetterListeners onAtsTripSetterListeners) throws Exception{

        // first library will sync log for ensuring that all location is synced successfully
        // after location sync it will update flag value then finally it will delete the existing tags


        try{
            Log.d(TAG , "STATUS : "+AppInfoManager.getAppStatusEncode());
            APPORIOLOGS.appStateLog(AppInfoManager.getAppStatusEncode());}catch (Exception e){}
        AndroidNetworking.post(""+ AtsApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key", AtsApplication.getGson().toJson(HyperLog.getDeviceLogs(false)))
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response,ModelResultChecker.class);
                            if(modelResultChecker.getResult() == 1 ){
                                Log.i(TAG, "Logs Synced Successfully while setting trip.");
                                startSettingFlagNow(flag, tag, locationSubmissionURL,  onAtsTripSetterListeners);
                            }else{
                                Log.e(TAG, "Logs Not synced from server while setting trip got message "+modelResultChecker.getMessage());
                                onSync.onSyncError(""+AtsConstants.SYNC_EXISTING_LOGS_ERROR);
                                onAtsTripSetterListeners.onTripSetFail("Logs Not synced from server while setting trip got message "+modelResultChecker.getMessage());
                            }
                        }catch (Exception e){
                            Log.e(TAG, "Logs Not synced with error code: "+e.getMessage());
                            onAtsTripSetterListeners.onTripSetFail("Logs Not synced while setting trip with error code: "+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced with error code: "+error.getErrorCode());
                        onSync.onSyncError(""+AtsConstants.SYNC_EXISTING_LOGS_ERROR);
                    }
                });

    }


    private void startSettingFlagNow(String flag, String tag, String locationSubmissionURL, final OnAtsTripSetterListeners onAtsTripSetterListeners) throws Exception{

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flag",""+flag);
        jsonObject.put("device",""+AtsApplication.UNIQUE_NO);
        jsonObject.put("package_name",""+AppInfoManager.getPackageName());
        jsonObject.put("location_submission_url",""+locationSubmissionURL);
        jsonObject.put("tag",""+tag);


        Log.d(TAG, "Setting trip with flag:"+flag);
        AndroidNetworking.post(""+ AtsApplication.EndPoint_set_trip)
                .addJSONObjectBody(jsonObject)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(""+response,ModelResultChecker.class);
                if(modelResultChecker.getResult() == 1){
                    Log.i(TAG, "Trip Flag set Successfully ");
                    onAtsTripSetterListeners.onTripSetSuccess("Trip Flag set Successfully ");

                }else{
                    onAtsTripSetterListeners.onTripSetFail("Result:0  Unable to set trip flag:"+modelResultChecker.getMessage());
                    Log.e(TAG , "Result:0  State Not Synced:"+modelResultChecker.getMessage());
                }
            }

            @Override
            public void onError(ANError error) {
                Log.e(TAG, "Trip state not set in library: "+error.getLocalizedMessage());
                onAtsTripSetterListeners.onTripSetFail("Trip state not set in library: "+error.getLocalizedMessage());
            }
        });
        HyperLog.deleteLogs();
    }

    public interface OnSync{
        void onSyncSuccess(String action);
        void onSyncError(String message);
    }



}
