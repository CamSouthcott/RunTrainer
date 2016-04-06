package com.camsouthcott.runtrainer.http;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cam Southcott on 3/25/2016.
 */
public class ResponseValidator {

    public static final Integer OK = 200;
    public static final Integer CREATED = 201;
    public static final Integer CONFLICT = 409;
    public static final Integer BAD_REQUEST = 400;
    public static final Integer NOT_FOUND = 404;
    public static final Integer INTERNAL_SERVER_ERROR = 500;
    public static final Integer UNAUTHORIZED = 401;

    private static final String REQUEST_STATUS = "RequestStatus";
    private static final String SUCCESSFUL = "Successful";
    private static final String RESOURCE_CREATED = "Created";
    private static final String DUPLICATE = "Duplicate";
    private static final String ILLEGAL_FORMAT = "IllegalFormat";
    private static final String RESOURCE_NOT_FOUND = "ResourceNotFound";
    private static final String ERROR = "Error";
    private static final String INPUT_MISSING = "InputMissing";
    private static final String ACCESS_DENIED = "AccessDenied";
    private static final String RUN_NOT_FOUND = "RunNotFound";
    private static final String RESOURCE_NOT_OWNED = "ResourceNotOwned";

    public static Boolean successful(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(OK)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(SUCCESSFUL);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean resourceCreated(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(CREATED)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                return bodyJSON.getString(REQUEST_STATUS).equals(RESOURCE_CREATED);

            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean duplicate(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(CONFLICT)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(DUPLICATE);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean inputMissing(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(BAD_REQUEST)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(INPUT_MISSING);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean illegalFormat(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(BAD_REQUEST)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(ILLEGAL_FORMAT);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean resourceNotFound(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(NOT_FOUND)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(RESOURCE_NOT_FOUND);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean error(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(INTERNAL_SERVER_ERROR)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(ERROR);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean accessDenied(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(UNAUTHORIZED)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(ACCESS_DENIED);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean runNotFound(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(NOT_FOUND)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(RUN_NOT_FOUND);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }

    public static Boolean resourceNotOwned(HttpResponse response){

        if(response == null){
            return false;
        }

        Integer responseCode = response.getResponseCode();
        String body = response.getBody();

        if(responseCode.equals(UNAUTHORIZED)){
            try{
                JSONObject bodyJSON = new JSONObject(body);

                if(bodyJSON.length() == 1){
                    return bodyJSON.getString(REQUEST_STATUS).equals(RESOURCE_NOT_OWNED);
                }
            }catch(JSONException e){

            }
        }


        return false;
    }
}
