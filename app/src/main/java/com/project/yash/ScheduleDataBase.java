package com.project.yash;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.regex.Pattern;

public class ScheduleDataBase extends SQLiteOpenHelper {
    Context context;
    SQLiteDatabase db;
    public  static  String DATABASE_NAME="yash027";
    public  static  String TABLE_NAME="Schedule_Table";
    public  static int VERSION=2;
    public ScheduleDataBase( Context context) {
        super(context, DATABASE_NAME,null,VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table "+TABLE_NAME+"(stime varchar(20),sun varchar(10),mon varchar(10),tue varchar(10),wed varchar(10),thus varchar(10),fri varchar(10),sat varchar(10));";
        Log.i("data_service",sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public Cursor getData()
    {
        db=this.getReadableDatabase();
        String sql="Select * from "+TABLE_NAME+" order by stime asc;";
        Log.i("data_service",sql);
        return db.rawQuery(sql,null);
    }
    public void removeRow()
    {
        db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from "+TABLE_NAME+";",null);
        if(cursor.getCount()>0)
            db.execSQL("delete from "+TABLE_NAME+";");
        cursor.close();
        Log.i("data_service","All data of table removed");
    }

    public Context getContext() {
        return context;
    }

    public void insert(String []value) {
        db = this.getWritableDatabase();
        String sql = "Insert into " + TABLE_NAME + " values " + "(\"" + value[0] + "\",\"null\",\"" + value[1] + "\",\"" + value[2] + "\",\"" + value[3] + "\",\"" + value[4] + "\",\"" + value[5] + "\",\"" + value[6] + "\");";
        Log.i("data_service", sql);
        db.execSQL(sql);
    }
    void onDestroy()
    {
        db.close();
    }
}
