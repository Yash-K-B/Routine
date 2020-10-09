package com.project.yash.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.yash.KeyStore;
import com.project.yash.Schedule_DB;
import com.project.yash.models.TimeContainer;
import com.project.yash.VibrationEnablerService;
import com.project.yash.routine.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskEnablerPage extends Fragment {
    TextView textView;
    TimeContainer t3, t2, t1;
    boolean b = false;
    Button button;
    TextView status_textview;
    boolean status;
    File file1;
    Context context;
    SharedPreferences preferences;

    public TaskEnablerPage() {

    }

    public static TaskEnablerPage newFragment() {
        return new TaskEnablerPage();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        status = preferences.getBoolean(KeyStore.STATUS, false);
        //period list creation
        List<TimeContainer> list = new ArrayList<>();
        final Schedule_DB schedule_db = new Schedule_DB(this.getContext());
        int flag = 0, day;
        //t3 = new TimeContainer(Schedule_DB.periods[0][0][0], Schedule_DB.periods[0][0][1]);
        if (schedule_db.getPERIODS() != null) {
            for (int i = 0; i < schedule_db.getPERIODS().length; i++) {
                day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
                Log.i("data_service", day + " " + schedule_db.getDAYS()[day][i]);
                if (schedule_db.getDAYS()[day][i].equals("null"))
                    continue;
                t1 = new TimeContainer(schedule_db.getPERIODS()[i][0][0], schedule_db.getPERIODS()[i][0][1]);
                t2 = new TimeContainer(schedule_db.getPERIODS()[i][1][0], schedule_db.getPERIODS()[i][1][1]);
                if (flag == 0) {
                    flag = 1;
                    list.add(t1);
                    t3 = t1;
                }
                if (t1.getHour() == t3.getHour() && t1.getMinute() == t3.getMinute()) {

                } else {
                    list.add(t3);
                    list.add(t1);
                }
                t3 = t2;
                Log.i("data_service", t1.getHour() + ":" + t1.getMinute() + "-" + t2.getHour() + ":" + t2.getMinute());
            }
            if (flag != 0)
                list.add(t2);
        }
        int p = 0;
        Log.i("data_service", "list size:" + list.size());
        if (!list.isEmpty())
            while (p < list.size())
                Log.i("data_service", list.get(p).getHour() + ":" + list.get(p++).getMinute());
        StringBuilder period_time = new StringBuilder();
        //getting period string
        if (!list.isEmpty())
            for (int k = 0, l = 1; k < list.size(); k += 2) {
                int t1_h = list.get(k).getHour();
                int t1_m = list.get(k).getMinute();
                int t2_h = list.get(k + 1).getHour();
                int t2_m = list.get(k + 1).getMinute();
                String s = String.format(Locale.US, "%02d:%02d%s - %02d:%02d%s", ((t1_h > 12) ? t1_h - 12 : t1_h), t1_m, ((t1_h > 12) ? "PM" : "AM"), ((t2_h > 12) ? t2_h - 12 : t2_h), t2_m, ((t2_h > 12) ? "PM" : "AM"));
                period_time.append("Phase ").append(l++).append(": ").append(s).append("\n\n");
                //"Phase "+(l++)+": "+(t1_h>12?t1_h-12:t1_h)+":"+t1_m+""+(t1_h>12?"PM":"AM")+" - "+(t2_h>12?t2_h-12:t2_h)+":"+t2_m+""+((t2_h>=12)?"PM":"AM")+"\n\n";
            }
        else
            period_time = new StringBuilder("No Classes");

        TextView textView = view.findViewById(R.id.class_schedule);
        button = view.findViewById(R.id.button_load);
        textView.setText(period_time.toString());
        status_textview = (TextView) view.findViewById(R.id.status_text);
        if (status) {
            status_textview.setText("Vibration Profile Applied");
            button.setText("Cancel Profile");
        } else {
            status_textview.setText("");
            button.setText("Activate Vibration Profile as per Class Schedule");
        }
        context = this.getContext();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = !status;
                if (status) {
                    status_textview.setText("Vibration Profile Applied");
                    button.setText("Cancel Profile");
                } else {
                    status_textview.setText("");
                    button.setText("Activate Vibration Profile as per Class Schedule");

                }
                preferences.edit().putBoolean(KeyStore.STATUS, status).apply();
                context.startService(new Intent(context, VibrationEnablerService.class));
            }
        });
    }
}
