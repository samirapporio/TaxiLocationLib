package com.apporioinfolabs.synchroniser;

import android.os.Handler;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

public class AtsSocket {

    private Handler mHandler;



    public AtsSocket(){
        mHandler = new Handler();
    }



    public void startListen(final String keyToListen, final OnAtsSocketListener onAtsSocketListener){

        if(ATSApplication.getSocket().connected()){
            ATSApplication.getSocket().emit(SocketListeners.REQUEST_LISTENER, ""+keyToListen, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion("Key registered Successfully: "+keyToListen);
                                ATSApplication.removePreviousListeners();
                                ATSApplication.getSocket().on(keyToListen, new Emitter.Listener() {
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

    public void stopListen (final String keyToRemove , final OnAtsSocketListener onAtsSocketListener){
        if(ATSApplication.getSocket().connected()){
            ATSApplication.getSocket().emit(SocketListeners.REMOVE_LISTENER, keyToRemove, new Ack() {
                @Override
                public void call(final Object... args) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(args[0].equals("1")){
                                onAtsSocketListener.onSuccessRegistrataion(keyToRemove+" removed successfully");
                                ATSApplication.getSocket().off(keyToRemove);
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

    public void stopAllListenersListen(String key){
        ATSApplication.getSocket().off(key);
    }

    public interface OnAtsSocketListener{
        void onMessageReceived(String message);
        void onSuccessRegistrataion(String message);
        void onFailureRegistration(String message);
    }

}
