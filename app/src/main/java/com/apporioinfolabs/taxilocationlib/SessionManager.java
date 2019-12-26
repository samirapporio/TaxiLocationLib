package com.apporioinfolabs.taxilocationlib;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {


    private Context context ;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "apporio_poc_prefrences";
    public static final String KEY_EXTRA_DATA = "KEY_EXTRA_DATA";


    public SessionManager(Context context) {
        this.context = context ;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveData(String key, String val){
        editor.putString(key, "" + val);
        editor.commit();
    }

    public String getData(String key){
        return pref.getString(key,"0");
    }

}
