package com.apporioinfolabs.taxilocationlib;

import android.location.Location;
import com.apporioinfolabs.synchroniser.AtsLocationService;
import com.onesignal.OneSignal;

public class UpdateServiceClass extends AtsLocationService {
    @Override
    public void onReceiveLocation(Location location) {

//        APPORIOLOGS.debugLog("####",""+location.getLatitude()+", "+location.getLongitude());
    }
}
