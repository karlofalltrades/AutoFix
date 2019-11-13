package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;
import com.capstone.autofix.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView lv;
    LinearLayout layout;
    private SQLiteHandler db;
    private SessionManager session;
    private String cid;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        lv = (ListView) findViewById(R.id.settings_lv);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
        lv.setAdapter(adapter);
        list.add("Deactivate Account");
        list.add("Change Password");

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> users = db.getUserDetails();
        cid = users.get("cid");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this,R.style.MyAlertDialog);
                        password = new EditText(SettingsActivity.this);
                        password.setSingleLine();
                        FrameLayout container = new FrameLayout(SettingsActivity.this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                        password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setLayoutParams(params);
                        container.addView(password);
                        builder.setTitle("DEACTIVATE ACCOUNT");
                        builder.setMessage("Deactivating account will logout current user");
                        builder.setView(container);
                        builder.setPositiveButton("DEACTIVATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this,R.style.MyAlertDialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("Deactivating Account");
                                progressDialog.show();
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deactivateAccount();
                                        progressDialog.dismiss();
                                    }
                                }, 2000);
                            }
                        });
                        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case 1:
                        Intent change = new Intent(SettingsActivity.this,ChangePassActivity.class);
                        startActivity(change);
                        break;
                }
            }
        });
    }
    public void deactivateAccount(){

        final String pass = password.getText().toString().trim();

        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_DEACTIVATE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String msg = item.getString("msg");
                        Toast.makeText(SettingsActivity.this,msg,Toast.LENGTH_LONG).show();
                        logoutUser();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(SettingsActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Registration Error:" +error.getMessage());
                Toast.makeText(SettingsActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("deac","deac");
                params.put("pass",pass);
                params.put("cid",cid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    public void logoutUser(){
        session = new SessionManager(this);
        session.setLogin(false);
        db.deleteUsers();
        Intent intent = new Intent(SettingsActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
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
