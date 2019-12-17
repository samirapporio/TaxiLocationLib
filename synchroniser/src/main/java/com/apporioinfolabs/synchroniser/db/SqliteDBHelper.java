package com.apporioinfolabs.synchroniser.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SqliteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logdatabase";
    private static final String TABLE_COLLECTED_LOGS = "logtable";
    private static final String KEY_ID = "id";
    private static final String KEY_LOG = "logdata";

    public SqliteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_COLLECTED_LOGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_LOG + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COLLECTED_LOGS);
        onCreate(db);
    }




    public void addLogBunch(String bunch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LOG, bunch);
        db.insert(TABLE_COLLECTED_LOGS, null, values);
        db.close();
    }



    String getLogs(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_COLLECTED_LOGS, new String[] { KEY_ID, KEY_LOG }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor.getString(1);
    }


    public List<OfflineLogModel> getAllLogsFromTable() {
        List<OfflineLogModel> logsdata = new ArrayList<OfflineLogModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_COLLECTED_LOGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                logsdata.add(  new OfflineLogModel(Integer.parseInt(cursor.getString(0)), "" +cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return logsdata;
    }


      //    public int updateLocation(OfflineLogModel offlineDataTable) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_LOG, offlineDataTable.get_latitude());
//        values.put(KEY_LONGITUDE, offlineDataTable.get_longitude());
//        values.put(KEY_ACCURACY, offlineDataTable.get_accuracy());
//        values.put(KEY_BEARING, offlineDataTable.get_bearing());
//        values.put(KEY_SPEED, offlineDataTable.get_speed());
//        // updating row
//        return db.update(TABLE_COLLECTED_LOGS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(offlineDataTable.getID()) });
//    }


    public void deleteLogsbyId(int log_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COLLECTED_LOGS, KEY_ID + " = ?", new String[] { String.valueOf(log_id) });
        db.close();
    }


    public void clearFullTable (){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_COLLECTED_LOGS);
        db.close();
    }

    public int getLogTableCount() {
        String countQuery = "SELECT  * FROM " + TABLE_COLLECTED_LOGS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int mCount = cursor.getCount() ;
        cursor.close();
        return mCount ;
    }

}