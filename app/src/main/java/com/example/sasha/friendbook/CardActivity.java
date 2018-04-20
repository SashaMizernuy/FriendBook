package com.example.sasha.friendbook;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class CardActivity extends AppCompatActivity {

    TabHost tabHost;
    FrameLayout frLayout;
    int pageNum;
    ArrayList<LinearLayout> lVerticalList = new ArrayList<>();
    ArrayList<View> viewsList = new ArrayList<>();
    final Context context=this;
    Calendar myCalendar=Calendar.getInstance();
    TextView txtDate;
    ImageView imageContent;

    public void createFields(){
        LinearLayout.LayoutParams layoutParamsll = new LinearLayout.LayoutParams(170, LinearLayout.LayoutParams.WRAP_CONTENT);

        viewsList.clear();
        for (int i = 0; i < MainActivity.keys.size(); i++) {
            String fName = MainActivity.keys.get(i);
            String fLabel = MainActivity.fields.get(i).get("name");
            String fType=MainActivity.fields.get(i).get("type");
            String fList=MainActivity.fields.get(i).get("list");
            int nPage=Integer.parseInt(MainActivity.fields.get(i).get("page"));

            LinearLayout lH = new LinearLayout(this);
            lH.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(this);
            tv.setLayoutParams(layoutParamsll);
            tv.setText(fLabel);
            lH.addView(tv);

            Spinner sp=new Spinner(this);

            final EditText ed = new EditText(this);

            ImageView photo=new ImageView(this);

            TextView txt=new TextView(this);


            switch (Integer.parseInt(fType)){
                case FieldType.TEXT:
                case FieldType.TEL:
                case FieldType.DIGIT:
                    if (i == 0)
                        ed.setEnabled(false);
                    ed.setLayoutParams(layoutParamsll);
                    viewsList.add(ed);
                    lH.addView(ed);
                    if (Integer.parseInt(fType)==FieldType.TEL)
                        ed.setOnLongClickListener(new MyOnLongClickListener()); //Листенер длинный с выводом лога
                    break;
                case  FieldType.LIST:
                    sp.setLayoutParams(layoutParamsll);
                    viewsList.add(sp);
                    lH.addView(sp);
                    String [] spData =fList.split(";");
                    ArrayAdapter spinnerValueAdapter=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,spData);
                    sp.setAdapter(spinnerValueAdapter);
                    break;
                case FieldType.PHOTO:
                    Log.i("Script","PHOTO");
                    photo.setLayoutParams(new LinearLayout.LayoutParams(60,60));
                    viewsList.add(photo);
                    lH.addView(photo);
                    ImageButton btPhoto=new ImageButton(this);
                    btPhoto.setImageResource(R.drawable.image);
                    btPhoto.setOnClickListener(new MyOnImageClickListener());
                    lH.addView(btPhoto);
                    break;
                case FieldType.DATA:
                    txt.setLayoutParams(layoutParamsll);
                    viewsList.add(txt);
                    lH.addView(txt);
                    ImageButton btDate=new ImageButton(this);
                    btDate.setImageResource(R.drawable.calendar);
                    btDate.setOnClickListener(new MyOnDateClickListener());
                    lH.addView(btDate);
                    break;

            }
            Log.i("Script","Page= "+nPage);
            LinearLayout lV=lVerticalList.get(nPage);
            Log.i("Script","lVertical= "+lV);

            lV.addView(lH);
        }

    }

    public void initFields() {
        MainActivity.chFields = false;
        createFields();
        //peoples=getPeoples(db);

    }

    public void createCard(){


        LinearLayout l=(LinearLayout)findViewById(R.id.main);
        frLayout=(FrameLayout)l.getChildAt(1);
        tabHost=(TabHost)findViewById(R.id.tabhost);

        TabHost.TabContentFactory tabFactory=new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String s) {
                return lVerticalList.get(Integer.parseInt(s));
            }
        };




        Cursor cursor=MainActivity.db.rawQuery("select max(page) from fields",null);
        cursor.moveToFirst();
        pageNum=cursor.getInt(0);
        if(pageNum>0){
            tabHost.setup();// инициализация
            TabHost.TabSpec tabSpec;
            for(int i=0;i<=pageNum;i++) {
                LinearLayout lV = new LinearLayout(this);
                lV.setOrientation(LinearLayout.VERTICAL);
                lVerticalList.add(lV);
                tabSpec = tabHost.newTabSpec(""+i);// создаем вкладку и указываем тег
                tabSpec.setIndicator("Страница"+i).setContent(tabFactory);// ставим индикатор название вкладки// указываем id компонента из FrameLayout, он и станет содержимым
                tabHost.addTab(tabSpec);// добавляем в корневой элемент
            }
            tabHost.setCurrentTabByTag("Вкладка0");// первая вкладка будет выбрана по умолчанию
        }
        else{
            RelativeLayout rl=new RelativeLayout(this);
            LinearLayout lV = new LinearLayout(this);
            lV.setOrientation(LinearLayout.VERTICAL);
            rl.addView(lV);
            lVerticalList.add(lV);
            frLayout.addView(rl);
        }
        initFields();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createCard();
    }

    class MyOnLongClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view) {
            EditText ed=(EditText)view;
            String tel = ed.getText().toString();
            Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                startActivity(call);
            return true;
        }
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            month = month+1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;
            for(int i=0;i<viewsList.size();i++) {
                View v=viewsList.get(i);
                if(v instanceof TextView) {
                    TextView txt =(TextView) v;
                    if (txtDate == txt)
                        txt.setText(formattedDayOfMonth+"-"+formattedMonth+"-"+year);
                }
            }
        }
    };

    class MyOnDateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view){
            LinearLayout l=(LinearLayout) view.getParent();
            txtDate=(TextView) l.getChildAt(1);
            new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }

    }


    class MyOnImageClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            LinearLayout l=(LinearLayout) view.getParent();
            imageContent=(ImageView) l.getChildAt(1);
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);//Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
            photoPickerIntent.putExtra("line","good");
            photoPickerIntent.setType("image/*");//Тип получаемых объектов - image:
            startActivityForResult(photoPickerIntent, 1);//Запускаем переход с ожиданием обратного результата в виде информации об изображении:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==1) {
            if(resultCode == RESULT_OK){
                Uri imageUri = intent.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    for(int i=0;i<viewsList.size();i++) {
                        View v=viewsList.get(i);
                        if(v instanceof ImageView) {
                            ImageView iv=(ImageView)v;
                            if (imageContent == iv) {
                                iv.setImageBitmap(BitmapFactory.decodeStream(imageStream));
                                iv.setTag(imageUri.toString());
                            }
                        }
                    }
                    imageContent=null;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}
