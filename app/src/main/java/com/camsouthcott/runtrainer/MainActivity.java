package com.camsouthcott.runtrainer;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.camsouthcott.runtrainer.security.UserCredentialsManager;
import com.camsouthcott.runtrainer.stab.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    MenuItem loginMenuItem;
    MenuItem logoutMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setViewPager(viewPager);

        Intent intent = new Intent(this,RunSyncService.class);
        intent.setAction(RunSyncService.SYNC_REQUEST);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        loginMenuItem = (MenuItem) menu.findItem(R.id.menu_login);
        logoutMenuItem = (MenuItem) menu.findItem(R.id.menu_logout);

        if(loggedIn()){
            loginMenuItem.setVisible(false);
            logoutMenuItem.setVisible(true);
        }
        return true;
    }

    private static final Integer LoginRequestCode = 1000;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.menu_login){

            if(!loggedIn()) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LoginRequestCode);
                return true;
            }
        }else if(id == R.id.menu_logout){
            processLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == LoginRequestCode && resultCode == RESULT_OK){
            processLogin();
        }
    }

    private void processLogin(){

        loginMenuItem.setVisible(false);
        logoutMenuItem.setVisible(true);
    }

    private void processLogout(){
        UserCredentialsManager.logout(this);

        loginMenuItem.setVisible(true);
        logoutMenuItem.setVisible(false);

        RecordsFragment.updateRunListBroadcast(this);
    }

    public boolean loggedIn(){

        String username = UserCredentialsManager.getUsername(this);
        String encryptedPassword = UserCredentialsManager.getEncryptedPassword(this);

        return username!= null && encryptedPassword != null;
    }

}
