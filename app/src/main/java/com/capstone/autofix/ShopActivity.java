package com.capstone.autofix;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopActivity extends AppCompatActivity {
    private static final String TAG = ShopActivity.class.getSimpleName();

    int sid;
    String shopname;
    private CircleImageView shopimage;
    private TextView name,location,contact,email,rNum;
    private RatingBar ratingBar;
    private Button mekaniko,sparts,book;
    String latlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Bundle b = this.getIntent().getExtras();

        if(b!=null) {
            sid = b.getInt("sid");
            shopname = b.getString("shopname");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(shopname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        shopimage = (CircleImageView) findViewById(R.id.pic_shop);
        name = (TextView)findViewById(R.id.name_shop);
        location = (TextView)findViewById(R.id.location_shop);
        contact = (TextView)findViewById(R.id.contact_shop);
        email = (TextView)findViewById(R.id.email_shop);
        rNum = (TextView)findViewById(R.id.rate_number);
        ratingBar = (RatingBar)findViewById(R.id.rating_shop);
//        mekaniko = (Button)findViewById(R.id.mekaniko);
//        mekaniko.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ShopActivity.this, EmployeeActivity.class);
//                intent.putExtra("sid",sid);
//                intent.putExtra("shopname",shopname);
//                startActivity(intent);
//            }
//        });
        sparts = (Button)findViewById(R.id.parts);
        sparts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this,ProdCatActivity.class);
                intent.putExtra("sid",Integer.toString(sid));
                intent.putExtra("shopname",shopname);
                startActivity(intent);
                Log.d("////////////","////////////"+sid);
            }
        });
        book = (Button)findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this,R.style.MyAlertDialog);
//                builder.setTitle("New Transaction");
//                builder.setMessage("Do you want to enter new car?");
//                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //logoutUser();
//                        dialog.dismiss();
//                        Intent intent = new Intent(ShopActivity.this,BookActivity.class);
//                        intent.putExtra("sid",sid);
//                        intent.putExtra("shopname",shopname);
//                        startActivity(intent);
//                    }
//                });
//                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        Intent intent = new Intent(ShopActivity.this,CarsListActivity.class);
//                        intent.putExtra("sid",sid);
//                        intent.putExtra("shopname",shopname);
//                        startActivity(intent);
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
                Intent intent = new Intent(ShopActivity.this,CarsListActivity.class);
                intent.putExtra("sid",sid);
                intent.putExtra("shopname",shopname);
                startActivity(intent);
            }
        });

        getShop();
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = location.getText().toString();
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,getApplicationContext(),new GeocoderHandler());
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

    public void getShop(){
        Log.d(TAG, "Shop Profile");

        String tag_string_req = "req_shop_profile";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Shop Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String sid = item.getString("shopId");
                        String image = item.getString("shopImage");
                        String sname = item.getString("shopName");
                        String saddr = item.getString("shopAddress");
                        String scont = item.getString("shopContact");
                        String semail = item.getString("shopEmail");
                        if (image.equals("null")){
                            Picasso.get().load(AppConfig.MAIN_URL + "uploads/blank.png").resize(300, 300).centerCrop().into(shopimage);
                        }else {
                            Picasso.get().load(AppConfig.MAIN_URL + image).resize(300, 300).centerCrop().into(shopimage);
                        }
                        name.setText(sname);
                        location.setText(saddr);
                        contact.setText(scont);
                        email.setText(semail);
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Registration Error:" +error.getMessage());
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sid",Integer.toString(sid));
                Log.d("////////////","////////////"+sid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    private class GeocoderHandler extends Handler{
        @Override
        public void handleMessage(Message message){
            double latitude = 0.00;
            double longitude = 0.00;
            switch (message.what){
                case 1:
                    Bundle bundle = message.getData();
                    latitude = bundle.getDouble("latitude");
                    longitude = bundle.getDouble("longitude");
                    break;
            default:
                latitude = 0.00;
                longitude = 0.00;
            }
            Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
            intent.putExtra("latitude",latitude);
            intent.putExtra("longitude",longitude);
            intent.putExtra("shopname",shopname);
            Log.d("////////////","SHOP LOCATION"+latitude+":"+longitude);
            startActivity(intent);
        }
    }
}
