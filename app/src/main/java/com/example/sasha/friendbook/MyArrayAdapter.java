package com.example.sasha.friendbook;

/**
 * Created by sasha on 07.12.17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
/**
 * Created by ACER on 26.11.2017.
 */

public class MyArrayAdapter extends ArrayAdapter {
    //View selectedView=null;
    //int selectedIndex=-1;
    public ArrayList<String> store;


    public MyArrayAdapter(Context context, int res,ArrayList<String>data){
        super(context,res,data);
        store=data;
    }



//    @SuppressLint("ResourceAsColor")
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        View view= super.getView(position,convertView,parent);
//        if(position==selectedIndex){
//            view.setBackgroundColor(Color.LTGRAY);
//            selectedView=view;
//        }
//        else
//            view.setBackgroundColor(Color.WHITE);
//        return view;
//    }
}
