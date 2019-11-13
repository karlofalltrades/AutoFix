package com.capstone.autofix;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class ItemActivity extends AppCompatActivity implements ItemAdapter.ItemAdapterListener{

    private static final String TAG = ItemActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item> itemList;
    private ItemAdapter mAdapter;
    private SearchView searchView;
    String catid,catname,sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Bundle b = this.getIntent().getExtras();

        if(b!=null) {
            sid = b.getString("sid");
            catid = b.getString("catid");
            catname = b.getString("catname");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(catname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        recyclerView = findViewById(R.id.item_recycler);
        itemList = new ArrayList<>();
        getItem();
        mAdapter = new ItemAdapter(this, itemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void getItem(){

        String tag_string_req = "req_emp_list";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ITEM_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Shops Response: " +response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);

                        itemList.add(new Item(
                                shop.getString("prodId"),
                                shop.getString("prodImage"),
                                shop.getString("prodName"),
                                shop.getString("prodBrand"),
                                shop.getString("prodTypeId"),
                                shop.getString("prodPrice"),
                                shop.getString("prodQuantity"),
                                shop.getString("prodStatus"),
                                shop.getString("shopId")
                        ));
                    }
                    mAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "ERROR: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("catid",catid);
                params.put("sid",sid);
                Log.d("////////////","////////////: "+catid+":"+sid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_search: return true;
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(Item item){
        Intent intent = new Intent(ItemActivity.this,ViewItemActivity.class);
        intent.putExtra("prodId",item.getProdId());
        intent.putExtra("prodImage",item.getProdImage());
        intent.putExtra("prodName",item.getProdName());
        intent.putExtra("prodBrand",item.getProdBrand());
        intent.putExtra("prodTypeId",item.getProdTypeId());
        intent.putExtra("prodPrice",item.getProdPrice());
        intent.putExtra("prodQuantity",item.getProdQuantity());
        intent.putExtra("prodStatus",item.getProdStatus());
        intent.putExtra("shopId",item.getShopId());
        intent.putExtra("catid",catid);
        intent.putExtra("catname", catname);
        startActivity(intent);
    }
}
