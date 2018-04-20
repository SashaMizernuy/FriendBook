package com.example.sasha.friendbook;

/**
 * Created by sasha on 07.12.17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context) {
        super(context, "myDataBase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table peoples(f1 integer primary key);");
        db.execSQL("create table fields(id integer primary key,fId text,name text,type int,empty int,width int,list text,page int);");
        db.execSQL("insert into fields (fId,name,type,empty,width,list,page) values ('f1','№',2,0,30,'',0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //peoples (1,2,3) 1,"Саша","M" 2,"Ира","Ж"
    //labels (id,fId,name) 1,1,"Номер" 2,2,"Имя" 3,3,"Пол"
}
