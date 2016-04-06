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


public class RunsDBManager extends SQLiteOpenHelper{

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "RunTrainerDB";
    private static final String RUNS_TABLE = "Runs";
    private static final String RUN_ID = "runID";
    private static final String GLOBAL_RUN_ID = "globalRunID";
    private static final String TIME = "time";
    private static final String DATE = "dateLong";
    private static final String OLD_DATE_COLUMN = "date";
    private static final String RUN_INTERVAL = "runInterval";
    private static final String WALK_INTERVAL = "walkInterval";
    private static final String RUN_DELETION_MARKER = "deletionMarker";
    private static final String USERNAME = "USERNAME";

    public RunsDBManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryString = "CREATE TABLE " + RUNS_TABLE + "(" + RUN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GLOBAL_RUN_ID + " INTEGER, " + USERNAME + " VARCHAR(255), " + TIME + " INTEGER NOT NULL, " + DATE + " LONG NOT NULL, " + RUN_INTERVAL + " INTEGER NOT NULL, " + WALK_INTERVAL + " INTEGER NOT NULL," + RUN_DELETION_MARKER + " INTEGER NOT NULL)";
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion == 1 && newVersion == 2){
            db.execSQL("ALTER TABLE " + RUNS_TABLE + " ADD " + GLOBAL_RUN_ID + " INTEGER");
            db.execSQL("ALTER TABLE " + RUNS_TABLE + " ADD " + USERNAME + " VARCHAR(255)");
            db.execSQL("ALTER TABLE " + RUNS_TABLE + " ADD " + DATE + " BIGINT NOT NULL DEFAULT 0");
            db.execSQL("ALTER TABLE " + RUNS_TABLE + " ADD " + RUN_DELETION_MARKER + " INTEGER NOT NULL DEFAULT 0");

            //date data is now stored in a Long instead of a timestamp, this line converts the timestamps to Long
            db.execSQL("UPDATE " + RUNS_TABLE + " SET " + DATE + " = strftime('%s'," + OLD_DATE_COLUMN + ")");
        }
    }

    public List<Run> getRuns(String username){

        List<Run> runsList = new ArrayList<Run>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        if(username != null) {
            cursor = db.query(RUNS_TABLE, new String[]{RUN_ID, GLOBAL_RUN_ID, TIME, DATE, RUN_INTERVAL, WALK_INTERVAL},
                    USERNAME + "=? AND " + RUN_DELETION_MARKER + "=?", new String[]{username, "0"}, null, null, DATE + " DESC");
        } else{
            cursor = db.query(RUNS_TABLE, new String[]{RUN_ID, GLOBAL_RUN_ID, TIME, DATE, RUN_INTERVAL, WALK_INTERVAL},
                    USERNAME + " IS NULL AND " + RUN_DELETION_MARKER + "=?", new String[]{"0"}, null, null, DATE + " DESC");
        }

        if(cursor !=null){
            while(cursor.moveToNext()){
                runsList.add(new Run(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2), cursor.getLong(3), cursor.getInt(4),cursor.getInt(5)));
            }
            cursor.close();
        }

        db.close();
        return runsList;
    }

    public List<Run> getRunsForSync(String username){

        List<Run> runsList = new ArrayList<Run>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        cursor = db.query(RUNS_TABLE,new String[] {RUN_ID, GLOBAL_RUN_ID, TIME, DATE, RUN_INTERVAL, WALK_INTERVAL},
                USERNAME + "=? AND " + RUN_DELETION_MARKER + "=? AND " + GLOBAL_RUN_ID + " IS NULL", new String[]{username,"0"},null,null,DATE + " DESC");

        if(cursor !=null){
            while(cursor.moveToNext()){
                runsList.add(new Run(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2), cursor.getLong(3), cursor.getInt(4),cursor.getInt(5)));
            }
            cursor.close();
        }

        db.close();
        return runsList;
    }

    public void insertRun(Context context, String username, int runTime, Integer globalID, long runDate, int runInterval, int walkInterval){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TIME, runTime);
        values.put(DATE, runDate);
        values.put(RUN_INTERVAL,runInterval);
        values.put(WALK_INTERVAL,walkInterval);
        values.put(RUN_DELETION_MARKER, 0);

        if(globalID != null) {
            values.put(GLOBAL_RUN_ID, globalID);
        }

        if(username != null){
            values.put(USERNAME,username);
        }

        db.insert(RUNS_TABLE, null, values);
        db.close();

        RecordsFragment.updateRunListBroadcast(context);
    }

    public Run getRunByLocalID(int runID){

        Run run = null;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(RUNS_TABLE,new String[] {RUN_ID, GLOBAL_RUN_ID, TIME, DATE, RUN_INTERVAL, WALK_INTERVAL},
                RUN_ID + "=?", new String[]{Integer.toString(runID)},null,null,null);

        if(cursor !=null){
            if(cursor.moveToNext()){
                run = new Run(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2), cursor.getLong(3), cursor.getInt(4),cursor.getInt(5));
            }
            cursor.close();
        }

        db.close();
        return run;
    }

    public Run getRunByGlobalID(int runID){

        Run run = null;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(RUNS_TABLE,new String[] {RUN_ID, GLOBAL_RUN_ID, TIME, DATE, RUN_INTERVAL, WALK_INTERVAL},
                GLOBAL_RUN_ID + "=?", new String[]{Integer.toString(runID)},null,null,null);

        if(cursor !=null){
            if(cursor.moveToNext()){
                run = new Run(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2), cursor.getLong(3), cursor.getInt(4),cursor.getInt(5));
            }
            cursor.close();
        }

        db.close();
        return run;
    }

    private void deleteRunByLocalID(Context context, Integer runID){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(RUNS_TABLE,RUN_ID +"=?",new String[]{Integer.toString(runID)});

        db.close();

        RecordsFragment.updateRunListBroadcast(context);

    }

    public void deleteRunByGlobalID(Context context, Integer globalRunID){

        SQLiteDatabase db = getWritableDatabase();

        Integer deletions = db.delete(RUNS_TABLE,GLOBAL_RUN_ID +"=?",new String[]{Integer.toString(globalRunID)});

        db.close();

        RecordsFragment.updateRunListBroadcast(context);

    }

    public void markForDeletion(Context context, int runID){

        Run run = getRunByLocalID(runID);

        if(run != null && run.getGlobalRunID() == null){
            deleteRunByLocalID(context, runID);
        }else{

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(RUN_DELETION_MARKER, 1);

            db.update(RUNS_TABLE,values,RUN_ID + "=?",new String[]{Integer.toString(runID)});

            db.close();
        }

        RecordsFragment.updateRunListBroadcast(context);
    }

    public List<Integer> getRunDeletionList(String username){

        List<Integer> idList = new ArrayList<Integer>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        cursor = db.query(RUNS_TABLE,new String[] {GLOBAL_RUN_ID}, USERNAME + "=? AND " + RUN_DELETION_MARKER + "=?", new String[]{username,"1"},null,null,GLOBAL_RUN_ID + " ASC");

        if(cursor !=null){
            while(cursor.moveToNext()){
                idList.add(cursor.getInt(0));
            }
            cursor.close();
        }

        db.close();
        return idList;
    }

    public void insertGlobalRunID(Integer runID, Integer globalRunID){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GLOBAL_RUN_ID, globalRunID);

        db.update(RUNS_TABLE,values,RUN_ID + "=?", new String[] {Integer.toString(runID)});

        db.close();
    }
}
