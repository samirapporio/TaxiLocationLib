package com.apporioinfolabs.taxilocationlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class SplashActivity extends Activity implements OSSubscriptionObserver {

    TextView loading_text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loading_text = findViewById(R.id.loading_text);
        getOneSignalPlayeridAndStartActiviy();
    }

    private void getOneSignalPlayeridAndStartActiviy(){
        if(OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getSubscribed()){
            loading_text.setText(""+OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            goTomainActivity();
        }else{
            OneSignal.addSubscriptionObserver(this);
        }
    }

    private void goTomainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1500);

    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() && stateChanges.getTo().getSubscribed()) {
            String player_id = stateChanges.getTo().getUserId();
            loading_text.setText("Player ID: "+player_id);
            goTomainActivity();
        }else{
            loading_text.setText("Device Not subscribed to OneSignal so retrying now . . .");
        }
    }

}
