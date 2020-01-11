package com.apporioinfolabs.taxilocationlib;

import android.util.Log;

import com.apporioinfolabs.synchroniser.handlers.AtsSocketConnectionHandlers;

public class AtsConnection implements AtsSocketConnectionHandlers {

    private static final String TAG = "AtsConnection";

    @Override
    public void atsServerConnectionState(boolean value) {
        Log.d("" +TAG,""+value);
    }
}
