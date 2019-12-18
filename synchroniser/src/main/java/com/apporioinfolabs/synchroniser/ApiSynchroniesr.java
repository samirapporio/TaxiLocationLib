package com.apporioinfolabs.synchroniser;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.hypertrack.hyperlog.HyperLog;

import org.json.JSONObject;

import java.util.TimeZone;

public class ApiSynchroniesr {

    private OnSync onSync ;

    private final static String TAG = "ApiSynchroniesr";

    public ApiSynchroniesr(OnSync onSync){
        this.onSync = onSync ;
    }

    public void syncLogsAccordingly( ){
        Log.d(TAG, "Syncing Logs to Log panel");
        AndroidNetworking.post(""+ATSApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("key",ATSApplication.getGson().toJson(HyperLog.getDeviceLogs(false)))
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Logs Synced Successfully ");
                        HyperLog.deleteLogs();
                        onSync.onSyncSuccess(""+AtsConstants.SYNC_ESISTING_LOGS);
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced with error code: "+error.getErrorCode());
                        onSync.onSyncError(""+AtsConstants.SYNC_ESISTING_LOGS_ERROR);
                    }
                });
    }

    public void syncPhoneState(JSONObject jsonObject){



        AndroidNetworking.post(""+ATSApplication.EndPoint_sync_App_State)
                .addJSONObjectBody(jsonObject)
                .setTag("log_sync")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "State Synced Successfully ");
                        onSync.onSyncSuccess(""+AtsConstants.SYNC_APP_STATE);
                    }
                    @Override
                    public void onError(ANError error) {
                        APPORIOLOGS.errorLog(TAG, "Phone state not synced in library "+error.getLocalizedMessage());
                        onSync.onSyncError(""+AtsConstants.SYNC_APP_STATE_ERROR);
                    }
                });

    }

    public void syncStraightAction(final String action ){


        AndroidNetworking.post(""+ATSApplication.EndPoint_add_logs)
                .addBodyParameter("timezone", TimeZone.getDefault().getID())
                .addBodyParameter("action",""+action)
                .setTag("syncing")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onSync.onSyncSuccess(""+action);
                    }
                    @Override
                    public void onError(ANError error) {

                        onSync.onSyncError("Error while syncing action"+error.getErrorCode());
                    }
                });

    }

    public interface OnSync{
        void onSyncSuccess(String action);
        void onSyncError(String action);
    }

}
