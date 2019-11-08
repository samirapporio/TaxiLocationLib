package com.apporioinfolabs.taxilocationlib;

import android.app.PendingIntent;
import com.apporioinfolabs.synchroniser.ATSApplication;


public class MainApplication extends ATSApplication {
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
}
