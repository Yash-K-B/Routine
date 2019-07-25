package com.project.yash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DailyRoutineSet extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("data_service","In the DailyRoutine Updater");
        context.startService(new Intent(context, VibrationEnablerService.class));

    }
}
