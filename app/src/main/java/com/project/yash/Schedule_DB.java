package com.project.yash;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.project.yash.models.Schedule;
import com.project.yash.storage.ScheduleDatabase;
import com.project.yash.storage.ScheduleEntity;
import com.yash.logging.LogHelper;

import java.util.List;
import java.util.regex.Pattern;

public class Schedule_DB {
    private static final String TAG = "Schedule_DB";
    ScheduleDataBase dataBase;
    LogHelper logHelper = LogHelper.getInstance("Routine");
    static int[][][] periods = {{{7, 0}, {7, 40}}, {{7, 40}, {8, 20}}, {{8, 20}, {9, 0}}, {{9, 0}, {9, 40}}, {{9, 40}, {10, 20}}, {{10, 30}, {12, 30}}};
    static String[][] days = {{"", "HONS.", "OB", "AMATH-III", "DAA(SM)", "DAA LAB(Knowledge Centre)"}, {"AMATH-III", "COA(PBS)", "DBS(AS)", "FLAT(SNM)", "HONS.", "COA LAB(Knowledge Center)"},
            {"", "FLAT(SNM)", "AMATH-III", "DAA(SM)", "DBS(AS)", "DBS LAB(Knowledge Centre)"}, {"", "DBS(AS)", "OB", "COA(PBS)", "FLAT(SNM)", "FLAT LAB(Central Computing)"},
            {"", "HONS.", "OB", "DAA(SM)", "COA(PBS)", "Skill Project H.(Knowledge Center)"}};
    private String[][] DAYS;
    private int[][][] PERIODS;

    public Schedule_DB(Context context) {
        dataBase = new ScheduleDataBase(context);
        Cursor c = dataBase.getData();
        List<ScheduleEntity> schedules = ScheduleDatabase.getInstance(context).getScheduleDao().getAll();
        for(ScheduleEntity schedule: schedules){
            logHelper.d(TAG,schedule.toString());
        }
        if (c != null && c.getCount() > 0) {
            DAYS = new String[7][c.getCount()];
            PERIODS = new int[c.getCount()][2][2];
            Log.i("data_service", c.getCount() + "");
            //c.moveToFirst();
            int i = 0;
            while (c.moveToNext()) {

                Pattern pattern = Pattern.compile("-");
                String[] arr = pattern.split(c.getString(0), 2);
                Pattern pattern1 = Pattern.compile(":");
                String[] time1 = pattern1.split(arr[0], 2);
                String[] time2 = pattern1.split(arr[1], 2);
                PERIODS[i][0][0] = Integer.parseInt(time1[0]);
                PERIODS[i][0][1] = Integer.parseInt(time1[1]);
                PERIODS[i][1][0] = Integer.parseInt(time2[0]);
                PERIODS[i][1][1] = Integer.parseInt(time2[1]);
                for (int j = 0; j < 7; j++) {
                    DAYS[j][i] = c.getString(j + 1);
                    Log.i("data_service", DAYS[j][i] + "/");
                }
                i++;
            }
        } else {
            DAYS = null;
            PERIODS = null;
        }

        if (c != null) {
            c.close();
        }
        dataBase.onDestroy();
    }

    public int[][][] getPERIODS() {
        return PERIODS;
    }

    public String[][] getDAYS() {
        return DAYS;
    }

}
