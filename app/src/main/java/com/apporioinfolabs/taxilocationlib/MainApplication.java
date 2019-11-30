package com.apporioinfolabs.taxilocationlib;

import android.app.PendingIntent;
import android.widget.Toast;

import com.apporioinfolabs.synchroniser.ATSApplication;
import com.onesignal.OneSignal;


public class MainApplication extends ATSApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new MyNotificationOpenHandler())
                .setNotificationReceivedHandler(new MyNotificationReceiverhandler())
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    @Override
    public boolean setDeveloperMode() {
        return true;
    }

    @Override
    public int setSmallNotificationIcons() {
        return R.drawable.accuracy_icon;
    }

    @Override
    public int setLargeNotificationIcons() {
        return R.drawable.accuracy_icon;
    }

    @Override
    public long setLocationFetchInterval() {
        return 7000;
    }

    @Override
    public String setNotificationOnlineText() {
        return "You are online now";
    }

    @Override
    public String setNotificationMakingOnlineText() {
        return "Making you online";
    }

    @Override
    public PendingIntent setNotificationClickIntent() {
        return null;
    }

    @Override
    public boolean setIntervalRunningOnVehicleStop() {
        return true;
    }

    @Override
    public String dataSynced(String data) {
        Toast.makeText(this, ""+data, Toast.LENGTH_SHORT).show();
        return data;
    }

    @Override
    public String dataSyncedError(String s) {
        Toast.makeText(this, "Error in syncing data by library", Toast.LENGTH_SHORT).show();
        return s;
    }
}
