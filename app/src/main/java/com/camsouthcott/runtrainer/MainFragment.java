package com.camsouthcott.runtrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.camsouthcott.runtrainer.security.UserCredentialsManager;

import java.util.Date;

public class MainFragment extends Fragment {

    private TextView timerTextView;
    private TimerReceiver timerReceiver;
    private NumberPicker runMinuteNumberPicker, run10SecNumberPicker, runSecNumberPicker, walkMinuteNumberPicker, walk10SecNumberPicker, walkSecNumberPicker;
    private RelativeLayout savePromptLayout;
    private ScrollView mainScrollView;
    private Integer runTime;
    private long runStartDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,container,false);

        timerTextView = (TextView) view.findViewById(R.id.timerTextView);

        //Set inputs
        runMinuteNumberPicker = (NumberPicker) view.findViewById(R.id.runMinuteNumberPicker);
        runMinuteNumberPicker.setMaxValue(9);
        runMinuteNumberPicker.setMinValue(0);

        run10SecNumberPicker = (NumberPicker) view.findViewById(R.id.run10SecNumberPicker);
        run10SecNumberPicker.setMaxValue(5);
        run10SecNumberPicker.setMinValue(0);

        runSecNumberPicker = (NumberPicker) view.findViewById(R.id.runSecNumberPicker);
        runSecNumberPicker.setMaxValue(9);
        runSecNumberPicker.setMinValue(0);

        walkMinuteNumberPicker = (NumberPicker) view.findViewById(R.id.walkMinuteNumberPicker);
        walkMinuteNumberPicker.setMaxValue(9);
        walkMinuteNumberPicker.setMinValue(0);

        walk10SecNumberPicker = (NumberPicker) view.findViewById(R.id.walk10SecNumberPicker);
        walk10SecNumberPicker.setMaxValue(5);
        walk10SecNumberPicker.setMinValue(0);

        walkSecNumberPicker = (NumberPicker) view.findViewById(R.id.walkSecNumberPicker);
        walkSecNumberPicker.setMaxValue(9);
        walkSecNumberPicker.setMinValue(0);

        setDefaultDuration();

        //Set Receiver for receiving timing signals
        timerReceiver = new TimerReceiver();
        IntentFilter filter = new IntentFilter(TimerService.TICK);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(timerReceiver, filter);

        view.findViewById(R.id.startRunButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRun();
            }
        });

        view.findViewById(R.id.stopRunButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRun();
            }
        });

        savePromptLayout = (RelativeLayout) view.findViewById(R.id.savePromptLayout);
        view.findViewById(R.id.savePromptButtonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRun();
                closeSaveRunPrompt();
            }
        });

        view.findViewById(R.id.savePromptButtonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSaveRunPrompt();
            }
        });


        mainScrollView = (ScrollView) view.findViewById(R.id.mainScrollView);

        return view;
    }

    @Override
    public void onDestroy() {

        if(timerReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(timerReceiver);
        }
        super.onDestroy();
    }

    private void setDefaultDuration(){

        //Sets the default value for the number pickers
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("default", Context.MODE_PRIVATE);
        int runTime = sharedPreferences.getInt("runInterval",60);
        int walkTime = sharedPreferences.getInt("walkInterval",240);

        int runArray[] = intervalToArray(runTime);
        int walkArray[] = intervalToArray(walkTime);

        runMinuteNumberPicker.setValue(runArray[0]);
        run10SecNumberPicker.setValue(runArray[1]);
        runSecNumberPicker.setValue(runArray[2]);

        walkMinuteNumberPicker.setValue(walkArray[0]);
        walk10SecNumberPicker.setValue(walkArray[1]);
        walkSecNumberPicker.setValue(walkArray[2]);
    }

    private int[] intervalToArray(int time){

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

    public void startRun() {

        Intent intent = new Intent(getContext(), TimerService.class);
        intent.setAction(TimerService.TIMER_START);
        int runInterval = getRunInterval();
        runStartDate = new Date().getTime();

        if(runInterval > 0) {
            disableNumberPickers();
            int walkInterval = getWalkInterval();
            intent.putExtra("runInterval", 100*runInterval);
            intent.putExtra("walkInterval", 100*walkInterval);

            //Save default values
            SharedPreferences.Editor sPEditor = getContext().getSharedPreferences("default",Context.MODE_PRIVATE).edit();
            sPEditor.putInt("runInterval",runInterval);
            sPEditor.putInt("walkInterval", walkInterval);
            sPEditor.commit();

            getContext().startService(intent);
        } else{
            Toast.makeText(getContext(), "Run Time must be greater than 0.", Toast.LENGTH_LONG).show();
        }
    }

    private int getRunInterval(){
        return 60*runMinuteNumberPicker.getValue() + 10*run10SecNumberPicker.getValue() + runSecNumberPicker.getValue();
    }

    private int getWalkInterval(){
        return 60*walkMinuteNumberPicker.getValue() + 10*walk10SecNumberPicker.getValue() + walkSecNumberPicker.getValue();
    }

    private void disableNumberPickers(){

        runMinuteNumberPicker.setEnabled(false);
        run10SecNumberPicker.setEnabled(false);
        runSecNumberPicker.setEnabled(false);
        walkMinuteNumberPicker.setEnabled(false);
        walk10SecNumberPicker.setEnabled(false);
        walkSecNumberPicker.setEnabled(false);
    }

    public void endRun() {

        Intent intent = new Intent(getContext(), TimerService.class);
        intent.setAction(TimerService.TIMER_STOP);
        getContext().startService(intent);

        enableNumberPickers();

        //Ask the user if they want to save if the runTime has been set
        if(runTime != null && runTime !=0) {
            saveRunPrompt();
        }
    }

    private void saveRunPrompt(){

        setViewGroupEnabled(mainScrollView, false);
        savePromptLayout.setVisibility(View.VISIBLE);
    }

    private void saveRun(){

        new RunsDBManager(getContext()).insertRun(getContext(),UserCredentialsManager.getUsername(getContext()),runTime,null,runStartDate,getRunInterval(),getWalkInterval());
    }

    private void closeSaveRunPrompt(){

        //close save run prompt, restore normal function
        savePromptLayout.setVisibility(View.INVISIBLE);
        setViewGroupEnabled(mainScrollView, true);

        //Remove previous run
        runTime = 0;
        timerTextView.setText(intToTime(runTime));
    }

    //enables or disables a viewgroup and all its child elements
    private void setViewGroupEnabled(ViewGroup viewGroup, boolean enabled){

        viewGroup.setEnabled(enabled);

        for(int i = 0; i < viewGroup.getChildCount(); i++){

            View child = viewGroup.getChildAt(i);

            if(child instanceof ViewGroup){
                setViewGroupEnabled((ViewGroup) child, enabled);
            } else{
                child.setEnabled(enabled);
            }
        }
    }


    private void enableNumberPickers(){

        runMinuteNumberPicker.setEnabled(true);
        run10SecNumberPicker.setEnabled(true);
        runSecNumberPicker.setEnabled(true);
        walkMinuteNumberPicker.setEnabled(true);
        walk10SecNumberPicker.setEnabled(true);
        walkSecNumberPicker.setEnabled(true);
    }

    private class TimerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            runTime = intent.getIntExtra("time",0);
            timerTextView.setText(intToTime(runTime));
        }
    }

    public static String intToTime(int intTime){

        //Formatting function for the run time display
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
