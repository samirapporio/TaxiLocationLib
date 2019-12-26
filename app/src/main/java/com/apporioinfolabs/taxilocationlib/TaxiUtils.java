package com.apporioinfolabs.taxilocationlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class TaxiUtils {

    public static boolean isInternetconnection (Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public boolean getConnectionType(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMetered = cm.isActiveNetworkMetered();
        return isMetered;
    }

}
