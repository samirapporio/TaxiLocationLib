package com.apporioinfolabs.synchroniser;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;


import com.apporioinfolabs.synchroniser.logssystem.APPORIOLOGS;

import org.json.JSONArray;
import org.json.JSONObject;

public class AppInfoManager {
    public final static String TAG = "AppInfoManager";



    public static JSONObject getApplicafionInfo (){
        JSONObject jsonObject = new JSONObject();
        PackageManager pm = AtsApplication.mContext.getPackageManager() ;
        String packagename = AtsApplication.mContext.getApplicationContext().getPackageName();
        try{

            jsonObject.put("package_name", ""+packagename);
            jsonObject.put("app_name", ""+(String) pm.getApplicationLabel(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA)));
            jsonObject.put("permissions", getPermissionWithStatus());

        }catch (Exception e){
            APPORIOLOGS.warningLog(TAG , ""+e.getMessage());
        }


        return jsonObject ;

    }

    public static String getPackageName (){
        return ""+ AtsApplication.mContext.getApplicationContext().getPackageName();
    }

    public static String getAppName() {
        try{
            PackageManager pm = AtsApplication.mContext.getPackageManager() ;
            String packagename = AtsApplication.mContext.getApplicationContext().getPackageName();
            return ""+(String) pm.getApplicationLabel(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
        }catch (Exception e ){
            // dont want to upload this as log
            return "NA";
        }

    }




    public static Drawable getApplicationLogo() throws Exception{
        PackageManager pm = AtsApplication.mContext.getPackageManager() ;
        String packagename = AtsApplication.mContext.getApplicationContext().getPackageName();
        return  pm.getApplicationLogo(pm.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
    }

    public static JSONArray getPermissionWithStatus(){

        JSONArray permissionsarray = new JSONArray();
        try{
            String packagename = AtsApplication.mContext.getApplicationContext().getPackageName();
            PackageInfo packageInfo = AtsApplication.mContext.getPackageManager().getPackageInfo(packagename,PackageManager.GET_PERMISSIONS);
            for(int i = 0 ; i < packageInfo.requestedPermissions.length ; i++){
                permissionsarray.put(new JSONObject().put("name",packageInfo.requestedPermissions[i]).put("status",hasPermission(packageInfo.requestedPermissions[i])));

            }
        }catch (Exception e){
            APPORIOLOGS.warningLog(TAG , ""+e.getMessage());
        }
        return permissionsarray ;


    }

    private static String isLocationPermissionCode() throws Exception{
        String value = "lf";
        String packagename = AtsApplication.mContext.getApplicationContext().getPackageName();
        PackageInfo packageInfo = AtsApplication.mContext.getPackageManager().getPackageInfo(packagename,PackageManager.GET_PERMISSIONS);

        for(int i =0 ; i < packageInfo.requestedPermissions.length ; i++){
            if(packageInfo.requestedPermissions[i].equals("android.permission.ACCESS_FINE_LOCATION") && hasPermission("android.permission.ACCESS_FINE_LOCATION")){
                value = "lt" ;
            }
        }

        return value;
    }

    private static String isAppInForegroundCode(){
        if(AtsApplication.isAppInForegorund()){
            return "fore";
        }else{
            return "back";
        }
    }




    //   services names array

    //   package name
    //   device uuid
    //   permission-> location, read and write, camera, audio,
    //   foreground or background

    //   com.apporio.apporiotaxilocationlib_udid_lt_f_classone_classnametwo_classnamingthree_classnamingotherexamplefour



    public static String getAppStatusEncode() throws Exception{

        String value = ""
                + AtsApplication.mContext.getApplicationContext().getPackageName() +"_"
                + Settings.Secure.getString(AtsApplication.mContext.getContentResolver(), Settings.Secure.ANDROID_ID)+"_"
                + isLocationPermissionCode()+"_"
                + isAppInForegroundCode()+"_"
                + AtsApplication.getListofRunningServices();

        return ""+value;
    }


    private static boolean hasPermission(String permission) {
        int res = AtsApplication.mContext.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


}
