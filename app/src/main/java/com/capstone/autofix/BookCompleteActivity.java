package com.capstone.autofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookCompleteActivity extends AppCompatActivity {

    String bid,baddr,bdate,btime,bstat,bshop,bemp,bcont,bimg;
    TextView bookaddr,bookdate,booktime,bookstat,bookshop,bookemp,bookcont;
    CircleImageView bookemp_image;
    Button done;
    private String IMAGE_URL ="http://192.168.43.93/AutoFix/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_complete);

        Bundle b = this.getIntent().getExtras();

        if(b!=null) {
            bid = b.getString("bid");
            baddr = b.getString("baddr");
            bdate = b.getString("bdate");
            btime = b.getString("btime");
            bstat = b.getString("bstat");
            bshop = b.getString("bshop");
            bemp = b.getString("bemp");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Booking Details");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        bookemp_image = (CircleImageView)findViewById(R.id.b_emp);
        bookemp = (TextView)findViewById(R.id.b_emp_name);
        bookaddr = (TextView)findViewById(R.id.book_addr);
        bookdate = (TextView)findViewById(R.id.b_date);
        booktime = (TextView)findViewById(R.id.b_time);
        bookstat = (TextView)findViewById(R.id.b_stat);
        bookshop = (TextView)findViewById(R.id.b_shop);
        bookcont = (TextView)findViewById(R.id.b_emp_cont);
        getEmpImage();
        bookemp.setText(bemp);
        bookshop.setText(bshop);
        bookdate.setText(bdate);
        booktime.setText(btime);
        bookstat.setText("STATUS: "+bstat);
        done = (Button)findViewById(R.id.finish_book);
        if(bstat.equals("REPAIRED")){
            done.setVisibility(View.GONE);
        }else {
            done.setVisibility(View.VISIBLE);
        }
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(BookCompleteActivity.this, R.style.MyAlertDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        finishBooking();
                        AlertDialog.Builder builder = new AlertDialog.Builder(BookCompleteActivity.this, R.style.MyAlertDialog);
                        builder.setMessage("THANK YOU FOR USING OUR SERVICE");
                        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
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
                }, 1000);
            }
        });

    }

    public void finishBooking(){

        String tag_string_req = "req_complete";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_BOOK_STAT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditBookingActivity","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(BookCompleteActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(BookCompleteActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("EditBookingActivity","Booking Error:" +error.getMessage());
                Toast.makeText(BookCompleteActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
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

    public void getEmpImage(){

        String tag_string_req = "req_complete";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GET_EMP_IMG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditBookingActivity","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        bimg = item.getString("emp_img");
                        bcont = item.getString("emp_con");
                        bookcont.setText(bcont);
                        Picasso.get().load(IMAGE_URL + "" + bimg).resize(300, 300).centerCrop().into(bookemp_image);
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(BookCompleteActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(BookCompleteActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("EditBookingActivity","Booking Error:" +error.getMessage());
                Toast.makeText(BookCompleteActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("bemp",bemp);
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
}
