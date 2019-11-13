package com.capstone.autofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.capstone.autofix.helper.SQLiteHandler;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewItemActivity extends AppCompatActivity {

    String catid, catname,cid,prodId,prodImage,prodName,prodBrand,prodTypeId,prodPrice,prodQuantity;
    String prodDesc,prodStatus,shopId,msg;
    ImageView plus,minus,it_pic;
    TextView it_name,it_brand,it_qty,it_price;
    EditText user_qty;
    Button cart,order;
    String URL="http://192.168.43.93/";

    SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            catid = b.getString("catid");
            catname = b.getString("catname");
            prodId = b.getString("prodId");
            prodImage = b.getString("prodImage");
            prodName = b.getString("prodName");
            prodBrand = b.getString("prodBrand");
            prodTypeId = b.getString("prodTypeId");
            prodPrice = b.getString("prodPrice");
            prodQuantity = b.getString("prodQuantity");
            prodStatus = b.getString("prodStatus");
            shopId = b.getString("shopId");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(catname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");

        plus = (ImageView)findViewById(R.id.plus_qty);
        minus = (ImageView)findViewById(R.id.subt);
        it_pic = (ImageView)findViewById(R.id.item_viewpic);
        it_name = (TextView)findViewById(R.id.i_name);
        it_brand = (TextView)findViewById(R.id.i_brand);
        it_qty = (TextView)findViewById(R.id.i_qty);
        it_price = (TextView)findViewById(R.id.i_price);
        user_qty = (EditText) findViewById(R.id.edit_qty);
        user_qty.setTransformationMethod(null);
        cart = (Button) findViewById(R.id.add_cart);
        order = (Button) findViewById(R.id.order_now);

        if (prodImage.equals("null")){
            Picasso.get().load(URL+"AutoFix/uploads/blank.png").resize(300,300).centerCrop().into(it_pic);
        }else{
            Picasso.get().load(URL+"AutoFix/"+prodImage).resize(300,300).centerCrop().into(it_pic);
        }
        it_name.setText(prodName);
        it_brand.setText(prodBrand);
        it_qty.setText(prodQuantity);
        it_price.setText(prodPrice);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(user_qty.getText().toString());
                c++;
                if (c > Integer.parseInt(prodQuantity)){
                    user_qty.setText(prodQuantity);
                }else{
                    user_qty.setText(Integer.toString(c));
                }
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = Integer.parseInt(user_qty.getText().toString());
                c--;
                if (c <= 1){
                    user_qty.setText("1");
                }else{
                    user_qty.setText(Integer.toString(c));
                }
            }
        });
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ViewItemActivity.this, R.style.MyAlertDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Adding Item to your Cart");
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        addCart();
                        finish();
                    }
                }, 1000);
            }
        });
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(ViewItemActivity.this, R.style.MyAlertDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Placing your order...\nPlease wait");
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        placeOrder();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewItemActivity.this, R.style.MyAlertDialog);
                        builder.setTitle("PLACE ORDER");
                        builder.setMessage("Your order has been placed");
                        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
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
        });
    }

    public void placeOrder(){

        final String mQty = user_qty.getText().toString().trim();
        final double mPrice = Double.parseDouble(prodPrice) * Integer.parseInt(mQty);

        String tag_string_req = "req_addorder";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ORDER_ITEM,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.d("","Response: "+response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        msg = item.getString("msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    }else{
                        msg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(ViewItemActivity.this,e.getMessage(),Toast.LENGTH_LONG);
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("","Error on placing order:" +error.getMessage());
                msg = error.getMessage();
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("quantity",mQty);
                params.put("amount",Double.toString(mPrice));
                params.put("status",Integer.toString(1));
                params.put("payment",Integer.toString(1));
                params.put("sid",shopId);
                params.put("pid",prodId);
                params.put("cid",cid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    public void addCart(){

        final String mQty = user_qty.getText().toString().trim();
        final double mPrice = Double.parseDouble(prodPrice) * Integer.parseInt(mQty);

        String tag_string_req = "req_chanepass";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ADD_CART,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.d("","Response: "+response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        msg = item.getString("msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    }else{
                        msg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(ViewItemActivity.this,e.getMessage(),Toast.LENGTH_LONG);
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("","Error adding to cart:" +error.getMessage());
                msg = error.getMessage();
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }

        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("quantity",mQty);
                params.put("amount",Double.toString(mPrice));
                params.put("sid",shopId);
                params.put("pid",prodId);
                params.put("cid",cid);
                params.put("catid",catid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
