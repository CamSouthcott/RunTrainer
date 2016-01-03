package com.camsouthcott.runtrainer;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    public static final String TIMER_START = "com.camsouthcott.RunTrainer.TIMER_START";
    public static final String TIMER_STOP = "com.camsouthcott.RunTrainer.TIMER_STOP";
    public static final String VOLUME_CHANGE = "com.camsouthcott.RunTrainer.VOLUME_CHANGE";
    public static final String TICK = "com.camsouthcott.RunTrainer.TICK";
    public static final int FOREGROUND_NOTIFICATION_ID = 479;
    private static MediaPlayer mRunSound,mWalkSound;
    private static boolean training;


    private Timer runTimer;
    private boolean timerRunning = false;
    private int mWalkInterval,mRunInterval,mPeriod;

    @Override
    public void onCreate() {
        super.onCreate();
        mRunSound = MediaPlayer.create(getApplicationContext(), R.raw.start_running);
        mWalkSound = MediaPlayer.create(getApplicationContext(), R.raw.start_walking);
        Log.e("TimerService", "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "Service Started");

        if(intent!= null) {
            String action = intent.getAction();

            if (action.equals(TIMER_START)) {
                startRun(intent);

            } else if (action.equals(TIMER_STOP)) {
                stopRun();
            } else if (action.equals(VOLUME_CHANGE)) {

                int volume = intent.getIntExtra("volume", -1);

                if (volume >= 0 && volume < 101) {

                    mRunSound.setVolume((float) (volume * .01), (float) (volume * .01));
                    mWalkSound.setVolume((float) (volume * .01), (float) (volume * .01));
                }
            }
        }
        return START_STICKY;

    }

    private void startRun(Intent intent) {
        Log.e("Service", "Run Started");

        if(!timerRunning) {
            timerRunning = true;
            runTimer = new Timer();

            //get volume from memory and set it for the notifications
            int volume = getSharedPreferences("default", Context.MODE_PRIVATE).getInt("notificationVolume",100);
            mRunSound.setVolume((float) (volume*.01),(float) (volume*.01));
            mWalkSound.setVolume((float) (volume*.01),(float) (volume*.01));

            //Don't Run Timer if RunTime was not received
            mRunInterval = intent.getIntExtra("runInterval",0);
            if(mRunInterval > 0) {

                //If walktime is 0, dont play training sounds
                mWalkInterval = intent.getIntExtra("walkInterval",0);
                mPeriod = mRunInterval + mWalkInterval;
                training = (mWalkInterval > 0);

                createForegroundNotification();

                runTimer.schedule(new TimerTask() {
                    private int time = 0;

                    @Override
                    public void run() {

                        if(training) {
                            if (time % mPeriod == 0) {
                                mRunSound.start();
                                Log.e("TimerThread","Run Sound");
                            } else if ((time - mRunInterval) % mPeriod == 0) {
                                mWalkSound.start();
                                Log.e("TimerThread", "Walk Sound");
                            }
                        }

                        Intent intent = new Intent(TICK);
                        intent.putExtra("time", time);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        time++;
                    }

                }, 0, 10);
            }
        }
    }

    private void stopRun() {

        if(runTimer != null) {
            runTimer.cancel();

            removeForegroundNotification();
        }

        timerRunning = false;
    }

    private void createForegroundNotification(){
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("RunTimer")
                .setTicker("Run Started")
                .setContentText("Run in Progress")
                .setSmallIcon(R.drawable.runner)
                .setOngoing(true)
                .build();

        startForeground(FOREGROUND_NOTIFICATION_ID, notification);
    }

    private void removeForegroundNotification(){
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //Not a bindable service, does nothing
        return null;
    }


}
