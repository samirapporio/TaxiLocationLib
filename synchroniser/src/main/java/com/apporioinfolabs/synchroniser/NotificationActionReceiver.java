package com.apporioinfolabs.synchroniser;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apporioinfolabs.apporiologsystem.APPORIOLOGS;

public class NotificationActionReceiver extends BroadcastReceiver {

    public final static String ACTION_SYNC_CASHED = "sync_cashed";
    public final static String ACTION_ONLINE= "online";
    public final static String ACTION_OFFLINE = "offline";
    private final String TAG = "NotificationActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getStringExtra("action");

        if(action.equals(ACTION_SYNC_CASHED)){
            performSyncCashed();
        }if(action.equals(ACTION_OFFLINE)){
            performOffline();
        }
        else if(action.equals(ACTION_ONLINE)){
            performOnline();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }


    @SuppressLint("LongLogTag")
    private void performSyncCashed(){
        APPORIOLOGS.debugLog(TAG , "PERFORM SYNC CASHED");
    }

    @SuppressLint("LongLogTag")
    private void performOnline(){
        APPORIOLOGS.debugLog(TAG , "PERFORM ONLINE API ");
    }

    @SuppressLint("LongLogTag")
    private void performOffline(){
        APPORIOLOGS.debugLog(TAG , "PERFORM OFFLINE API ");
    }



}
