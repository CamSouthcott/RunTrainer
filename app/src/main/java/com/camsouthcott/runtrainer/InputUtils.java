package com.camsouthcott.runtrainer;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Cam Southcott on 3/31/2016.
 */
public class InputUtils {

    //This code doesnt currently work, fix later
    public static void hideKeyboard(Activity activity){

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
