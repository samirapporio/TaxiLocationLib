package com.apporioinfolabs.synchroniser.db;

public class OfflineLogModel {



    int _id;
    String _log;


    public OfflineLogModel(){   }
    public OfflineLogModel(int id, String _log){
        this._id = id;
        this._log = _log;
    }

    public OfflineLogModel(String _log){

        this._log = _log;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public int get_id() {
        return this._id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_log() {
        return this._log;
    }

    public void set_latitude(String _log) {
        this._log = _log;
    }


}