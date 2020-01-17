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
    public static final String TOKEN = "token" ;
    public static final String APP_STATE = "app_state" ;



    public static Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            emitAuthenticator();

        }
    };



    public static Emitter.Listener onDisconnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AtsApplication.IS_SOCKET_CONNECTED = false;
            EventBus.getDefault().post(""+AtsEventBus.SOCKET_DISCONNECTED);
            AtsApplication.atsSocketConnectionHandlers.atsServerConnectionState(false);
        }
    };



    public static void emitAuthenticator (){
        AtsApplication.getSocket().emit(TOKEN, ""+AtsApplication.TOKEN, new Ack() {
            @Override
            public void call(Object... args) {

                /* if device is authenticated then need to call connect device emitor
                 method that will take all device values and store with unique
                 identifier over sever accordingly */

                try{
                 String data = ""+args[0] ;
                 ModelResultChecker modelResultChecker = AtsApplication.getGson().fromJson(data, ModelResultChecker.class);
                 if(modelResultChecker.getResult() == 1){
                     connectDevice();
                 }else{
                     Log.e(TAG, "Invalid Token Provided");
                 }
                }catch (Exception e){
                    Log.e(TAG , ""+e.getMessage());
                }
            }
        });

    }



    public static void connectDevice(){
        AtsApplication.IS_SOCKET_CONNECTED = true;
        EventBus.getDefault().post(""+AtsEventBus.SOCKET_CONNECTED);
        AtsApplication.atsSocketConnectionHandlers.atsServerConnectionState(true);
        AtsApplication.getSocket().emit(CONNECT_DEVICE, OnConnectionInfoManager.getDeviceAndApplicationInfo(), new Ack() {
            @Override
            public void call(Object... args) {
                emitAppState();
            }
        });

    }


    public static void emitLocation(JSONObject location){
        if(AtsApplication.isSocketConnected() && AtsApplication.isSyncLocationOnSocket){
            AtsApplication.getSocket().emit(DEVICE_LOCATION, location, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, " - - "+ args[0]);
                }
            });
        }
    }


    public static void emitCashedLocation ( JSONObject cashed_location){
        AtsApplication.getSocket().emit(CASHED_LOCATION, cashed_location, new Ack() {
            @Override
            public void call(Object... args) {
                APPORIOLOGS.informativeLog(TAG, " - - "+ args[0]);
                EventBus.getDefault().post(new EventLocationSyncSuccess(true));
            }
        });

    }


    public static void emitLog(String log){
        if(AtsApplication.getSocket().connected() && AtsApplication.isLiveLogsAllowed ){
            AtsApplication.getSocket().emit(LIVE_LOG, log, new Ack() {
                @Override
                public void call(Object... args) {
                    Log.i(TAG, " - - "+ args[0]);
                }
            });
        }else{
            Log.d(TAG, "Socket is not connected for emitting live logs. ");
        }
    }


    public static void emitAppState(){
        if(AtsApplication.getSocket().connected()){
            try{
                String state_to_sync = AppInfoManager.getAppStatusEncode();
                AtsApplication.getSocket().emit(APP_STATE, state_to_sync, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.i(TAG, " **** - - **** "+ args[0]);
                    }
                });
            }catch (Exception e){
                Log.e(TAG , "Unable to get app state "+e.getMessage());
            }
        }else{
            Log.d(TAG, "Socket is not connected for emitting live logs. ");
        }
    }







}
