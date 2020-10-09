package com.project.yash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import java.util.Objects;

import static android.content.Context.AUDIO_SERVICE;

public class TaskReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String isEnableVibration=intent.getStringExtra("state");
        Log.i("data_service","Task Receiver");
        if(Objects.equals(isEnableVibration, "ON"))
        {
            AudioManager audioManager=(AudioManager)context.getSystemService(AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            Log.i("data_service","Vibration Mode");
        }
        else if(Objects.equals(isEnableVibration, "OFF"))
        {
            AudioManager audioManager=(AudioManager)context.getSystemService(AUDIO_SERVICE);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.i("data_service","Normal Mode");
        }
    }
}
