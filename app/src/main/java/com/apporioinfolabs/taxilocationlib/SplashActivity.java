package com.apporioinfolabs.taxilocationlib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.apporioinfolabs.synchroniser.AtsApplication;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class SplashActivity extends BaseActivity implements OSSubscriptionObserver {

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
            String player_id = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId()  ;
            loading_text.setText(""+player_id);
            AtsApplication.setPlayerId(player_id);
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
            AtsApplication.setPlayerId(player_id);
            goTomainActivity();
        }else{
            loading_text.setText("Device Not subscribed to OneSignal so retrying now . . .");
        }
    }

}
