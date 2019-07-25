package com.project.yash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.yash.routine.R;

import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {
    private List<String> data;
    Context context;
    public ListAdapter(Context context, int resource, int textViewResourceId,List<String> objects) {
        super(context,resource,textViewResourceId,objects);
        data=objects;
        this.context=context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        LayoutInflater inflater=LayoutInflater.from(context);
        TextView view=(TextView)inflater.inflate(R.layout.list_item, parent, false);
        view.setText((String)((position+1)+".           "+data.get(position)));
        return view;
    }
}
