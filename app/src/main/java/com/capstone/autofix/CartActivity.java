package com.capstone.autofix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    RecyclerView recycler_itemlist;
    private ArrayList<CartListModel> cartListModels;
    private CartListAdapter adapter;
    private Button plc_order;
    private CheckBox selectall;
    public static TextView tv_total;
    String sid,cid;
    CartListModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            sid = b.getString("sid");
            cid = b.getString("cid");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        recycler_itemlist = (RecyclerView) findViewById(R.id.recycler_cart);
//        tv_total = (TextView) findViewById(R.id.tv_total);
        recycler_itemlist.setHasFixedSize(true);
        recycler_itemlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_itemlist.setItemAnimator(new DefaultItemAnimator());
        plc_order = (Button) findViewById(R.id.btn_placeorder);
        plc_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                double total = 0.00;
//                for (int i = 0; i<CartListAdapter.imageModelArrayList.size(); i++){
//                    if (CartListAdapter.imageModelArrayList.get(i).getSelected()){
//                        total = total + Double.parseDouble(CartListAdapter.imageModelArrayList.get(i).getCartPrice().toString());
//                        tv_total.setText(Double.toString(total));
//                        Log.d("",Double.toString(total));
//                    }
//                }
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
        cartListModels = new ArrayList<>();
        loadCartItems(false);
        adapter = new CartListAdapter(this,cartListModels);
        recycler_itemlist.setAdapter(adapter);

        selectall = (CheckBox) findViewById(R.id.cb_select);
        selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectall.isChecked()){
                    cartListModels.clear();
                    loadCartItems(true);
                }else {
                    cartListModels.clear();
                    loadCartItems(false);
                }
            }
        });
    }

    public void loadCartItems(boolean isSelect){
        String tag_string_req = "req_cartlist";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SUB_CART_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("","Shops Response: " +response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);
                        model = new CartListModel(
                                shop.getString("prodId"),
                                shop.getString("prodImage"),
                                shop.getString("prodName"),
                                shop.getString("prodBrand"),
                                shop.getString("prodPrice"),
                                shop.getString("prodQuantity"),
                                shop.getString("cartID"),
                                shop.getString("cartQuantity"),
                                shop.getString("cartPrice"),
                                shop.getString("CustomerID"),
                                shop.getString("shopId")
                        );
                        model.setSelected(isSelect);
                        cartListModels.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("","Cart Error:" +error.getMessage());
                Toast.makeText(CartActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sid",sid);
                params.put("cid",cid);
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
