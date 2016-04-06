package com.camsouthcott.runtrainer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import java.util.GregorianCalendar;

//This service manages how the app interacts with the IntentService that syncs the local run db to the global run db
public class RunSyncService extends Service {

    private static final Integer SYNC_PERIOD = 3600000;
    private static final Integer ALARM_REQUEST_CODE = 489420;

    public static final String SYNC_REQUEST = "com.camsouthcott.runtrainer.SYNC_REQUEST";

    private RunSyncReceiver runSyncReceiver;
    @Override
    public void onCreate() {
        super.onCreate();

        runSyncReceiver = new RunSyncReceiver();

        Log.e("RunSyncService", "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("RunSyncService", "Service Started");

        if(intent!= null) {
            String action = intent.getAction();

            if(action!= null){
                if(action.equals(SYNC_REQUEST)){
                    Intent syncIntent = new Intent(this,RunSyncIntentService.class);
                    startService(syncIntent);

                    runSyncReceiver.set(this);
                }
            }
        }
        return START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class RunSyncReceiver extends BroadcastReceiver {

        public void set(Context context) {

            Long nextSyncTime = new GregorianCalendar().getTimeInMillis() + SYNC_PERIOD;
            Intent intent = new Intent(context, RunSyncReceiver.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, nextSyncTime, PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Intent syncIntent = new Intent(context,RunSyncIntentService.class);
            startService(syncIntent);
            set(context);
        }


    }

}
