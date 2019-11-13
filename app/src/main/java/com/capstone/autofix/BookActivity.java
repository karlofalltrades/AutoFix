package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BookActivity extends AppCompatActivity {
    private static final String TAG = BookActivity.class.getSimpleName();

    String shopname,cid,address;
    int sid;
    EditText dates,times,brand,model,year,desc,plate_num;
    Button booknow,bookcancel;
    private CoordinatorLayout coordinatorLayout;
    private SQLiteHandler db;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            sid = b.getInt("sid");
            shopname = b.getString("shopname");
        }
        db = new SQLiteHandler(BookActivity.this);
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");
        address = user.get("address");
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(shopname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinate);

        brand = (EditText) findViewById(R.id.car_brand);
        plate_num = (EditText) findViewById(R.id.plate_number);
        model = (EditText) findViewById(R.id.car_model);
        year = (EditText) findViewById(R.id.car_year);
        year.setTransformationMethod(null);
        desc = (EditText) findViewById(R.id.car_desc);

        bookcancel = (Button) this.findViewById(R.id.book_cancel);
        bookcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dates = (EditText) findViewById(R.id.date);
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
        times = (EditText) findViewById(R.id.time);
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
        booknow = (Button) this.findViewById(R.id.book_now);
        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mTime = times.getText().toString().trim();
                final String mDate = dates.getText().toString().trim();
                Log.d("",""+mTime+":"+mDate);
                if (!validate()){
                    String msg = "PLEASE FILL UP ALL FIELDS";
                    Snackbar sb = Snackbar.make(coordinatorLayout,msg,Snackbar.LENGTH_LONG);
                    sb.show();
                    return;
                } else {
                    Intent intent = new Intent(BookActivity.this,EmployeeActivity.class);
                    intent.putExtra("sid",sid);
                    intent.putExtra("shopname",shopname);
                    intent.putExtra("cid",cid);
                    intent.putExtra("stat","PENDING");
                    intent.putExtra("booktime",mTime);
                    intent.putExtra("bookdate",mDate);
                    intent.putExtra("address",address);
                    intent.putExtra("brand",brand.getText().toString());
                    intent.putExtra("model",model.getText().toString());
                    intent.putExtra("year",year.getText().toString());
                    intent.putExtra("plate_num",plate_num.getText().toString());
                    intent.putExtra("desc",desc.getText().toString());
                    intent.putExtra("edit","0");
                    startActivity(intent);
                }
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public boolean validate(){

        boolean valid = true;

        String time = times.getText().toString();
        String date = dates.getText().toString();
        String s = year.getText().toString();

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

        if (s.isEmpty() || s.length() > 4){
            year.setError("Please enter a valid year");
            valid = false;
        }else{
            year.setError(null);
        }
        return valid;
    }
}
