package com.camsouthcott.runtrainer.http;

import android.content.Context;
import android.util.Base64;

import com.camsouthcott.runtrainer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ServerIOHelper {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";

    public static HttpResponse login(Context context, String username, String password){
        return httpCommunicate(context, "user/login", GET, null, username, password);
    }

    public static HttpResponse register(Context context, String username, String password){

        Map<String,String> params = new HashMap<>();
        params.put(context.getString(R.string.username_parameter_tag),username);
        params.put(context.getString(R.string.password_parameter_tag),password);

        return httpCommunicate(context, "user", POST, params, null, null);
    }

    public static HttpResponse getRuns(Context context, Integer lastUpdateID, String username, String password){

        Map<String,String> params = new HashMap<>();
        if(lastUpdateID != null){
            params.put(context.getString(R.string.run_last_update_id_parameter_tag), lastUpdateID.toString());
        }

        return httpCommunicate(context,"user/" + username + "/run", GET, params, username, password);
    }

    public static HttpResponse insertRun(Context context, String username, String password, Integer time, Long date, Integer runInterval, Integer walkInterval){

        Map<String,String> params = new HashMap<>();
        params.put(context.getString(R.string.run_time_parameter_tag), time.toString());
        params.put(context.getString(R.string.run_date_parameter_tag), date.toString());
        params.put(context.getString(R.string.run_run_interval_parameter_tag), runInterval.toString());
        params.put(context.getString(R.string.run_walk_interval_parameter_tag), walkInterval.toString());

        return httpCommunicate(context, "user/" + username + "/run", POST, params, username, password);
    }

    public static HttpResponse deleteRun(Context context, String username, String password, Integer globalRunID){

        return httpCommunicate(context, "user/" + username + "/run/" + globalRunID, DELETE, null, username, password);
    }

    public static HttpResponse httpCommunicate(Context context, String uri, String httpMethod, Map<String,String> params, String username, String password){

        HttpResponse response = null;
        String paramString = null;

        //if there are parameters convert them to x-www-form-urlencoded
        if(params != null && !params.isEmpty()){

            StringBuilder querySB = new StringBuilder();


            Boolean first = true;

            for (String key : params.keySet()) {
                if (!first) {
                    querySB.append("&");
                }

                querySB.append(key);
                querySB.append("=");
                querySB.append(URLEncoder.encode(params.get(key)));
                first = false;
            }

            paramString = querySB.toString();
        }


        try {
            URL url = null;

            if(httpMethod.equals("GET") && paramString != null){
                url = new URL(context.getString(R.string.server_base_url) + uri + "?" + paramString);
            }else{
                url = new URL(context.getString(R.string.server_base_url) + uri);
            }

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(httpMethod);
            urlConnection.setRequestProperty("ACCEPT","application/json");

            //http basic authorization if set
            if(username!= null && password != null){
                String authenticationString =  "Basic " + encodeBase64(username + ":" + password);
                urlConnection.setRequestProperty("Authorization", authenticationString);
            }

            //Add query parameters if set
            if(!(paramString == null) && !httpMethod.equals("GET")){

                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


                //Output to server
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                outputStreamWriter.write(paramString);
                outputStreamWriter.flush();
            }

            InputStream inputStream = null;

            Integer responseCode = urlConnection.getResponseCode();
            if(responseCode/100 == 2){
                //Request Successful
                inputStream = urlConnection.getInputStream();
            } else{
                //Request Not Successful
                inputStream = urlConnection.getErrorStream();
            }

            //Prevent an error where the connection is closed and the input stream is null
            if(inputStream == null){
                return response;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            response = new HttpResponse(responseCode, sb.toString(), urlConnection.getHeaderFields());

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }

        return response;
    }

    private static String encodeBase64(String input){

        byte[] inputBytes = input.getBytes();
        return Base64.encodeToString(inputBytes,Base64.URL_SAFE|Base64.NO_WRAP);
    }
}
