package com.capstone.autofix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements PlaceAdapter.PlaceAdapterListener {

    double total=0.00;
    TextView tv_totals;
    RecyclerView recyclerView;
    private List<PlaceOrder> cartList;
    private PlaceAdapter adapter;
    CheckBox cb;
    EditText et;
    SQLiteHandler db;
    String address,msg;
    Button place_order;
    String stat = "Cash on Pickup";
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Place Order");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        tv_totals = findViewById(R.id.tv_total);
        for (int i = 0; i<CartListAdapter.imageModelArrayList.size(); i++){
            if (CartListAdapter.imageModelArrayList.get(i).getSelected()){
                        total = total + Double.parseDouble(CartListAdapter.imageModelArrayList.get(i).getCartPrice().toString());
                        tv_totals.setText(Double.toString(total)+"0");
                        Log.d("",Double.toString(total));
            }
        }
        db = new SQLiteHandler(OrderActivity.this);
        HashMap<String,String> user = db.getUserDetails();
        address = user.get("address");
        et = findViewById(R.id.addr_order);
        et.setVisibility(View.GONE);
        cb = findViewById(R.id.cb_mop);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()){
                    et.setVisibility(View.VISIBLE);
                    stat = "Cash on Delivery";
                }else {
                    et.setVisibility(View.GONE);
                }
            }
        });
        place_order = (Button)findViewById(R.id.btn_place);
        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (i = 0; i<CartListAdapter.imageModelArrayList.size(); i++){
                    if (CartListAdapter.imageModelArrayList.get(i).getSelected()){
                        placeOrders(i);
                    }
                }
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.recycler_checkout);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartList = new ArrayList<>();
        loadPlaceOrder();
        adapter = new PlaceAdapter(OrderActivity.this, cartList ,this);
        recyclerView.setAdapter(adapter);
        et.setText(address);
    }

    public void loadPlaceOrder(){
        for (int i = 0; i<CartListAdapter.imageModelArrayList.size(); i++){
            if (CartListAdapter.imageModelArrayList.get(i).getSelected()){
                cartList.add(new PlaceOrder(
                        CartListAdapter.imageModelArrayList.get(i).getProdId(),
                        CartListAdapter.imageModelArrayList.get(i).getProdImage(),
                        CartListAdapter.imageModelArrayList.get(i).getProdName(),
                        CartListAdapter.imageModelArrayList.get(i).getProdBrand(),
                        CartListAdapter.imageModelArrayList.get(i).getProdPrice(),
                        CartListAdapter.imageModelArrayList.get(i).getProdQuantity(),
                        CartListAdapter.imageModelArrayList.get(i).getCartID(),
                        CartListAdapter.imageModelArrayList.get(i).getCartQuantity(),
                        CartListAdapter.imageModelArrayList.get(i).getCartPrice(),
                        CartListAdapter.imageModelArrayList.get(i).getCustomerID(),
                        CartListAdapter.imageModelArrayList.get(i).getShopId()
                ));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void placeOrders(int position){
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
                    Toast.makeText(OrderActivity.this,e.getMessage(),Toast.LENGTH_LONG);
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
                params.put("quantity",CartListAdapter.imageModelArrayList.get(position).getCartQuantity());
                params.put("amount",CartListAdapter.imageModelArrayList.get(position).getCartPrice());
                params.put("status",stat);
                params.put("payment","On Going");
                params.put("sid",CartListAdapter.imageModelArrayList.get(position).getShopId());
                params.put("pid",CartListAdapter.imageModelArrayList.get(position).getProdId());
                params.put("cid",CartListAdapter.imageModelArrayList.get(position).getCustomerID());
                params.put("cartid",CartListAdapter.imageModelArrayList.get(position).getCartID());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public void onPlaceSelected(PlaceOrder place) {

    }
}
