package com.project.yash;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.project.yash.models.Schedule;
import com.project.yash.routine.R;
import com.yash.logging.LogHelper;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ListViewWidgetService extends RemoteViewsService {
    static int x = 1;
    private Schedule_DB schedule_db;
    private static final String TAG = "ListViewWidgetService";
    LogHelper logHelper = LogHelper.getInstance("Routine");


    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListViewRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListViewRemoteViewsFactory implements RemoteViewsFactory {
        private Context mContext;
        private ArrayList<Schedule> records;
        private int appWidgetId;

        public ListViewRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            records = new ArrayList<>();
        }

        public RemoteViews getViewAt(int position) {
            // position will always range from 0 to getCount() - 1.
            // Construct a RemoteViews item based on the app widget item XML file, and set the
            // text based on the position.
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.s_lists);
            Schedule data = records.get(position);
            rv.setTextViewText(R.id.stime, data.getTime());
            rv.setTextViewText(R.id.subject, data.getSubject());
            //Log.i("data", rv+":"+data.getTime()+":"+ data.getSubject());
            return rv;
        }

        public int getCount() {
            return records.size();
        }

        public int getViewTypeCount() {
            return 1;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onCreate() {

            records.add(new Schedule("7:00 - 7:40am", ""));
            records.add(new Schedule("7:40 - 8:20am", "FLAT(SNM)"));
            records.add(new Schedule("8:20 - 9:00am", "Math-III"));
            records.add(new Schedule("9:00 - 9:40am", "DAA(SM)"));
            records.add(new Schedule("9:40 - 10:20am", "DBS(AS)"));
            records.add(new Schedule("10 minutes", "Break"));
            records.add(new Schedule("10:30 - 12:30am", "DBS LAB(Knowledge Center)"));
            Log.i("data", "item created");
        }

        @Override
        public void onDataSetChanged() {
            records.clear();
            schedule_db = new Schedule_DB(getApplicationContext());
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_WEEK) - 1;
            try {
                for (int i = 0; i < schedule_db.getPERIODS().length; i++) {
                    if (schedule_db.getDAYS()[day][i].equals("null"))
                        continue;
                    int t1_h = schedule_db.getPERIODS()[i][0][0];
                    int t1_m = schedule_db.getPERIODS()[i][0][1];
                    int t2_h = schedule_db.getPERIODS()[i][1][0];
                    int t2_m = schedule_db.getPERIODS()[i][1][1];
                    String s = String.format(Locale.US, "%02d:%02d%s - %02d:%02d%s", ((t1_h > 12) ? t1_h - 12 : t1_h), t1_m, ((t1_h > 12) ? "PM" : "AM"), ((t2_h > 12) ? t2_h - 12 : t2_h), t2_m, ((t2_h > 12) ? "PM" : "AM"));
                    //String s=((t1_h>12)?t1_h-12:t1_h)+":"+t1_m+((t1_h>12)?"PM":"AM")+" - "+((t2_h>12)?t2_h-12:t2_h)+":"+t2_m+((t2_h>12)?"PM":"AM");
                    records.add(new Schedule(s, schedule_db.getDAYS()[day][i]));
                }
                if (day == 0) {
                    records.add(new Schedule("", "         PUBLIC HOLIDAY"));
                }
                if (records.isEmpty()) {
                    records.add(new Schedule("               No classes", "      FEELS LIKE HOLIDAY"));
                }
            } catch (NullPointerException e) {
                records.add(new Schedule("", "      No Records"));
                logHelper.d(TAG, e.getMessage());
            }

            /*
            if(day>1&&day<7) {
                records.add(new schedule("7:00 - 7:40am", Schedule_DB.days[day - 2][0]));
                records.add(new schedule("7:40 - 8:20am", Schedule_DB.days[day - 2][1]));
                records.add(new schedule("8:20 - 9:00am", Schedule_DB.days[day - 2][2]));
                records.add(new schedule("9:00 - 9:40am", Schedule_DB.days[day - 2][3]));
                records.add(new schedule("9:40 - 10:20am", Schedule_DB.days[day - 2][4]));
                records.add(new schedule("10 minutes", "Break"));
                records.add(new schedule("10:30 - 12:30am", Schedule_DB.days[day - 2][5]));
                Log.i("data", "item updated" + x);
                x++;
            }
            else if(day==7)
            {
                records.add(new schedule("               No classes","      FEELS LIKE HOLIDAY"));
            }
            else
            {
                records.add(new schedule("","         PUBLIC HOLIDAY"));
            }*/

        }

        public void onDestroy() {

            records.clear();

        }

        public boolean hasStableIds() {

            return true;

        }

        public RemoteViews getLoadingView() {

            return null;

        }

    }
}