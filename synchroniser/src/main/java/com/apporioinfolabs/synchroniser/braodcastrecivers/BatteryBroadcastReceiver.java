package com.apporioinfolabs.synchroniser.braodcastrecivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;



public class BatteryBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = "BatteryBroadcastReceiver";
    private final static String BATTERY_LEVEL = "level";
    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra(BATTERY_LEVEL, 0);
//        mBatteryLevelText.setText(getString(R.string.battery_level) + " " + level);
//        mBatteryLevelProgress.setProgress(level);
        APPORIOLOGS.informativeLog(TAG , ""+level);
    }
}