package com.capstone.autofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.google.android.material.snackbar.Snackbar;
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

public class EditBookingActivity extends AppCompatActivity {

    String cid,bid;
    EditText dates,times;
    Button booknow,bookcancel;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            cid = b.getString("cid");
            bid = b.getString("bookingID");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Schedule");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        booknow = (Button) this.findViewById(R.id.update_book);
        booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()){
                    String msg = "PLEASE FILL UP ALL FIELDS";
                    Toast.makeText(EditBookingActivity.this,msg,Toast.LENGTH_LONG).show();
                    return;
                } else {
                    final ProgressDialog progressDialog = new ProgressDialog(EditBookingActivity.this, R.style.MyAlertDialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Updating Your Schedule...");
                    progressDialog.show();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            updateRequest();
                            AlertDialog.Builder builder = new AlertDialog.Builder(EditBookingActivity.this, R.style.MyAlertDialog);
                            builder.setTitle("SCHEDULE UPDATE");
                            builder.setMessage("Your schedule has been updated");
                            builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //logoutUser();
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }, 3000);
                }
            }
        });
        dates = (EditText) findViewById(R.id.edit_date);
        dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        String format = "MM/dd/yy";
//                        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.TAIWAN);
//                        dates.setText(sdf.format(myCalendar.getTime()));
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
        times = (EditText) findViewById(R.id.edit_time);
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
    }

    public void updateRequest(){
        final String mTime = times.getText().toString().trim();
        final String mDate = dates.getText().toString().trim();

        String tag_string_req = "req_book";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditBookingActivity","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String msg = item.getString("msg");
                        Toast.makeText(EditBookingActivity.this,msg,Toast.LENGTH_LONG).show();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(EditBookingActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(EditBookingActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("EditBookingActivity","Booking Error:" +error.getMessage());
                Toast.makeText(EditBookingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cid",cid);
                params.put("bid",bid);
                params.put("booktime",mTime);
                params.put("bookdate",mDate);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    public void cancelRequest(){

        String tag_string_req = "req_book";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_CANCEL_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditBookingActivity","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String msg = item.getString("msg");
                        Toast.makeText(EditBookingActivity.this,msg,Toast.LENGTH_LONG).show();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(EditBookingActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(EditBookingActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("EditBookingActivity","Booking Error:" +error.getMessage());
                Toast.makeText(EditBookingActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("bid",bid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
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
