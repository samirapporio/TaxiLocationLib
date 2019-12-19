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


    /*
    0 --> timestamp of mobile
    1 --> log type
    2 --> tag
    3 --> Unique device no
    4 --> message
    5 --> package name
    6 --> operating system version
    7 --> application name
    8 --> app logo
    9 --> Manufacture
    10 -> Phone Model
     */

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
                        "AppLogo" + " @:@ " +
                        Build.MANUFACTURER + " @:@ " +
                        Build.MODEL ;


        Log.d("********--> ",""+formattedMessage);

        return formattedMessage;
    }

}