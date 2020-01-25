package com.apporioinfolabs.synchroniser;

import android.os.Handler;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;

public class AtsSocket {

    private Handler mHandler;



    public AtsSocket(){
        mHandler = new Handler();
    }



    public void startListen(final String keyToListen, final OnAtsSocketListener onAtsSocketListener){

        if(AtsApplication.getSocket().connected()){
            AtsApplication.getSocket().emit(SocketListeners.REQUEST_LISTENER, ""+keyToListen, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion("Key registered Successfully: "+keyToListen);
                                AtsApplication.removePreviousListeners();
                                AtsApplication.getSocket().on(keyToListen, new Emitter.Listener() {
                                    @Override
                                    public void call(final Object... margs) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() { onAtsSocketListener.onMessageReceived(""+margs[0]); }
                                        }); }
                                });
                            }else{
                                onAtsSocketListener.onFailureRegistration("Key registration failed: "+keyToListen);
                            }
                        }
                    });
                }
            });
        }else{
            onAtsSocketListener.onMessageReceived("Socket Not Connected");
        }
    }


    public  void startListeningScreenId(final String screenIdToRegisterAndListen,final OnAtsSocketListener onAtsSocketListener ){
        if(AtsApplication.getSocket().connected()){
            AtsApplication.getSocket().emit(SocketListeners.ADD_SCREEN_ID, ""+screenIdToRegisterAndListen, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion("Screen ID registered Successfully: "+screenIdToRegisterAndListen);
                                AtsApplication.removePreviousListeners();
                                AtsApplication.getSocket().on(screenIdToRegisterAndListen, new Emitter.Listener() {
                                    @Override
                                    public void call(final Object... margs) {
                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() { onAtsSocketListener.onMessageReceived(""+margs[0]); }
                                        }); }
                                });
                            }else{
                                onAtsSocketListener.onFailureRegistration("Screen ID registration failed: "+screenIdToRegisterAndListen);
                            }
                        }
                    });
                }
            });
        }else{
            onAtsSocketListener.onMessageReceived("Socket Not Connected");
        }
    }



    public void stopListen (final String keyToRemove , final OnAtsSocketListener onAtsSocketListener){
        if(AtsApplication.getSocket().connected()){
            AtsApplication.getSocket().emit(SocketListeners.REMOVE_LISTENER, keyToRemove, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion(keyToRemove+" removed successfully");
                                AtsApplication.getSocket().off(keyToRemove);
                            }else{
                                onAtsSocketListener.onFailureRegistration("Unable to remove key: "+keyToRemove);
                            }
                        }
                    });
                }
            });
        }else{
            onAtsSocketListener.onMessageReceived("Socket Not Connected");
        }
    }


    public void stopListenScreenId (final String screenIdToRemove , final OnAtsSocketListener onAtsSocketListener){
        if(AtsApplication.getSocket().connected()){
            AtsApplication.getSocket().emit(SocketListeners.REMOVE_SCREEN_ID, screenIdToRemove, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion(screenIdToRemove+" removed successfully");
                                AtsApplication.getSocket().off(screenIdToRemove);
                            }else{
                                onAtsSocketListener.onFailureRegistration("Unable to remove key: "+screenIdToRemove);
                            }
                        }
                    });
                }
            });
        }else{
            onAtsSocketListener.onMessageReceived("Socket Not Connected");
        }
    }



    public void stopAllListenersListen(String key){
        AtsApplication.getSocket().off(key);
    }

    public interface OnAtsSocketListener{
        void onMessageReceived(String message);
        void onSuccessRegistrataion(String message);
        void onFailureRegistration(String message);
    }

}
