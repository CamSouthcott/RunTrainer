package com.camsouthcott.runtrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TimerReceiver timerReceiver;
    private NumberPicker runMinuteNumberPicker, run10SecNumberPicker, runSecNumberPicker, walkMinuteNumberPicker, walk10SecNumberPicker, walkSecNumberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set inputs
        runMinuteNumberPicker = (NumberPicker) findViewById(R.id.runMinuteNumberPicker);
        runMinuteNumberPicker.setMaxValue(9);
        runMinuteNumberPicker.setMinValue(0);

        run10SecNumberPicker = (NumberPicker) findViewById(R.id.run10SecNumberPicker);
        run10SecNumberPicker.setMaxValue(5);
        run10SecNumberPicker.setMinValue(0);

        runSecNumberPicker = (NumberPicker) findViewById(R.id.runSecNumberPicker);
        runSecNumberPicker.setMaxValue(9);
        runSecNumberPicker.setMinValue(0);

        walkMinuteNumberPicker = (NumberPicker) findViewById(R.id.walkMinuteNumberPicker);
        walkMinuteNumberPicker.setMaxValue(9);
        walkMinuteNumberPicker.setMinValue(0);

        walk10SecNumberPicker = (NumberPicker) findViewById(R.id.walk10SecNumberPicker);
        walk10SecNumberPicker.setMaxValue(5);
        walk10SecNumberPicker.setMinValue(0);

        walkSecNumberPicker = (NumberPicker) findViewById(R.id.walkSecNumberPicker);
        walkSecNumberPicker.setMaxValue(9);
        walkSecNumberPicker.setMinValue(0);

        setDefaultDurations();

        //Set Receiver for receiving timing signals
        timerReceiver = new TimerReceiver();
        IntentFilter filter = new IntentFilter(TimerService.TICK);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, filter);

        timerTextView = (TextView) findViewById(R.id.timerTextView);

    }

    private void setDefaultDurations(){

        //Sets the default value for the number pickers
        SharedPreferences sharedPreferences = getSharedPreferences("default",Context.MODE_PRIVATE);
        int runTime = sharedPreferences.getInt("runTime",60);
        int walkTime = sharedPreferences.getInt("walkTime",240);

        int runArray[] = timeToArray(runTime);
        int walkArray[] = timeToArray(walkTime);

        runMinuteNumberPicker.setValue(runArray[0]);
        run10SecNumberPicker.setValue(runArray[1]);
        runSecNumberPicker.setValue(runArray[2]);

        walkMinuteNumberPicker.setValue(walkArray[0]);
        walk10SecNumberPicker.setValue(walkArray[1]);
        walkSecNumberPicker.setValue(walkArray[2]);
    }

    private int[] timeToArray(int time){

        //converts a time in s to an array of [min, 10s ,s]
        if(time < 0){
            time = 0;
        }

        if(time > 5999){
            time = 5999;
        }

        int array[] = new int[3];

        array[0] = time/60;
        int remainder = time%60;
        array[1] = remainder/10;
        remainder = remainder%10;
        array[2] = remainder;

        return array;
    }

    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startRun(View view) {

        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.TIMER_START);
        int runTime = 6000*runMinuteNumberPicker.getValue() + 1000*run10SecNumberPicker.getValue() + 100*runSecNumberPicker.getValue();

        if(runTime > 0) {
            disableNumberPickers();
            int walkTime = 6000*walkMinuteNumberPicker.getValue() + 1000*walk10SecNumberPicker.getValue() + 100*walkSecNumberPicker.getValue();
            intent.putExtra("runTime", runTime);
            intent.putExtra("walkTime", walkTime);

            //Save default values
            SharedPreferences.Editor sPEditor = getSharedPreferences("default",Context.MODE_PRIVATE).edit();
            sPEditor.putInt("runTime",runTime/100);
            sPEditor.putInt("walkTime",walkTime/100);
            sPEditor.commit();

            startService(intent);
        } else{
            Toast.makeText(this,"Run Time must be greater than 0.",Toast.LENGTH_LONG).show();
        }
    }

    private void disableNumberPickers(){

        runMinuteNumberPicker.setEnabled(false);
        run10SecNumberPicker.setEnabled(false);
        runSecNumberPicker.setEnabled(false);
        walkMinuteNumberPicker.setEnabled(false);
        walk10SecNumberPicker.setEnabled(false);
        walkSecNumberPicker.setEnabled(false);
    }

    public void endRun(View view) {

        Intent intent = new Intent(this, TimerService.class);
        intent.setAction(TimerService.TIMER_STOP);
        startService(intent);

        enableNumberPickers();
    }

    private void enableNumberPickers(){

        runMinuteNumberPicker.setEnabled(true);
        run10SecNumberPicker.setEnabled(true);
        runSecNumberPicker.setEnabled(true);
        walkMinuteNumberPicker.setEnabled(true);
        walk10SecNumberPicker.setEnabled(true);
        walkSecNumberPicker.setEnabled(true);
    }

    private class TimerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            timerTextView.setText(intToTime(intent.getIntExtra("time",0)));
        }
    }

    private String intToTime(int intTime){

        //Converts a integer to a time string of format X:XX:XX:XX
        String stringTime = "";

        if(intTime>0){
            stringTime += String.valueOf(intTime / 360000) + ":";

            int remainder = intTime % 360000;
            int minutes = remainder/6000;
            if(minutes < 10){
                stringTime += "0" + String.valueOf(minutes) + ":";
            } else{
                stringTime += String.valueOf(minutes) + ":";
            }


            remainder = remainder % 6000;
            int seconds = remainder/100;
            if(seconds < 10){
                stringTime += "0" + String.valueOf(remainder/100) + ":";
            } else{
                stringTime += String.valueOf(remainder/100) + ":";
            }


            remainder = remainder % 100;
            if(remainder < 10){
                stringTime += "0" + String.valueOf(remainder);
            } else{
                stringTime += String.valueOf(remainder);
            }

        }else{
            stringTime = "0:00:00:00";
        }

        return stringTime;
    }


}
