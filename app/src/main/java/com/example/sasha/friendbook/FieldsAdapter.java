package com.example.sasha.friendbook;

/**
 * Created by sasha on 07.12.17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FieldsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ArrayMap<String, String>> objects;
    ArrayList<String> keys=new ArrayList<>();

    View selectedView=null;
    int selectedIndex= -1;

    FieldsAdapter(Context context, ArrayList<ArrayMap<String, String>> peoples) {
        ctx = context;
        objects = peoples;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null)
            view = lInflater.inflate(R.layout.list_item, parent, false);
        if(position==selectedIndex){
            view.setBackgroundColor(Color.LTGRAY);
            selectedView=view;
        }
        else
            view.setBackgroundColor(Color.WHITE);
        LinearLayout ll=(LinearLayout) view;
        int curColumns=ll.getChildCount();
        if(curColumns<keys.size())
            for(int i=curColumns;i<keys.size();i++) {
                TextView tv=new TextView(ctx);
                ll.addView(tv);
            }

        ArrayMap<String, String> p = (ArrayMap<String, String>)getItem(position);
        for(int i=0;i<keys.size();i++) {
            TextView tv = (TextView) ll.getChildAt(i);
            tv.setText(p.get(keys.get(i)));
            tv.setTextSize(20);
            tv.setLineSpacing(10f,0f);
        }
        return view;
    }

}
