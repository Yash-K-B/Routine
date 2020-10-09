package com.project.yash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.project.yash.models.TimeContainer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class VibrationEnablerService extends Service {
    boolean status = false;
    List<TimeContainer> list;
    Schedule_DB schedule_db;
    TimeContainer t1, t2, t3;
    SharedPreferences preferences;

    @Override
    public void onCreate() {
        Log.i("data_service", "Service Created");
        list = new ArrayList<>();
        int p = 0;
        Context context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        while (p<list.size())
//            Log.i("data_service",list.get(p).getHour()+":"+list.get(p++).getMinute());

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int k, flag = 0, day;
        schedule_db = new Schedule_DB(this);
        //making schedule
        list.clear();
        if (schedule_db.getPERIODS() != null) {
            for (int i = 0; i < schedule_db.getPERIODS().length; i++) {
                day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                Log.i("data_service", day + "");
                if (schedule_db.getDAYS()[day][i].equals("null"))
                    continue;
                t1 = new TimeContainer(schedule_db.getPERIODS()[i][0][0], schedule_db.getPERIODS()[i][0][1]);
                t2 = new TimeContainer(schedule_db.getPERIODS()[i][1][0], schedule_db.getPERIODS()[i][1][1]);
                if (flag == 0) {
                    list.add(t1);
                    t3 = t1;
                    flag = 1;
                }
                if (t1.getHour() != t3.getHour() || t1.getMinute() != t3.getMinute()) {
                    list.add(t3);
                    list.add(t1);
                }
                t3 = t2;
                Log.i("data_service", t1.getHour() + ":" + t1.getMinute() + "-" + t2.getHour() + ":" + t2.getMinute());
            }
            if (flag != 0)
                list.add(t2);
        }
        //schedule creation completed
        //gathering the function applied information

        status = preferences.getBoolean(KeyStore.STATUS,false);

        Log.i("data_service", status + "\tsize" + list.size());
        //implementing the alarm schedule task
        for (k = 0; k < list.size(); k += 2) {
            AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, list.get(k).getHour());
            calendar.set(Calendar.MINUTE, list.get(k).getMinute());
            calendar.set(Calendar.SECOND, 0);
            Intent intent1 = new Intent(this, TaskReceiver.class);
            intent1.putExtra("state", "ON");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), k, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            if (status)
                alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            else
                alarmManager1.cancel(pendingIntent);
            Log.i("data_service", "Alarm set on " + calendar.getTime() + "\n" + new Date() + "\n");

            AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
            calendar.set(Calendar.HOUR_OF_DAY, list.get(k + 1).getHour());
            calendar.set(Calendar.MINUTE, list.get(k + 1).getMinute());
            calendar.set(Calendar.SECOND, 0);
            Intent intent2 = new Intent(this, TaskReceiver.class);
            intent2.putExtra("state", "OFF");
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), k + 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            if (status)
                alarmManager2.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent1);
            else
                alarmManager2.cancel(pendingIntent1);
            Log.i("data_service", "Alarm set on " + calendar.getTime() + "\n" + new Date() + "\n");
        }
        //alarm for doing job on next day
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        Intent intent1 = new Intent(this, DailyRoutineSet.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), k + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (status)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        else{
            alarmManager.cancel(pendingIntent);
            Intent normalModeIntent =  new Intent(this, TaskReceiver.class);
            normalModeIntent.putExtra("state", "OFF");
            sendBroadcast(normalModeIntent);
        }
        Log.i("data_service", "Alarm set on " + calendar.getTime() + "\n" + new Date() + "\n");
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("data_service", "Service Destroyed");
        list.clear();
        super.onDestroy();
    }
}
