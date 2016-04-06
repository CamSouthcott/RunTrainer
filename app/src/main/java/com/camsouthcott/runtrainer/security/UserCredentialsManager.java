package com.camsouthcott.runtrainer.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by Cam Southcott on 3/23/2016.
 */
public class UserCredentialsManager {

    public static String getUsername(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username",null);
    }

    public static String getEncryptedPassword(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences("default", Context.MODE_PRIVATE);
        return sharedPreferences.getString("encryptedPassword",null);
    }

    public static void saveCredentials(Context context, String username, String encryptedPassword){

        SharedPreferences.Editor sPEditor = context.getSharedPreferences("default", Context.MODE_PRIVATE).edit();
        sPEditor.putString("encryptedPassword" ,encryptedPassword);
        sPEditor.putString("username", username);
        sPEditor.commit();

    }

    public static String encryptPassword(Context context, String password) throws EncryptionFailureException, EncryptionProviderInitializationException{

        EncryptionProvider encryptionProvider = new EncryptionProvider(context);
        byte[] encryptedPasswordBytes = encryptionProvider.encrypt(password.getBytes());

        return Base64.encodeToString(encryptedPasswordBytes, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static void logout(Context context){

        SharedPreferences.Editor sPEditor = context.getSharedPreferences("default", Context.MODE_PRIVATE).edit();
        sPEditor.putString("username",null);
        sPEditor.putString("encryptedPassword",null);
        sPEditor.commit();
    }
}
