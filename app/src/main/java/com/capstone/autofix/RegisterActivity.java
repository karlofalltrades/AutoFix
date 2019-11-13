package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;
import com.capstone.autofix.helper.SessionManager;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private TextView login;
    private EditText fname,address,phone,email,username,pass;
    private Button signup;

    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        fname = (EditText) findViewById(R.id.fname);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.contact);
        phone.setTransformationMethod(null);

        signup = (Button) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        login = (TextView) findViewById(R.id.link_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    public void registerUser(){
        Log.d(TAG, "Signup");

        if (!validate()){
            onSignupFailed();
            return;
        }

        signup.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account");
        progressDialog.show();

        final String fullname = fname.getText().toString().trim();
        final String useraddress = address.getText().toString().trim();
        final String number = phone.getText().toString().trim();
        final String useremail = email.getText().toString().trim();
        final String user = username.getText().toString().trim();
        final String password = pass.getText().toString().trim();

        /////////////////////CODE FOR REGISTRATION HERE///////////////////////////////
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Register Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String cid = item.getString("cid");
                        String fname = item.getString("fname");
                        String addr = item.getString("address");
                        String contact = item.getString("contact");
                        String email = item.getString("email");
                        String username = item.getString("username");
                        String photo = item.getString("photo");

                        db.addUser(fname,addr,contact,username,email,cid,photo);

                        Toast.makeText(getApplicationContext(),"User successfully registered. Try login now!",Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Registration Error:" +error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

        }){
            @Override
            protected Map<String,String>getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("fullname",fullname);
                params.put("useraddress",useraddress);
                params.put("number",number);
                params.put("useremail",useremail);
                params.put("user",user);
                params.put("password",password);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onSignupSuccess();
                //onSignupFailed();
                progressDialog.dismiss();
            }
        }, 3000);
    }

    public void onSignupSuccess(){
        signup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(){
        Toast.makeText(getBaseContext(),"Signup Failed",Toast.LENGTH_LONG).show();
        signup.setEnabled(true);
    }

    public  boolean validate(){
        boolean valid = true;

        String fullname = fname.getText().toString();
        String useraddress = address.getText().toString();
        String number = phone.getText().toString();
        String useremail = email.getText().toString();
        String user = username.getText().toString();
        String password = pass.getText().toString();

        if (fullname.isEmpty() || fullname.length() < 3){
            fname.setError("at least 3 characters");
            valid = false;
        } else {
            fname.setError(null);
        }

        if (useraddress.isEmpty() || useraddress.length() < 8){
            address.setError("at least 8 characters");
            valid = false;
        } else {
            address.setError(null);
        }

        if (number.isEmpty() || number.length() < 11 || number.length() >11){
            phone.setError("Must be 11 characters!");
            valid = false;
        } else {
            phone.setError(null);
        }

        if (useremail.isEmpty() || useremail.length() < 3 || !Patterns.EMAIL_ADDRESS.matcher(useremail).matches()){
            email.setError("Enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (user.isEmpty() || user.length() < 6){
            username.setError("at least 6 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 20){
            pass.setError("Must be between 6 to 20 characters");
            valid = false;
        } else {
            pass.setError(null);
        }

        return  valid;
    }
}
