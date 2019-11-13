package com.capstone.autofix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewCarActivity extends AppCompatActivity {

    EditText m_brand,m_model,m_year,m_plate;
    Button m_submit,m_cancel;
    String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            cid = b.getString("cid");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pick a Car:");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        m_brand = (EditText)findViewById(R.id.mycar_brand);
        m_model = (EditText)findViewById(R.id.mycar_model);
        m_year = (EditText)findViewById(R.id.mycar_year);
        m_year.setTransformationMethod(null);
        m_plate = (EditText)findViewById(R.id.myplate_number);
        m_submit = (Button)findViewById(R.id.mybook_now);
        m_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(NewCarActivity.this, R.style.MyAlertDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Adding to your cars...");
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        creatRequest();
                        AlertDialog.Builder builder = new AlertDialog.Builder(NewCarActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("MY CARS");
                        builder.setMessage("Your car has been added.");
                        builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }, 3000);
            }
        });
        m_cancel = (Button)findViewById(R.id.mybook_cancel);
        m_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void creatRequest(){
        String tag_string_req = "req_cars";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_MYCAR, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("/////////////////","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String msg = item.getString("msg");
                        Toast.makeText(NewCarActivity.this,msg,Toast.LENGTH_LONG).show();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(NewCarActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(NewCarActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("///////","Adding Car Error:" +error.getMessage());
                Toast.makeText(NewCarActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cid",cid);
                params.put("brand",m_brand.getText().toString());
                params.put("model",m_model.getText().toString());
                params.put("year",m_year.getText().toString());
                params.put("plate_num",m_plate.getText().toString());
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
