package com.camsouthcott.runtrainer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cam Southcott on 12/23/2015.
 */
public class RunsDBManager extends SQLiteOpenHelper{

    public static final String RUN_TABLE_UPDATE = "com.camsouthcott.runtrainer.RUN_TABLE_UPDATE";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "RunTrainerDB";
    private static final String RUNS_TABLE = "Runs";
    private static final String RUN_ID = "runID";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String RUN_INTERVAL = "runInterval";
    private static final String WALK_INTERVAL = "walkInterval";

    public RunsDBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RUNS_TABLE + "(" + RUN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIME + " INTEGER NOT NULL, " + DATE + " DEFAULT CURRENT_TIMESTAMP, " + RUN_INTERVAL + " INTEGER NOT NULL, " + WALK_INTERVAL + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Only 1 DB version currently available
    }

    public List<Run> getRuns(){

        List<Run> runsList = new ArrayList<Run>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + RUN_ID + ", " + TIME + ", " + "datetime(" + DATE + ",'localtime'), " + RUN_INTERVAL + ", " + WALK_INTERVAL + " FROM " + RUNS_TABLE + " ORDER BY " + RUN_ID + " DESC",null);

        if(cursor !=null){
            while(cursor.moveToNext()){
                runsList.add(new Run(cursor.getInt(0),cursor.getInt(1),cursor.getString(2), cursor.getInt(3), cursor.getInt(4)));
            }
            cursor.close();
        }

        db.close();
        return runsList;
    }

    public void insertRun(Context context, int runTime, int runInterval, int walkInterval){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TIME, runTime);
        values.put(RUN_INTERVAL,runInterval);
        values.put(WALK_INTERVAL,walkInterval);
        db.insert(RUNS_TABLE, null, values);
        db.close();

        Intent intent = new Intent(RUN_TABLE_UPDATE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
