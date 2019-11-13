package com.capstone.autofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookOldActivity extends AppCompatActivity {

    String cid,address,brand,model,year,plate_num,shopname;
    EditText dates,times,desc;
    Button booknow,bookcancel;
    final Calendar myCalendar = Calendar.getInstance();
    int sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_old);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            sid = b.getInt("sid");
            address = b.getString("address");
            cid = b.getString("cid");
            brand = b.getString("brand");
            model = b.getString("model");
            year = b.getString("year");
            plate_num = b.getString("plate_number");
        }


        desc = (EditText) findViewById(R.id.c_desc_old);
        dates = (EditText) findViewById(R.id.c_date_old);
        myCalendar.add(Calendar.DATE,1);
        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        DateFormat df_long = DateFormat.getDateInstance(DateFormat.LONG);
                        String format = df_long.format(myCalendar.getTime());
                        dates.setText(format);
                    }
                }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.setMinDate(myCalendar);
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.setThemeDark(true);
                dpd.setAccentColor("#858585");
                dpd.show(getSupportFragmentManager(),"");
            }
        });
        times = (EditText) findViewById(R.id.c_time_old);
        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpl = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        String time = hourOfDay+":"+minute;
                        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                        Date dateme = null;
                        try {
                            dateme = fmt.parse(time);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");
                        String formattedTime = fmtOut.format(dateme);
                        times.setText(formattedTime);
                    }
                }, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false);
                tpl.setThemeDark(true);
                tpl.setMinTime(8,00,00);
                tpl.setMaxTime(21,00,00);
                tpl.setVersion(TimePickerDialog.Version.VERSION_2);
                tpl.setAccentColor("#858585");
                tpl.show(getSupportFragmentManager(),"");
            }
        });
        bookcancel = (Button) this.findViewById(R.id.cancel_old);
        bookcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        booknow = (Button) this.findViewById(R.id.book_old);
        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mTime = times.getText().toString().trim();
                final String mDate = dates.getText().toString().trim();
                Log.d("",""+mTime+":"+mDate);
                if (!validate()){
                    Toast.makeText(getApplicationContext(),"Please fill up necessary fields",Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Intent intent = new Intent(BookOldActivity.this,EmployeeActivity.class);
                    intent.putExtra("sid",sid);
                    intent.putExtra("shopname",shopname);
                    intent.putExtra("cid",cid);
                    intent.putExtra("stat","PENDING");
                    intent.putExtra("booktime",mTime);
                    intent.putExtra("bookdate",mDate);
                    intent.putExtra("address",address);
                    intent.putExtra("brand",brand);
                    intent.putExtra("model",model);
                    intent.putExtra("year",year);
                    intent.putExtra("plate_num",plate_num);
                    intent.putExtra("desc",desc.getText().toString());
                    intent.putExtra("edit","1");
                    startActivity(intent);
                }
            }
        });
    }

    public boolean validate(){

        boolean valid = true;

        String time = times.getText().toString();
        String date = dates.getText().toString();

        if (time.isEmpty()){
            times.setError("Please enter a time");
            valid = false;
        }else{
            times.setError(null);
        }

        if (date.isEmpty()){
            dates.setError("Please enter a date");
            valid = false;
        }else{
            dates.setError(null);
        }
        return valid;
    }
}
