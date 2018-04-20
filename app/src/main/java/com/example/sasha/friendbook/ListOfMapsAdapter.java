package com.example.sasha.friendbook;

/**
 * Created by sasha on 07.12.17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.graphics.Color;

public class ListOfMapsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<ArrayMap<String, String>> objects;
    ArrayList<String> keys=new ArrayList<>();
    ArrayList<Integer> widths=new ArrayList<>();

    View selectedView=null;
    int selectedIndex= -1;
    int listItem;

    ListOfMapsAdapter(Context context, ArrayList<ArrayMap<String, String>> peoples, int resource) {
        ctx = context;
        objects = peoples;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listItem=resource;
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
            view = lInflater.inflate(listItem, parent, false);
        if(position==selectedIndex){
            view.setBackgroundColor(Color.parseColor("#c0ff57"));
            selectedView=view;
        }
        else
            view.setBackgroundColor(Color.WHITE);
        LinearLayout ll=(LinearLayout) view;


        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = 80;
        layoutParams.width=600;

        ArrayMap<String, String> p = (ArrayMap<String, String>)getItem(position);

        int curColumns=ll.getChildCount();

        if(curColumns<keys.size())
            for(int i=curColumns;i<keys.size();i++) {
                TextView tv;
                ImageView iv;
                String vall=p.get(keys.get(i));
//?????????????????????????????????????????????????????????????
                if(vall==null || vall.startsWith("content")){
                    iv=new ImageView(ctx);
                    ll.addView(iv);
                    //Log.i("Script","ImageView create");

                }
                else {
                    //Log.i("Script","TextView create");
                    tv = new TextView(ctx);
                    ll.addView(tv);
                    int n=widths.get(i);
                    if(n>0)
                        tv.setWidth(n);
                    if(vall.contains("-")==true){
                        Log.i("Script","TextView length"+vall);


                    }
                }
            }


        for(int i=0;i<keys.size();i++) {
            View v=ll.getChildAt(i);
            String val= p.get(keys.get(i));
            if(v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(val);
                tv.setTextSize(15);
            }
            else if(v instanceof ImageView){
                ImageView iv=(ImageView) v;
                iv.setLayoutParams(new LinearLayout.LayoutParams(60,60));
                try {
                    InputStream imageStream = ctx.getContentResolver().openInputStream(Uri.parse(val));
                    iv.setImageBitmap(BitmapFactory.decodeStream(imageStream));


                }
                catch (NullPointerException e) {
                    iv.setImageBitmap(null);
                    e.printStackTrace();
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return view;
    }
}
