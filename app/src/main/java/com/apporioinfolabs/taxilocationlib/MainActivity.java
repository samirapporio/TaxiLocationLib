package com.apporioinfolabs.taxilocationlib;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.apporioinfolabs.synchroniser.ATSApplication;
import com.apporioinfolabs.synchroniser.AtsLocationService;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MainActivity extends Activity implements OSSubscriptionObserver {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.addSubscriptionObserver(this);
        ATSApplication.setPlayerId(""+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
        ATSApplication.setExtraData("{driver_id:1043,driver_name:Samir goel,driver_email:samir@apporio.com,driver_vehicle_no:DL-3656}");

        findViewById(R.id.add_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APPORIOLOGS.debugLog("MainActivity","Some Log");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, UpdateServiceClass.class));
        } else { // normal
            startService(new Intent(this, UpdateServiceClass.class));
        }









        Toast.makeText(this, ""+isServiceRunning(UpdateServiceClass.class), Toast.LENGTH_SHORT).show();

        findViewById(R.id.phone_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ATSApplication.syncPhoneState();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        Toast.makeText(this, ""+stateChanges.toString(), Toast.LENGTH_SHORT).show();
    }






    private  boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
