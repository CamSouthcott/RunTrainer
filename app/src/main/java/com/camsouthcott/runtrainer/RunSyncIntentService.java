package com.camsouthcott.runtrainer;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.camsouthcott.runtrainer.http.HttpResponse;
import com.camsouthcott.runtrainer.http.ResponseValidator;
import com.camsouthcott.runtrainer.http.ServerIOHelper;
import com.camsouthcott.runtrainer.security.UserCredentialsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//This Service does the work of syncing the local run db with the global run db
public class RunSyncIntentService  extends IntentService{


    public RunSyncIntentService(){
        super("RunSyncIntentService");
    }

    public RunSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("RunSyncIntentService", "Handling Intent");
        syncRuns(this);
    }

    private void syncRuns(Context context) {

        Log.e("RunSyncIntentService","Syncing");
        String username = UserCredentialsManager.getUsername(context);
        String password = UserCredentialsManager.getEncryptedPassword(context);

        requestRuns(context, username, password);
        sendRuns(context, username, password);
        sendRunDeletions(context, username, password);
    }

    private void requestRuns(Context context, String username, String password) {

        Log.e("RunSyncIntentService","Requesting Runs");
        SharedPreferences sharedPreferences = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        Integer lastUpdateID = sharedPreferences.getInt("runLastUpdateID", -1);

        if (lastUpdateID < 0) {
            lastUpdateID = null;
        }

        if (username != null && password != null) {
            HttpResponse response = ServerIOHelper.getRuns(context, lastUpdateID, username, password);

            if(response == null){
                return;
            }

            try {
                JSONObject responseBody = new JSONObject(response.getBody());
                JSONArray runsArray = responseBody.getJSONArray("runs");

                RunsDBManager dbManager = new RunsDBManager(context);

                for (int i = 0; i < runsArray.length(); i++) {

                    JSONObject run = runsArray.getJSONObject(i);

                    Integer globalRunID = run.getInt(context.getString(R.string.run_global_id_parameter_tag));

                    if (lastUpdateID == null || globalRunID > lastUpdateID) {
                        lastUpdateID = globalRunID;
                    }

                    dbManager.insertRun(context,
                            username,
                            run.getInt(context.getString(R.string.run_time_parameter_tag)),
                            globalRunID,
                            run.getLong(context.getString(R.string.run_date_parameter_tag)),
                            run.getInt(context.getString(R.string.run_run_interval_parameter_tag)),
                            run.getInt(context.getString(R.string.run_walk_interval_parameter_tag)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //highest update id is saved to lower bandwidth
        if (lastUpdateID != null) {
            SharedPreferences.Editor sPEditor = context.getSharedPreferences("default", Context.MODE_PRIVATE).edit();
            sPEditor.putInt("runLastUpdateID", lastUpdateID);
            sPEditor.commit();
        }
    }

    private void sendRuns(Context context, String username, String password) {

        Log.e("RunSyncIntentService", "Sending Runs");
        if (username != null && password != null) {

            RunsDBManager dbManager = new RunsDBManager(context);
            List<Run> runs = dbManager.getRunsForSync(username);

            for (Run run : runs) {

                HttpResponse response = ServerIOHelper.insertRun(context, username, password, run.getRunTime(), run.getRunDate(), run.getRunInterval(), run.getWalkInterval());

                if(response == null){
                    return;
                }

                try {
                    JSONObject responseBody = new JSONObject(response.getBody());

                    dbManager.insertGlobalRunID(run.getRunID(), responseBody.getInt("runID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendRunDeletions(Context context, String username, String password) {

        Log.e("RunSyncIntentService","Sending Run Deletions");
        if (username != null && password != null) {

            RunsDBManager dbManager = new RunsDBManager(context);
            List<Integer> deletionList = dbManager.getRunDeletionList(username);

            for (Integer globalRunID : deletionList) {
                HttpResponse response = ServerIOHelper.deleteRun(context, username, password, globalRunID);

                if (ResponseValidator.successful(response) || ResponseValidator.runNotFound(response)) {
                    dbManager.deleteRunByGlobalID(context, globalRunID);
                }
            }
        }
    }
}
