package com.apporioinfolabs.taxilocationlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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


    }
}
