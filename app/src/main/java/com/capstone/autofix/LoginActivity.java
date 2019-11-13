package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private SessionManager session;
    private SQLiteHandler db;

    private EditText txtUser, txtPass;
    private Button btnLogin;
    private TextView register,forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

        txtUser = (EditText) findViewById(R.id.user);
        txtPass = (EditText) findViewById(R.id.pass);
        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);
//                finish();
                return;
            }
        });
        btnLogin = (Button) findViewById(R.id.login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
//                finish();
                return;
            }
        });
    }

    public void login(){
        Log.d(TAG, "Login");
        if (!validate()){
            onLoginFailed();
            return;
        }

        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String user = txtUser.getText().toString().trim();
        final String pass = txtPass.getText().toString().trim();
        String tag_string_req = "req_login";

        //CODE FOR LOGIN AUTHENTICATION
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Login Response: "+ response.toString());
                if (progressDialog.isShowing()){ progressDialog.dismiss(); }
                try{
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error){
                        session.setLogin(true);

                        JSONObject item = jObj.getJSONObject("user");
                        String cid = item.getString("cid");
                        String fname = item.getString("fname");
                        String addr = item.getString("address");
                        String contact = item.getString("contact");
                        String email = item.getString("email");
                        String username = item.getString("username");
                        String photo = item.getString("photo");

                        db.addUser(fname,addr,contact,username,email,cid,photo);

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Login Error: "+error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String>getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("user",user);
                params.put("pass",pass);

                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
        //TO BE CONTINUED
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_SIGNUP){
            if (requestCode == RESULT_OK){
                //SUCCESSFUL REGISTER CODE HERE
                //FINISHED ACTIVITY AND LOG IN AUTOMATICALLY
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }

    public boolean validate(){

        boolean valid = true;

        String user = txtUser.getText().toString();
        String pass = txtPass.getText().toString();

        if (user.isEmpty()){
            txtUser.setError("Please enter a username");
            valid = false;
        }else{
            txtUser.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 4 || pass.length() > 15){
            txtPass.setError("Between 4 and 15 alphanumeric characters");
            valid = false;
        }else{
            txtPass.setError(null);
        }
        return valid;
    }
}
