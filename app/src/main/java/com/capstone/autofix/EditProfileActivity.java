package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private static int SELECT_PICTURE = 1;
    private EditText fname,phone,addr,email,username;
    private CircleImageView profile,imageView;
    private String cid,image;
    private Bitmap bitmap;
    private Uri uriImage;
    private String IMAGE_URL ="http://192.168.43.93/AutoFix/uploads/";

    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> user = db.getUserDetails();

        fname = (EditText) findViewById(R.id.edit_name);
        addr = (EditText) findViewById(R.id.edit_addr);
        email = (EditText) findViewById(R.id.edit_email);
        username = (EditText) findViewById(R.id.edit_user);
        phone = (EditText) findViewById(R.id.edit_phone);
        phone.setTransformationMethod(null);
        profile = (CircleImageView) findViewById(R.id.edit_pic);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent,SELECT_PICTURE);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),SELECT_PICTURE);
            }
        });

        String p = user.get("photo");
        String pic;
        if (p.equals("null")){
            pic = IMAGE_URL+"blank.png";
        }else {
            pic = IMAGE_URL + p;
        }
        String name = user.get("fullname");
        String address = user.get("address");
        String contact = user.get("contact");
        String user_n = user.get("username");
        String useremail = user.get("email");
        cid = user.get("cid");

        fname.setText(name);
        addr.setText(address);
        email.setText(useremail);
        phone.setText(contact);
        username.setText(user_n);
        Picasso.get().load(pic).resize(300,300).centerCrop().into(profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.save_profile:
                //Toast.makeText(getApplicationContext(),"SAVED",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this,R.style.MyAlertDialog);
                builder.setTitle("SAVE");
                builder.setMessage("Save changes and update your profile?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logoutUser();
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this,R.style.MyAlertDialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Saving Changes\nPlease Wait...");
                        progressDialog.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateProfile();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                profile.setImageBitmap(bitmap);
                image = getStringImage(bitmap);
                Log.d(TAG,"IMAGE STRING"+ image);
            }catch (IOException e){
                e.printStackTrace();
            }
            // Picasso.get().load(uriImage).resize(300, 300).centerCrop().into(profile);
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return encodedImage;
    }

    public void updateProfile(){
        Log.d(TAG, "Update Profile");

        if (!validate()){
            onSignupFailed();
            return;
        }

        final String fullname = fname.getText().toString().trim();
        final String useraddress = addr.getText().toString().trim();
        final String number = phone.getText().toString().trim();
        final String useremail = email.getText().toString().trim();
        final String user = username.getText().toString().trim();

        /////////////////////CODE FOR PROFILE UPDATE HERE///////////////////////////////
        String tag_string_req = "req_update";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Update Response: " +response.toString());
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

                        db.updateDetails(fname,addr,contact,username,email,cid,photo);

                        imageView = (CircleImageView) findViewById(R.id.current_image);
                        Picasso.get().load(IMAGE_URL+""+photo).resize(300,300).centerCrop().into(imageView);
                        Toast.makeText(getApplicationContext(),"Profile successfully updated!",Toast.LENGTH_LONG).show();
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
            }

        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("fullname",fullname);
                params.put("useraddress",useraddress);
                params.put("number",number);
                params.put("useremail",useremail);
                params.put("user",user);
                params.put("cid",cid);
                params.put("image",image);
                params.put("name", getFileName(uriImage));
                Log.d(TAG,"Image Name: "+getFileName(uriImage));


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);

    }

    String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf("/");
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void onSignupFailed(){
        Toast.makeText(getBaseContext(),"Profile failed to update!",Toast.LENGTH_LONG).show();
    }

    public  boolean validate(){
        boolean valid = true;

        String fullname = fname.getText().toString();
        String useraddress = addr.getText().toString();
        String number = phone.getText().toString();
        String useremail = email.getText().toString();
        String user = username.getText().toString();

        if (fullname.isEmpty() || fullname.length() < 3){
            fname.setError("at least 3 characters");
            valid = false;
        } else {
            fname.setError(null);
        }

        if (useraddress.isEmpty() || useraddress.length() < 8){
            addr.setError("at least 8 characters");
            valid = false;
        } else {
            addr.setError(null);
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

        return  valid;
    }
}
