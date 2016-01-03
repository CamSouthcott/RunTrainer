package com.camsouthcott.runtrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Cam Southcott on 12/26/2015.
 */
public class RecordsFragment extends Fragment {

    List<Run> runsList;
    ListView runsListView;
    BroadcastReceiver runListUpdateReceiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_records,container,false);

        runsListView = (ListView) view.findViewById(R.id.runsListView);

        populateRunList();

        //Set up receiver to update the list when the table is updated
        runListUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                populateRunList();
            }
        };

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(runListUpdateReceiver, new IntentFilter(RunsDBManager.RUN_TABLE_UPDATE));

        return view;
    }

    private void populateRunList(){
        runsList = new RunsDBManager(getContext()).getRuns();
        ArrayAdapter<Run> adapter = new RunListAdapter(getContext());
        runsListView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {

        if(runListUpdateReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(runListUpdateReceiver);
        }
        super.onDestroy();
    }

    private class RunListAdapter extends ArrayAdapter<Run> {

        public RunListAdapter(Context context){
            super(context,R.layout.records_listview_item, runsList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            //create view if it has not been created
            if(view == null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.records_listview_item, parent, false);
            }

            Run record = runsList.get(position);

            //Set text values
            TextView dateTextView = (TextView) view.findViewById(R.id.runDateTextView);
            dateTextView.setText( record.getRunDate());

            TextView timeTextView = (TextView) view.findViewById(R.id.runTimeTextView);
            timeTextView.setText(MainFragment.intToTime(record.getRunTime()));

            TextView runIntervalTextView = (TextView) view.findViewById(R.id.runIntervalTextView);
            runIntervalTextView.setText(intToMinuteString(record.getRunInterval()));

            TextView walkIntervalTextView = (TextView) view.findViewById(R.id.walkIntervalTextView);
            walkIntervalTextView.setText(intToMinuteString(record.getWalkInterval()));

            return view;
        }
    }

    //returns a String of minutes:seconds based on input
    private String intToMinuteString(int time){

        StringBuilder sb = new StringBuilder();

        sb.append(time/60);
        sb.append(":");
        int seconds = time % 60;

        if(seconds < 10){
            sb.append("0");
        }

        sb.append(seconds);

        return sb.toString();
    }

}
