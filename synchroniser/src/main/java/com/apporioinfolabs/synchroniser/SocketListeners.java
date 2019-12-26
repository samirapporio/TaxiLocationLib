package com.apporioinfolabs.synchroniser;

import android.util.Log;

import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

public class SocketListeners {

    private final static String TAG = "SocketListeners";
    public static final String CONNECT_DEVICE = "connect_device" ;
    public static final String DEVICE_LOCATION = "device_location" ;
    public static final String CASHED_LOCATION = "cashed_location" ;
    public static final String REQUEST_LISTENER = "request_listener" ;
    public static final String REMOVE_LISTENER = "remove_listener" ;
    public static final String LIVE_LOG = "live_log" ;



    public static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AtsApplication.IS_SOCKET_CONNECTED = true;
            Log.d(TAG, "Connected to socket server and emitting device info ");
            EventBus.getDefault().post(""+AtsEventBus.SOCKET_CONNECTED);
            if(AtsApplication.isSocketConnection_allowed){
                AtsApplication.getSocket().emit(CONNECT_DEVICE, OnConnectionInfoManager.getDeviceAndApplicationInfo(), new Ack() {
                    @Override
                    public void call(Object... args) {
                        APPORIOLOGS.informativeLog(TAG, " - - "+ args[0]);
                    }
                });
            }
        }
    };



    public static Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AtsApplication.IS_SOCKET_CONNECTED = false;
            Log.d(TAG , "Disconnected to socket server");
            EventBus.getDefault().post(""+AtsEventBus.SOCKET_DISCONNECTED);
        }
    };



    public static void emitLocation(JSONObject location){
        if(AtsApplication.isSocketConnection_allowed){
            AtsApplication.getSocket().emit(DEVICE_LOCATION, location, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, " - - "+ args[0]);
                }
            });
        }
    }


    public static void emitCashedLocation ( JSONObject cashed_location){
        if(AtsApplication.isSocketConnection_allowed){
            AtsApplication.getSocket().emit(CASHED_LOCATION, cashed_location, new Ack() {
                @Override
                public void call(Object... args) {
                    APPORIOLOGS.informativeLog(TAG, " - - "+ args[0]);
                    EventBus.getDefault().post(new EventLocationSyncSuccess(true));
                }
            });

        }
    }



    public static void emitLog(String log){
        if(AtsApplication.isSocketConnection_allowed && AtsApplication.isLiveLogsAllowed){
            if(AtsApplication.getSocket().connected()){
                AtsApplication.getSocket().emit(LIVE_LOG, log, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.i(TAG, " - - "+ args[0]);
                    }
                });
            }else{
                Log.d(TAG, "Socket is not connected for emitting live logs. ");
            }
        }else{
            Log.i(TAG , "Either Socket Connection or live logs are not allowed. ");
        }
    }







}
