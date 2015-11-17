package com.camsouthcott.runtrainer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {

    MediaPlayer runSound;
    SeekBar notificationVolumeBar;
    int notificationVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        runSound = MediaPlayer.create(getApplicationContext(), R.raw.start_running);

        notificationVolumeBar = (SeekBar) findViewById(R.id.volumeBar);

        notificationVolumeBar.setProgress(getSharedPreferences("default",Context.MODE_PRIVATE).getInt("notificationVolume",100));
        notificationVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                notificationVolume = progress;
                float volume = (float) (progress * .01);
                runSound.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!runSound.isPlaying()) {
                    runSound.start();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                runSound.release();
                runSound = MediaPlayer.create(getApplicationContext(), R.raw.start_running);

                Intent intent = new Intent(getApplicationContext(), TimerService.class);
                intent.setAction(TimerService.VOLUME_CHANGE);
                intent.putExtra("volume", notificationVolume);

                startService(intent);

                SharedPreferences.Editor sPEditor = getSharedPreferences("default", Context.MODE_PRIVATE).edit();
                sPEditor.putInt("notificationVolume",notificationVolume);
                sPEditor.commit();
            }
        });
    }
}
