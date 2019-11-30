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
    private final static String TAG = ""+ApiSynchroniesr.class.getSimpleName();

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
                        onSync.onSyncSuccess("logs_synced");
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "Logs Not synced "+error.getLocalizedMessage());
                        onSync.onSyncError("error_logs_in_syncing");
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
                        onSync.onSyncSuccess("state_synced");
                    }
                    @Override
                    public void onError(ANError error) {
                        APPORIOLOGS.errorLog(TAG, "Phone state not synced in library "+error.getLocalizedMessage());
                        onSync.onSyncError("state_sync_error");
                    }
                });

    }



    public interface OnSync{
        void onSyncSuccess(String action);
        void onSyncError(String action);
    }

}
