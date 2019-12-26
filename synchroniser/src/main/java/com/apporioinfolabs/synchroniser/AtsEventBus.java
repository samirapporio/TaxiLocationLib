package com.apporioinfolabs.synchroniser;

import org.greenrobot.eventbus.EventBus;

public class AtsEventBus extends EventBus {
    public static final String SOCKET_CONNECTED = "Socket connected";
    public static final String SOCKET_DISCONNECTED = "Socket disconnected";
}
