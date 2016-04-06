package com.camsouthcott.runtrainer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.camsouthcott.runtrainer.http.HttpResponse;
import com.camsouthcott.runtrainer.http.ResponseValidator;
import com.camsouthcott.runtrainer.http.ServerIOHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    Button registerButton;
    EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    TextView errorTextView;
    Boolean registerInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        errorTextView = (TextView) findViewById(R.id.errorTextView);
        errorTextView.setVisibility(View.INVISIBLE);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setEnabled(true);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getApplicationContext().getResources().getInteger(R.integer.maxPasswordSize))});

        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        passwordEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getApplicationContext().getResources().getInteger(R.integer.maxPasswordSize))});

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        usernameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(getApplicationContext().getResources().getInteger(R.integer.maxUsernameSize))});

        registerInProgress = false;
    }

    public void onRegisterClick(View view) {

        InputUtils.hideKeyboard(this);

        if(!registerInProgress) {
            errorTextView.setVisibility(View.INVISIBLE);

            String username = usernameEditText.getText().toString().trim().toLowerCase();
            String password = passwordEditText.getText().toString().trim();

            if(password.equals(confirmPasswordEditText.getText().toString().trim())) {

                if(validateUsername(username)) {

                    if (validatePassword(password)) {

                        registerButton.setEnabled(false);
                        new RegistrationAsyncTask().execute(username, password);

                    } else {
                        errorTextView.setText("Password must be between " +
                                Integer.toString(getResources().getInteger(R.integer.minPasswordSize)) +
                                " and " + Integer.toString(getResources().getInteger(R.integer.maxPasswordSize)) +
                                " and contain only letters and numbers");
                        errorTextView.setVisibility(View.VISIBLE);
                    }

                }else{
                    errorTextView.setText("Username must be between " +
                            Integer.toString(getResources().getInteger(R.integer.minUsernameSize)) +
                            " and " + Integer.toString(getResources().getInteger(R.integer.maxUsernameSize)) +
                            ", start with a letter and contain only letters and numbers");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            } else {
                errorTextView.setText("Passwords must match");
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean validateUsername(String username){

        if(username.length() < getResources().getInteger(R.integer.minUsernameSize)){
            return false;
        }

        if(username.length() > getResources().getInteger(R.integer.maxUsernameSize)){
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");
        Matcher matcher = pattern.matcher(username);

        if(matcher.find()){
            return true;
        }

        return false;
    }

    private boolean validatePassword(String password){

        if(password.length() < getResources().getInteger(R.integer.minPasswordSize)){
            return false;
        }

        if(password.length() > getResources().getInteger(R.integer.maxPasswordSize)){
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]*$");
        Matcher matcher = pattern.matcher(password);

        if(matcher.find()){
            return true;
        }

        return false;
    }



    private class RegistrationAsyncTask extends AsyncTask<String,Void,Void> {

        HttpResponse response = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            registerInProgress = true;
        }

        @Override
        protected Void doInBackground(String... params) {

            String username = params[0];
            String password = params[1];

            response = ServerIOHelper.register(getApplicationContext(),username,password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(response != null && response.getBody() != null){

                if(ResponseValidator.resourceCreated(response)){
                    Toast.makeText(getApplicationContext(),"Registration Successful",Toast.LENGTH_LONG).show();
                    finish();
                }else if(ResponseValidator.duplicate(response)){
                    errorTextView.setText("Username already in use");
                    errorTextView.setVisibility(View.VISIBLE);
                }else if(ResponseValidator.inputMissing(response)){
                    errorTextView.setText("The server expected additional information to process your request\nUpdating this app may help this issue");
                    errorTextView.setVisibility(View.VISIBLE);
                }else if(ResponseValidator.illegalFormat(response)){
                    errorTextView.setText("The server rejected the format of the username or password\nPlease consider updating this app to get updated naming rules");
                    errorTextView.setVisibility(View.VISIBLE);
                }else if(ResponseValidator.error(response)){
                    errorTextView.setText("The server encountered an error while processing your request");
                    errorTextView.setVisibility(View.VISIBLE);
                }else{
                    errorTextView.setText("Could not understand server response");
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }else{
                errorTextView.setText(" Server did not respond");
            }

            registerButton.setEnabled(true);
            registerInProgress = false;
        }
    }
}
