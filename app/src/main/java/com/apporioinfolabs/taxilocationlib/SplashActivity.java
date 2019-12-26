package com.apporioinfolabs.taxilocationlib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

public class SplashActivity extends BaseActivity implements OSSubscriptionObserver {

    TextView loading_text, save_btn  ;
    LinearLayout name_layout ;
    EditText name_edt ;
    FrameLayout root ;
    private SessionManager sessionManager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loading_text = findViewById(R.id.loading_text);
        name_layout = findViewById(R.id.name_layout);
        name_edt = findViewById(R.id.name_edt);
        save_btn = findViewById(R.id.save_btn);
        root = findViewById(R.id.root);
        sessionManager = new SessionManager(this);
        getOneSignalPlayeridAndStartActiviy();


        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = name_edt.getText().toString() ;
                if(value.equals("")){
                    Toast.makeText(SplashActivity.this, "Please enter Some identifier", Toast.LENGTH_SHORT).show();
                }else{
                    sessionManager.saveData(SessionManager.KEY_EXTRA_DATA,""+name_edt.getText().toString());
                    AtsApplication.setExtraData(""+name_edt.getText().toString());
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }

    private void getOneSignalPlayeridAndStartActiviy(){
        if(OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getSubscribed()){
            String player_id = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId()  ;
            loading_text.setText(""+player_id);
            AtsApplication.setPlayerId(player_id);
            if(sessionManager.getData(SessionManager.KEY_EXTRA_DATA).equals("0")){
                name_layout.setVisibility(View.VISIBLE);
            }else{
                goTomainActivity();
            }

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
            name_layout.setVisibility(View.VISIBLE);
        }else{
            loading_text.setText("Device Not subscribed to OneSignal so retrying now . . .");
        }
    }

}
