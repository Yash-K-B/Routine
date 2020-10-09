package com.project.yash;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.project.yash.routine.R;


public class Routines extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int currentWidgetId : appWidgetIds) {
            updateWidgetListView(context, appWidgetManager, currentWidgetId);
            //Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    private void updateWidgetListView(Context context,AppWidgetManager appWidgetManager, int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.routine_widget);
        Intent svcIntent = new Intent(context, ListViewWidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.listview, svcIntent);
        //remoteViews.setRemoteAdapter(appWidgetId, R.id.listview, svcIntent);
        remoteViews.setViewVisibility(R.id.update_bt,View.GONE);

        Intent updateIntent = new Intent(context,this.getClass());
        updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, Routines.class));
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,appWidgetId,updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.update_bt,pendingIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listview);
        appWidgetManager.updateAppWidget(appWidgetId,remoteViews);

        SystemClock.sleep(1000);
        remoteViews.setViewVisibility(R.id.update_bt,View.VISIBLE);
        appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
    }
}

