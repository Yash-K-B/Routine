package com.project.yash.ui;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.project.yash.ListAdapter;
import com.project.yash.Routines;
import com.project.yash.ScheduleDataBase;
import com.project.yash.VibrationEnablerService;
import com.project.yash.routine.R;
import com.project.yash.storage.ScheduleDao;
import com.project.yash.storage.ScheduleDatabase;
import com.project.yash.storage.ScheduleEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class ScheduleMakerPage extends Fragment implements TimePickerDialog.OnTimeSetListener{
    ScheduleDataBase dataBase;
    Context context;
    int counter;
    String[][] values;
    ListView lv;
    EditText editText1;
    View layout;
    List<String> data;
    boolean flag=false;
    ScheduleMakerPage scheduleMakerPage;
    ScheduleDao scheduleDao;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        context=this.getContext();
        data= new ArrayList<>();
        return inflater.inflate(R.layout.own_schedule,container,false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        scheduleDao = ScheduleDatabase.getInstance(context).getScheduleDao();
    }

    public static ScheduleMakerPage newInstance() {

        Bundle args = new Bundle();

        ScheduleMakerPage fragment = new ScheduleMakerPage();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        dataBase=new ScheduleDataBase(context.getApplicationContext());
        layout=view;
        lv=(ListView)view.findViewById(R.id.schedules);
        ListAdapter adapter=new ListAdapter(context,R.id.schedules,R.layout.list_item,data);
        lv.setAdapter(adapter);
        final Button button1=(Button)view.findViewById(R.id.button_create_period);
        final Button button2=(Button)view.findViewById(R.id.button_delete_last);
        final Button button3=(Button)view.findViewById(R.id.button_add_to_database);
        button2.setEnabled(false);
        button3.setEnabled(false);
        editText1=(EditText) layout.findViewById(R.id.time_s_pick);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                TimePickerDialog timePickerDialog= new TimePickerDialog(context,ScheduleMakerPage.this,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                timePickerDialog.show();



            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=editText1.getText().toString();
                if(s.equals(""))
                {
                    if(!data.isEmpty()){
                        data.remove(data.get(data.size()-1));
                        Log.i("data_service",data.toString());

                    }
                    lv.invalidateViews();
                    if(data.isEmpty())
                    {
                        button3.setEnabled(false);
                        button2.setEnabled(false);
                    }

                }
                else {
                    editText1.setText("");
                    Button button=(Button)layout.findViewById(R.id.button_create_period);
                    button.setEnabled(true);
                    if(data.isEmpty())
                        button2.setEnabled(false);
                    TextView textView=(TextView) layout.findViewById(R.id.send_data);
                    textView.setVisibility(View.GONE);
                }
            }
        });
        TextView textView=(TextView) view.findViewById(R.id.send_data);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=true;
                EditText editText=(EditText)layout.findViewById(R.id.time_s_pick);
                String p=editText.getText().toString();
               for(String s:data)
                {
                    boolean tempFlag=false;
                    Pattern pattern=Pattern.compile("-");
                    String[] arr =pattern.split(s,2);
                    String[] arr2 =pattern.split(p,2);
                    Pattern pattern1=Pattern.compile(":");
                    String[] time_1 =pattern1.split(arr[0],2);         //time_1[0] : time_2[1]
                    String[] time_2 =pattern1.split(arr[1],2);          //time1_1[0] : time1_2[1]
                    String[] time1_1 =pattern1.split(arr2[0],2);
                    String[] time1_2 =pattern1.split(arr2[1],2);
                    int h1=Integer.parseInt(time_1[0]);
                    int m1=Integer.parseInt(time_1[1]);
                    int h2=Integer.parseInt(time_2[0]);
                    int m2=Integer.parseInt(time_2[1]);
                    int hh1=Integer.parseInt(time1_1[0]);
                    int mm1=Integer.parseInt(time1_1[1]);
                    int hh2=Integer.parseInt(time1_2[0]);
                    int mm2=Integer.parseInt(time1_2[1]);
                    if(((h1>hh2)||((h1==hh2)&&(m1>=mm2)))||((h2<hh1)||((h2==hh1)&&(m2<=mm1))))
                    {
                        tempFlag=true;
                    }
                    Log.i("data_service","internal flag:"+tempFlag+"");
                  flag=flag&&tempFlag;

                }

                if(flag) {
                    data.add(p);
                    lv.invalidate();
                }
                else
                {
                    TextView textView1=(TextView)layout.findViewById(R.id.error_report);
                    textView1.setText("Overlapping schedule try once more...");
                }
                v.setVisibility(View.GONE);
                Button button=(Button)layout.findViewById(R.id.button_create_period);
                button.setEnabled(true);
                editText.setText("");
                button2.setEnabled(true);
                button3.setEnabled(true);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!data.isEmpty()){
                    values=new String[data.size()][7];
                    counter=0;
                   /* for (String s:data)
                    {
                        Pattern pattern=Pattern.compile("-");
                        String arr[]=pattern.split(s,2);
                        Pattern pattern1=Pattern.compile(":");
                        String time_1[]=pattern1.split(arr[0],2);
                        String time_2[]=pattern1.split(arr[1],2);
                        Log.i("data_service",time_1[0]+":"+time_1[1]+"-"+time_2[0]+":"+time_2[1]);

                    }*/
                   String s=data.get(counter);
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    LayoutInflater inflater =LayoutInflater.from(context);
                    final View view1=inflater.inflate(R.layout.schedule_set_dialog,null,false);
                    final TextView t1=(TextView)view1.findViewById(R.id.text_schedule);
                    t1.setText((String)((counter+1)+".\t\t\t\t"+s));
                    final Button b1=(Button)view1.findViewById(R.id.button_next);
                    if((counter+1)<data.size())
                        b1.setText("Next");
                    else
                        b1.setText("Done");
                    builder.setView(view1);
                    final TextView l1=(TextView)view1.findViewById(R.id.l_mon);
                    final TextView l2=(TextView)view1.findViewById(R.id.l_tue);
                    final TextView l3=(TextView)view1.findViewById(R.id.l_wed);
                    final TextView l4=(TextView)view1.findViewById(R.id.l_thu);
                    final TextView l5=(TextView)view1.findViewById(R.id.l_fri);
                    final TextView l6=(TextView)view1.findViewById(R.id.l_sat);
                    final EditText e1=(EditText)view1.findViewById(R.id.text_monday);
                    final EditText e2=(EditText)view1.findViewById(R.id.text_tuesday);
                    final EditText e3=(EditText)view1.findViewById(R.id.text_wednesday);
                    final EditText e4=(EditText)view1.findViewById(R.id.text_thursday);
                    final EditText e5=(EditText)view1.findViewById(R.id.text_friday);
                    final EditText e6=(EditText)view1.findViewById(R.id.text_saturday);
                    e1.setVisibility(View.GONE);
                    e2.setVisibility(View.GONE);
                    e3.setVisibility(View.GONE);
                    e4.setVisibility(View.GONE);
                    e5.setVisibility(View.GONE);
                    e6.setVisibility(View.GONE);
                    l1.setVisibility(View.GONE);
                    l2.setVisibility(View.GONE);
                    l3.setVisibility(View.GONE);
                    l4.setVisibility(View.GONE);
                    l5.setVisibility(View.GONE);
                    l6.setVisibility(View.GONE);
                    final TextView d1=(TextView) view1.findViewById(R.id.mon);
                    final TextView d2=(TextView) view1.findViewById(R.id.tue);
                    final TextView d3=(TextView) view1.findViewById(R.id.wed);
                    final TextView d4=(TextView) view1.findViewById(R.id.thu);
                    final TextView d5=(TextView) view1.findViewById(R.id.fri);
                    final TextView d6=(TextView) view1.findViewById(R.id.sat);
                    d1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l1.getVisibility()==View.GONE){
                                d1.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d1.setTextColor(Color.WHITE);
                                l1.setVisibility(View.VISIBLE);
                                e1.setVisibility(View.VISIBLE);}
                            else
                            {
                                d1.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d1.setTextColor(Color.parseColor("#757575"));
                                l1.setVisibility(View.GONE);
                                e1.setVisibility(View.GONE);
                            }
                            }

                    });
                    d2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l2.getVisibility()==View.GONE){
                                d2.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d2.setTextColor(Color.WHITE);
                                l2.setVisibility(View.VISIBLE);
                                e2.setVisibility(View.VISIBLE);}
                            else
                            {
                                d2.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d2.setTextColor(Color.parseColor("#757575"));
                                l2.setVisibility(View.GONE);
                                e2.setVisibility(View.GONE);
                            }

                        }
                    });
                    d3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l3.getVisibility()==View.GONE){
                                d3.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d3.setTextColor(Color.WHITE);
                                l3.setVisibility(View.VISIBLE);
                                e3.setVisibility(View.VISIBLE);}
                            else
                            {
                                d3.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d3.setTextColor(Color.parseColor("#757575"));
                                l3.setVisibility(View.GONE);
                                e3.setVisibility(View.GONE);
                            }
                        }
                    });
                    d4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l4.getVisibility()==View.GONE){
                                d4.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d4.setTextColor(Color.WHITE);
                                l4.setVisibility(View.VISIBLE);
                                e4.setVisibility(View.VISIBLE);}
                            else
                            {
                                d4.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d4.setTextColor(Color.parseColor("#757575"));
                                l4.setVisibility(View.GONE);
                                e4.setVisibility(View.GONE);
                            }
                        }
                    });
                    d5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l5.getVisibility()==View.GONE){
                                d5.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d5.setTextColor(Color.WHITE);
                                l5.setVisibility(View.VISIBLE);
                                e5.setVisibility(View.VISIBLE);}
                            else
                            {
                                d5.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d5.setTextColor(Color.parseColor("#757575"));
                                l5.setVisibility(View.GONE);
                                e5.setVisibility(View.GONE);
                            }
                        }
                    });
                    d6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(l6.getVisibility()==View.GONE){
                                d6.setBackground(new ColorDrawable(Color.parseColor("#FF3D00")));
                                d6.setTextColor(Color.WHITE);
                                l6.setVisibility(View.VISIBLE);
                                e6.setVisibility(View.VISIBLE);}
                            else
                            {
                                d6.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d6.setTextColor(Color.parseColor("#757575"));
                                l6.setVisibility(View.GONE);
                                e6.setVisibility(View.GONE);
                            }
                        }
                    });
                    builder.setCancelable(false);

                    final AlertDialog dialog = builder.create();
                    dialog.show();

                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if((counter+1)<data.size())
                            {
                                //retrieving data entered
                                values[counter][0]=data.get(counter);
                                if(e1.getVisibility()!=View.GONE){
                                    values[counter][1]=e1.getText().toString();
                                    e1.setText("");
                                }
                                if(e2.getVisibility()!=View.GONE){
                                    values[counter][2]=e2.getText().toString();
                                    e2.setText("");
                                }
                                if(e3.getVisibility()!=View.GONE){
                                    values[counter][3]=e3.getText().toString();
                                    e3.setText("");
                                }
                                if(e4.getVisibility()!=View.GONE){
                                    values[counter][4]=e4.getText().toString();
                                    e4.setText("");
                                }
                                if(e5.getVisibility()!=View.GONE){
                                    values[counter][5]=e5.getText().toString();
                                    e5.setText("");
                                }
                                if(e6.getVisibility()!=View.GONE){
                                    values[counter][6]=e6.getText().toString();
                                    e6.setText("");
                                }

                                ////------------------------------------------
                                 counter++;
                                if((counter+1)<data.size())         //predict the text of submit button
                                    b1.setText("Next");
                                else
                                    b1.setText("Done");
                                e1.setVisibility(View.GONE);    //make all view GONE
                                e2.setVisibility(View.GONE);
                                e3.setVisibility(View.GONE);
                                e4.setVisibility(View.GONE);
                                e5.setVisibility(View.GONE);
                                e6.setVisibility(View.GONE);
                                l1.setVisibility(View.GONE);
                                l2.setVisibility(View.GONE);
                                l3.setVisibility(View.GONE);
                                l4.setVisibility(View.GONE);
                                l5.setVisibility(View.GONE);
                                l6.setVisibility(View.GONE);
                                d1.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6"))); //reset all buttons
                                d1.setTextColor(Color.parseColor("#757575"));
                                d2.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d2.setTextColor(Color.parseColor("#757575"));
                                d3.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d3.setTextColor(Color.parseColor("#757575"));
                                d4.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d4.setTextColor(Color.parseColor("#757575"));
                                d5.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d5.setTextColor(Color.parseColor("#757575"));
                                d6.setBackground(new ColorDrawable(Color.parseColor("#E6E6E6")));
                                d6.setTextColor(Color.parseColor("#757575"));
                                t1.setText((String)((counter+1)+".\t\t\t\t"+data.get(counter)));
                            }
                            else {
                                dataBase.removeRow();
                                scheduleDao.clear();
                                //retrieving data entered
                                values[counter][0]=data.get(counter);
                                if(e1.getVisibility()!=View.GONE){
                                    values[counter][1]=e1.getText().toString();
                                }
                                if(e2.getVisibility()!=View.GONE){
                                    values[counter][2]=e2.getText().toString();
                                }
                                if(e3.getVisibility()!=View.GONE){
                                    values[counter][3]=e3.getText().toString();
                                }
                                if(e4.getVisibility()!=View.GONE){
                                    values[counter][4]=e4.getText().toString();
                                }
                                if(e5.getVisibility()!=View.GONE){
                                    values[counter][5]=e5.getText().toString();
                                }
                                if(e6.getVisibility()!=View.GONE){
                                    values[counter][6]=e6.getText().toString();
                                }
                                dialog.dismiss();
                                data.clear();
                                lv.invalidateViews();
                                //checking data
                                for (String[] value : values) {
                                    dataBase.insert(value);
                                    scheduleDao.insert(new ScheduleEntity(value[0], value[1], value[2], value[3], value[4], value[5], value[6], null));
                                    Log.i("data_service", value[0] + "/" + value[1] + "/" + value[2] + "/" + value[3] + "/" + value[4] + "/" + value[5] + "/" + value[6] + "/");
                                }
                                button2.setEnabled(false);
                                button3.setEnabled(false);
                                context.startService(new Intent(context, VibrationEnablerService.class));
                                //getActivity().recreate();
                                Thread thread=new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Thread.sleep(5000);
                                        }
                                        catch(InterruptedException e)
                                        {
                                            e.printStackTrace();
                                        }
                                        Intent intent=new Intent(context, Routines.class);
                                        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                                        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
                                        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context,Routines.class));
                                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                                        context.sendBroadcast(intent);
                                    }
                                });
                                thread.start();

                            }
                        }
                    });
                    Button b2=(Button)view1.findViewById(R.id.button_cancel);
                    b2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           dialog.dismiss();
                        }
                    });

                }
            }
        });


    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Button b1=(Button)layout.findViewById(R.id.button_delete_last);
        if(!b1.isEnabled())
            b1.setEnabled(true);

        String s="";
        EditText editText=(EditText) layout.findViewById(R.id.time_s_pick);
        TextView textView1=(TextView)layout.findViewById(R.id.error_report);
        textView1.setText("");
        String p=editText.getText().toString();
        if(p.equals("")) {
            if(hourOfDay<10)
                s="0";
            s=s+hourOfDay+":";
            if(minute<10)
                s+="0";
            s+=minute;
            //s = hourOfDay + ":" + minute;
            editText.setText(s);
            flag=true;
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar=Calendar.getInstance();
                    TimePickerDialog timePickerDialog= new TimePickerDialog(context,ScheduleMakerPage.this,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                    timePickerDialog.show();
                }
            },100);
        }
        else if(flag) {
            Pattern pattern=Pattern.compile(":");
            String[] arr =pattern.split(p);
            if(hourOfDay>Integer.parseInt(arr[0])||hourOfDay==Integer.parseInt(arr[0])&&minute>Integer.parseInt(arr[1])) {
                s=p+"-";
                if(hourOfDay<10)
                    s+="0";
                s=s+hourOfDay+":";
                if(minute<10)
                    s+="0";
                s+=minute;
               // s = p + "-" + hourOfDay + ":" + minute;
                editText.setText(s);
                Button button = (Button) layout.findViewById(R.id.button_create_period);
                button.setEnabled(false);
                TextView textView = (TextView) layout.findViewById(R.id.send_data);
                textView.setVisibility(View.VISIBLE);
                flag = false;
            }
            Log.i("data_service",arr[0]+":"+arr[1]);
        }

        Log.i("data_service",hourOfDay+":"+minute+"/"+p);
    }
}
