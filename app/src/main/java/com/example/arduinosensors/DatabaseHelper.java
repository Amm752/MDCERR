package com.example.arduinosensors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Device.db";
    public static final String TABLE_NAME = "patient_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "GX";
    public static final String COL_3 = "GY";
    public static final String COL_4 = "GZ";
    public static final String COL_5 = "F1";
    public static final String COL_6 = "F2";
    public static final String COL_7 = "F3";
    public static final String COL_8 = "F4";
    public static final String COL_9 = "AX";
    public static final String COL_10 = "AY";
    public static final String COL_11 = "AZ";
    public static final String COL_12 = "SYNC";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,GX STRING,GY STRING,GZ STRING," +
                "F1 STRING,F2 STRING,F3 STRING,F4 STRING,AX STRING,AY STRING,AZ STRING,SYNC STRING)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String Gx,String Gy,String Gz,String f1,String f2,String f3,String f4
            ,String Ax,String Ay,String Az,String Sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,Gx);
        contentValues.put(COL_3,Gy);
        contentValues.put(COL_4,Gz);
        contentValues.put(COL_5,f1);
        contentValues.put(COL_6,f2);
        contentValues.put(COL_7,f3);
        contentValues.put(COL_8,f4);
        contentValues.put(COL_9,Ax);
        contentValues.put(COL_10,Ay);
        contentValues.put(COL_11,Az);
        contentValues.put(COL_12,Sync);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from" + TABLE_NAME,null);
        return res;
    }
}