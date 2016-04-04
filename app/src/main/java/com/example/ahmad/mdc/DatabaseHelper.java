package com.example.ahmad.mdc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


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
    public void bulkInsertOneHundredRecords(String SAMPLE_TABLE_NAME,String[] data, int size) {

        String sql = "INSERT INTO "+ SAMPLE_TABLE_NAME +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0; i<size; i++) {
            statement.clearBindings();
            //statement.bindString(1, "Test");
            statement.bindString(2, data[i]);
            statement.bindString(3, data[i]);
            statement.bindString(4, data[i]);
            statement.bindString(5, data[i]);
            statement.bindString(6, data[i]);
            statement.bindString(7, data[i]);
            statement.bindString(8, data[i]);
            statement.bindString(9, data[i]);
            statement.bindString(10, data[i]);
            statement.bindString(11, data[i]);
            statement.bindString(12, data[i]);
            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    public SQLiteStatement buildStatement(){
        String sql = "INSERT INTO "+ "patient_table" +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        return statement;
    }

    public void setTransaction(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void insertTestData(String[] data, int rows) {
        String sql = "insert into producttable (name, description, price, stock_available) values (?, ?, ?, ?);";

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);

        for (int i = 0; i < rows; i++) {
            //generate some values

            stmt.bindString(2, data[i]);
            stmt.bindString(3, data[i+1]);
            stmt.bindString(4, data[i+2]);
            stmt.bindString(5, data[i+3]);
            stmt.bindString(6, data[i+4]);
            stmt.bindString(7, data[i+5]);
            stmt.bindString(8, data[i+6]);
            stmt.bindString(9, data[i+7]);
            stmt.bindString(10, data[i+8]);
            stmt.bindString(11, data[i+9]);
            stmt.bindString(12, data[i+10]);
            i = i+10;
            long entryID = stmt.executeInsert();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        this.close();
    }

    public void insertData(String Gx,String Gy,String Gz,String f1,String f2,String f3,String f4
            ,String Ax,String Ay,String Az,String Sync) {
        String sql = "INSERT INTO "+ "patient_table" +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
            statement.clearBindings();
            //statement.bindString(1, "Test");
            statement.bindString(2, Gz);
            statement.bindString(3, Gy);
            statement.bindString(4, Gz);
            statement.bindString(5, f1);
            statement.bindString(6, f2);
            statement.bindString(7, f3);
            statement.bindString(8, f4);
            statement.bindString(9, Ax);
            statement.bindString(10, Ay);
            statement.bindString(11, Az);
            statement.bindString(12, Sync);
            statement.execute();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    /* This works!!!!!!!!!!!!!!! Just slow
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

    */
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT SYNC FROM " + TABLE_NAME,null);

        // Cursor res = db.query("patient_table", new String[]{"ColumnName"} , null, null, null, null, null);
        return res;
    }
    public Cursor queryLast(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY ID DESC LIMIT 1", null);

        // Cursor res = db.query("patient_table", new String[]{"ColumnName"} , null, null, null, null, null);
        return res;
    }
    public void deleteDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("patient_table", null,null);
    }
}