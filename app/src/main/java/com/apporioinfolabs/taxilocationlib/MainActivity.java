package com.apporioinfolabs.taxilocationlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.apporioinfolabs.synchroniser.ATSApplication;
import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ATSApplication.setPlayerId("90554-D9CD-49HCR-598H");
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
}
