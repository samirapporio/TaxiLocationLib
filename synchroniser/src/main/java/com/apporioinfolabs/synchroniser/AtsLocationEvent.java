package com.apporioinfolabs.synchroniser;

import android.location.Location;

import org.json.JSONObject;

public class AtsLocationEvent {

    public JSONObject location ;
    public Location pojolocation ;

    public JSONObject getLocation() { return location; }

    public void setLocation(JSONObject location) {
        this.location = location;
    }




    public Location getPojolocation() {
        return pojolocation;
    }

    public void setPojolocation(Location pojolocation) {
        this.pojolocation = pojolocation;
    }




}
