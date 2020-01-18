package com.replon.www.womensafety;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;


public class DatabaseHelperGuardians extends SQLiteOpenHelper {

    //id for this row is today's date

    public static final String DATABASE_NAME="GuardianRegistration.db";
    public static final String TABLE_NAME="guardian_table";


    public static final String ID = "ID";
    public static final String NAME="name";
    public static final String PHONE="phone";


    public static final String TAG = "DatabaseHelperSelf";


    public DatabaseHelperGuardians(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "+TABLE_NAME+ "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+NAME+" TEXT,"+PHONE+" TEXT)");
        Log.i(TAG,"Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
        Log.i(TAG,"onUpgrade running");

    }

    public boolean insertData(String name,String phone){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();

        contentValues.put(NAME,name);
        contentValues.put(PHONE,phone);



        //if data not inserted it will return -1 else it will show the values

        long result=db.insert(TABLE_NAME,null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }

    }

    public Cursor getAllData(){
        SQLiteDatabase db=this.getWritableDatabase();


        Cursor res=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return res;
    }


//
//
//    public boolean updateData(String name, String phone){
//
//        SQLiteDatabase db=this.getWritableDatabase();
//        ContentValues contentValues=new ContentValues();
//
//
//        contentValues.put(NAME,name);
//        contentValues.put(PHONE,phone);
//
//
//        //if data not inserted it will return -1 else it will show the values
//
//        long result=db.update(TABLE_NAME,contentValues,"ID = ?",new String[]{ "1" });
//
//        if(result==-1){
//            return false;
//        }else{
//            return true;
//        }
//
//    }

}