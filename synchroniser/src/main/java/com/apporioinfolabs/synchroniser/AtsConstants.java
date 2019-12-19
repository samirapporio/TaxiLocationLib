package com.apporioinfolabs.synchroniser;

public interface AtsConstants {

    String SYNC_APP_STATE = "sync_app_state";
    String START_LOCATION_SERVICE = "start_location_service";
    String STOP_LOCATION_SERVICE = "stop_location_service";
    String SYNC_EXISTING_LOGS = "sync_existing_logs"; // will sync existing hyper logs rely in hyper database
    String SYNC_EXISTING_LOGS_ERROR = "sync_existing_logs_error";
    String SYNC_EXISTING_LOGS_FROM_DATABASE = "sync_existing_logs_from_database";
    String CLEAR_ONE_SIGNAL_NOTIFICATIONS = "clear_OneSignal_Notifications";
    String SYNC_APP_STATE_ERROR = "sync_app_state_error";

    interface SessionKeys{
        String Latitude = "LAT";
        String Longitude = "LONG";
        String Provider = "PROVIDER";
        String Accuracy = "ACCURACY";
        String Bearing = "BEARING";
    }

}
