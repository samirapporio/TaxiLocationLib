package com.apporioinfolabs.taxilocationlib;


import org.greenrobot.eventbus.EventBus;

public class TimerTask extends java.util.TimerTask {

    public static String EVENT_NETWORK = "EVENT_NETWORK";

    @Override
    public void run() {
        EventBus.getDefault().post(""+EVENT_NETWORK);
    }
}
