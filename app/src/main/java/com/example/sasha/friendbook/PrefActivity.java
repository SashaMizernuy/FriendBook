package com.example.sasha.friendbook;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class PrefActivity extends AppCompatActivity {
    ListView list;
    EditText edKey;
    EditText edSpiner;
    String[] types={"текст","телефон","число","дата","список","фото"};
    String[] nulls={"нет","да"};
    //String[] tab={"1","2"};
    ArrayList<Integer> widths = new ArrayList<>();

    ListOfMapsAdapter fieldAdapter;
    EditText edWidth;
    LinearLayout llSpinner;
    String curField;
    Spinner spTypes;
    Spinner spNull;
    Spinner spValues;
    EditText edTab;


    class MyOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            fieldAdapter.selectedIndex=position;
            if(fieldAdapter.selectedView!=null)
                fieldAdapter.selectedView.setBackgroundColor(Color.WHITE);
            fieldAdapter.selectedView=view;
            view.setBackgroundColor(Color.parseColor("#c0ff57"));

            ArrayMap<String, String> row=fieldAdapter.objects.get(position);

            edKey.setText(row.get("name"));
            edWidth.setText(row.get("width"));
            edTab.setText(row.get("page"));
            curField=MainActivity.keys.get(position);
            int fType=Integer.parseInt(row.get("type"));
            spTypes.setSelection(fType);
            spNull.setSelection(Integer.parseInt(row.get("empty")));
            ArrayList<String> ar=new ArrayList<>();
            if(fType==FieldType.LIST){
                String fList=MainActivity.fields.get(position).get("list");
                Log.i("Script","LIST="+fList);
                String [] spData =fList.split(";");
                for(int i=0;i<spData.length;i++)
                    ar.add(spData[i]);
            }
            spValues.setAdapter(new MyArrayAdapter(PrefActivity.this,R.layout.support_simple_spinner_dropdown_item,ar));
            spValues.setOnItemSelectedListener(new MyOnSpinerValuesSelectedListener());
        }
    }

    class MyOnSpinerSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if(spTypes.getSelectedItemPosition()==4)
                llSpinner.setVisibility(View.VISIBLE);
            else
                llSpinner.setVisibility(View.INVISIBLE);

        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
    class MyOnSpinerValuesSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String select=spValues.getSelectedItem().toString();
            edSpiner.setText(select);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        list=(ListView) findViewById(R.id.fields);
        edKey=(EditText)findViewById(R.id.edKey);
        spTypes=(Spinner)findViewById(R.id.spTypes);
        spNull=(Spinner)findViewById(R.id.spNull);
        spValues=(Spinner)findViewById(R.id.spValues);
        llSpinner=(LinearLayout)findViewById(R.id.llSpinner);
        edWidth=(EditText)findViewById(R.id.edWidth);
        edSpiner=(EditText)findViewById(R.id.edSpiner);
        edTab=(EditText) findViewById(R.id.edTab);
        ArrayAdapter spinnerTypeAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,types);
        spTypes.setAdapter(spinnerTypeAdapter);
        spTypes.setOnItemSelectedListener(new MyOnSpinerSelectedListener());

        ArrayAdapter spinnerNullAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,nulls);
        spNull.setAdapter(spinnerNullAdapter);

//        ArrayAdapter spinnerTabAdapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,tab);
//        spTab.setAdapter(spinnerTabAdapter);

        fieldAdapter=new ListOfMapsAdapter(this,MainActivity.fields,R.layout.list_item_f);
        ArrayList<String> keys=new ArrayList<>();
        keys.add("name");
        keys.add("type");
        keys.add("empty");
        keys.add("width");

        keys.add("page");


        widths.add(100);
        widths.add(40);
        widths.add(40);
        widths.add(40);
        widths.add(40);

        fieldAdapter.keys=keys;
        fieldAdapter.widths=widths;
        list.setAdapter(fieldAdapter);
        list.setOnItemClickListener(new MyOnItemClickListener());

    }




    public String maxField(){//метод который возвращает макс значение поля

        int max=0;
        for(int i=0;i<MainActivity.keys.size();i++) {
            int countField = Integer.parseInt(MainActivity.keys.get(i).substring(1));
            if(countField>max)
                max = countField;
        }
        return "f"+(max+1);
    }

    public void addField(View v){

        String fName=edKey.getText().toString().trim();
        if(fName.equals("")) {
            Toast toastAdd = Toast.makeText(getApplicationContext(), "Введите название поля!", Toast.LENGTH_LONG);
            toastAdd.show();
        }
        else {
            String fType=String.valueOf(spTypes.getSelectedItemPosition());
            String  fNull=String.valueOf(spNull.getSelectedItemPosition());

            String fPage=edTab.getText().toString();


            String fWidth=edWidth.getText().toString().trim();
            String fList="";
            if(Integer.parseInt(fType)==FieldType.LIST){
                MyArrayAdapter ad=(MyArrayAdapter) spValues.getAdapter();
                int n=ad.store.size();
                for(int i=0;i<n;i++)
                    if(i>0)
                        fList+=";"+spValues.getItemAtPosition(i);
                    else
                        fList+=spValues.getItemAtPosition(i);

            }
            if(fWidth.equals(""))
                fWidth="0";
            String maxF = maxField();
            Log.i("Script","MAXF= "+maxF);


            //////////////////Сделать транзакцию
          //  MainActivity.db.beginTransaction();
            MainActivity.db.execSQL("ALTER TABLE peoples ADD COLUMN " + maxF + " text");
            MainActivity.db.execSQL("INSERT INTO fields (fId,name,type,empty,width,list,page)values('" + maxF + "','" + fName + "',"+fType+","+fNull+","+fWidth+",'"+fList+"',"+fPage+")");
       //     MainActivity.db.endTransaction();

            MainActivity.db.close();
            MainActivity.db = MainActivity.dbHelper.getWritableDatabase();// подключаемся к БД
            MainActivity.keys.add(maxF);
            ArrayMap<String,String> ar=new ArrayMap<>();
            ar.put("name",fName);
            ar.put("type",fType);
            ar.put("empty",fNull);
            ar.put("width",fWidth);
            ar.put("list",fList);

            ar.put("page",fPage);

            MainActivity.fields.add(ar);
            MainActivity.chFields = true;
            fieldAdapter.notifyDataSetChanged();
        }
    }

    public void delField(View v){

        String  cFields="f1 integer primary key";
        String iFields="f1";
        MainActivity.keys.remove(curField);
        int n=Integer.parseInt(curField.substring(1))-1;
        for(int i=1;i<MainActivity.keys.size();i++){
            cFields=cFields+","+MainActivity.keys.get(i)+" text";
            iFields=iFields+","+MainActivity.keys.get(i);
        }
        MainActivity.db.execSQL("CREATE TABLE tmp("+cFields+");");
        MainActivity.db.execSQL("INSERT INTO tmp SELECT "+iFields+" FROM peoples;");
        MainActivity.db.execSQL("DROP TABLE peoples;");
        MainActivity.db.execSQL("ALTER TABLE tmp RENAME TO peoples;");
        MainActivity.db.execSQL("DELETE FROM fields WHERE fId='"+curField+"'");
        MainActivity.db.close();
        MainActivity.db=MainActivity.dbHelper.getWritableDatabase();// подключаемся к БД
        String fName=edKey.getText().toString().trim();
        MainActivity.fields.remove(n);
        MainActivity.chFields=true;
        fieldAdapter.notifyDataSetChanged();
    }

    public void changeField(View v){

        String fName=edKey.getText().toString().trim();
        if(fName.equals("")){
            Toast toastAdd = Toast.makeText(getApplicationContext(), "Выбирите поле а затем введите название!", Toast.LENGTH_LONG);
            toastAdd.show();
        }
        else{
            String fWidth=edWidth.getText().toString().trim();
            String fPage=edTab.getText().toString();
            if(fWidth.equals(""))
                fWidth="0";
            int n=Integer.parseInt(curField.substring(1))-1;
            String fType=String.valueOf(spTypes.getSelectedItemPosition());
            String fNull=String.valueOf(spNull.getSelectedItemPosition());

            String fList="";
            if(Integer.parseInt(fType)==FieldType.LIST){
                MyArrayAdapter ad=(MyArrayAdapter) spValues.getAdapter();
                int l=ad.store.size();
                for(int i=0;i<l;i++)
                    if(i>0)
                        fList+=";"+spValues.getItemAtPosition(i);
                    else
                        fList+=spValues.getItemAtPosition(i);
            }

            ArrayMap<String,String> ar=new ArrayMap<>();
            ar.put("name",fName);
            ar.put("type",fType);
            ar.put("empty",fNull);
            ar.put("width",fWidth);
            ar.put("list",fList);
            ar.put("page",fPage);
            MainActivity.db.execSQL("UPDATE fields SET name='" + fName + "',type="+fType+",empty="+fNull+",width="+fWidth+",list='"+fList+"',page="+fPage+" WHERE fId='" + curField + "'");
            MainActivity.db.close();
            MainActivity.db = MainActivity.dbHelper.getWritableDatabase();// подключаемся к БД
            MainActivity.fields.set(n,ar);
            MainActivity.chFields = true;
            fieldAdapter.notifyDataSetChanged();
        }
    }

    public void clearField(View v){
        edKey.getText().clear();
        edWidth.getText().clear();
        if(spTypes.getSelectedItemPosition()==4)
            spValues.setAdapter(null);
            edSpiner.getText().clear();
    }

    public void addSpiner(View v){
        MyArrayAdapter ad=(MyArrayAdapter) spValues.getAdapter();
        ad.store.add(edSpiner.getText().toString().trim());
    }

    public void delSpiner(View v){
        MyArrayAdapter ad=(MyArrayAdapter) spValues.getAdapter();
        ad.store.remove( spValues.getSelectedItemPosition());
    }

    public void changeSpiner(View v) {
        MyArrayAdapter ad=(MyArrayAdapter) spValues.getAdapter();
        ad.store.set( spValues.getSelectedItemPosition(),edSpiner.getText().toString());
    }
}
