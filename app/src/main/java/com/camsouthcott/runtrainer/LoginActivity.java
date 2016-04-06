package com.camsouthcott.runtrainer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.camsouthcott.runtrainer.http.HttpResponse;
import com.camsouthcott.runtrainer.http.ResponseValidator;
import com.camsouthcott.runtrainer.http.ServerIOHelper;
import com.camsouthcott.runtrainer.security.EncryptionFailureException;
import com.camsouthcott.runtrainer.security.EncryptionProviderInitializationException;
import com.camsouthcott.runtrainer.security.UserCredentialsManager;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Boolean loginInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        //Set maximum characters in the edit texts
        usernameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getApplicationContext().getResources().getInteger(R.integer.maxUsernameSize))});
        passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getApplicationContext().getResources().getInteger(R.integer.maxPasswordSize))});

        loginInProgress = false;
    }

    public void onClickLogin(View view){

        //Don't start logging in again if a login thread is active
        if(!loginInProgress) {

            String username = usernameEditText.getText().toString().trim().toLowerCase();
            String password = passwordEditText.getText().toString().trim();

            if ((!username.isEmpty()) && (!password.isEmpty())) {

                try {

                    String encryptedPassword = UserCredentialsManager.encryptPassword(this, password);
                    new LoginAsyncTask().execute(username,encryptedPassword);

                } catch (EncryptionFailureException e) {
                    e.printStackTrace();
                } catch (EncryptionProviderInitializationException e) {
                    e.printStackTrace();
                }


            } else {
                Toast.makeText(this, "Enter Username and Password", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void onClickRegister(View view){

        startActivity(new Intent(this, RegistrationActivity.class));
    }

    private class LoginAsyncTask extends AsyncTask<String,Void,Void> {

        HttpResponse response = null;
        String encryptedPassword = null;
        String username = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginInProgress = true;
        }

        @Override
        protected Void doInBackground(String... params) {

            username = params[0];
            encryptedPassword = params[1];

            response = ServerIOHelper.login(getApplicationContext(),username,encryptedPassword);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(response != null && ResponseValidator.successful(response)){

                UserCredentialsManager.saveCredentials(getApplicationContext(), username, encryptedPassword);

                //Broadcast to update records fragment
                RecordsFragment.updateRunListBroadcast(getApplicationContext());

                setResult(RESULT_OK);
                finish();

            } else{
                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
            }

            loginInProgress = false;
        }
    }
}
