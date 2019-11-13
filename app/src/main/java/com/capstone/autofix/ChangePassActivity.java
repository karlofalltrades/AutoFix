package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText old,change,confirm;
    private CoordinatorLayout coordinateLayout;
    private SQLiteHandler db;
    private String cid;
    String msg="";
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        old = (EditText) findViewById(R.id.old_pass);
        change = (EditText) findViewById(R.id.new_pass);
        confirm = (EditText) findViewById(R.id.conf_pass);
        coordinateLayout = (CoordinatorLayout) findViewById(R.id.coordinated_layout);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> users = db.getUserDetails();
        cid = users.get("cid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_profile:
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassActivity.this,R.style.MyAlertDialog);
                builder.setTitle("SAVE");
                builder.setMessage("Save changes?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logoutUser();
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(ChangePassActivity.this,R.style.MyAlertDialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Saving Changes\nPlease Wait...");
                        progressDialog.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updatePassword();
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
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void updatePassword(){

        if (!validate()){
            msg = "CHANGE PASS FAILED";
            snackbar = Snackbar.make(coordinateLayout,msg,Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        final String old_p = old.getText().toString().trim();
        final String new_p = change.getText().toString().trim();

        String tag_string_req = "req_chanepass";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_CHANGE_PASS,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.d(TAG,"Response: "+response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        msg = item.getString("msg");
                        snackbar = Snackbar.make(coordinateLayout,msg,Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        msg = jObj.getString("error_msg");
                        snackbar = Snackbar.make(coordinateLayout,msg,Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
//                    msg = e.getMessage();
//                    snackbar = Snackbar.make(coordinateLayout,msg,Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    Toast.makeText(ChangePassActivity.this,e.getMessage(),Toast.LENGTH_LONG);
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Change Pass Error:" +error.getMessage());
                msg = error.getMessage();
                snackbar = Snackbar.make(coordinateLayout,msg,Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("newpass","newpass");
                params.put("old_p",old_p);
                params.put("new_p",new_p);
                params.put("cid",cid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    public boolean validate(){
        boolean valid = true;

        String old_p = old.getText().toString().trim();
        String new_p = change.getText().toString().trim();
        String conf_p = confirm.getText().toString().trim();

        if (old_p.isEmpty() || old_p.length() < 6 || old_p.length() > 20){
            old.setError("Must be between 6 to 20 characters");
            valid = false;
        }else{
            old.setError(null);
        }

        if (new_p.isEmpty() || new_p.length() < 6 || new_p.length() > 20){
            change.setError("Must be between 6 to 20 characters");
            valid = false;
        }else{
            change.setError(null);
        }

        if (conf_p.isEmpty() || conf_p.length() < 6 || conf_p.length() > 20){
            confirm.setError("Must be between 6 to 20 characters");
            valid = false;
        }else{
            confirm.setError(null);
        }

        if (!conf_p.equals(new_p)){
            confirm.setError("Incorrect password!");
            change.setError("Incorrect password!");
            valid = false;
        }else{
            old.setError(null);
        }
        return valid;
    }
}
