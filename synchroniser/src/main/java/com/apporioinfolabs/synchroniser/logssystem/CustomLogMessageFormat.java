package com.apporioinfolabs.synchroniser.logssystem;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.apporioinfolabs.synchroniser.AtsApplication;
import com.apporioinfolabs.synchroniser.AppInfoManager;
import com.apporioinfolabs.synchroniser.DeviceInfoManager;
import com.hypertrack.hyperlog.LogFormat;

public class CustomLogMessageFormat extends LogFormat {


    public CustomLogMessageFormat(Context context) {
        super(context);
    }


    @Override
    public String getFormattedLogMessage(String logLevelName, String tag, String message, String timeStamp, String senderName, String osVersion, String deviceUUID) {
        String formattedMessage =
                timeStamp + " @:@ " +
                        logLevelName + " @:@ " +
                        tag + " @:@ " +
                        AtsApplication.UNIQUE_NO + " @:@ " +
                        message + " @:@ " +
                        AppInfoManager.getPackageName() + " @:@ " +
                        osVersion + " @:@ " +
                        AppInfoManager.getAppName() + " @:@ " +
                        DeviceInfoManager.getDeviceInfo() + " @:@ " +
                        "AppLogo" + " @:@ " +
                        Build.MANUFACTURER + " @:@ " +
                        Build.MODEL + " @:@ " +
                        AtsApplication.getAtsPrefrences().getString("player_id", "00000000") + " @:@ " +
                        AtsApplication.getAtsPrefrences().getString("extra_data", "Not yet Login or fetched");

        return formattedMessage;
    }

}