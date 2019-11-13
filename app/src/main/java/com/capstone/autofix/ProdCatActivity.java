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

public class ProdCatActivity extends AppCompatActivity implements CategoryAdapter.CategoryAdapterListener{
    private static final String TAG = ProdCatActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<ProductCategory> categoryList;
    private CategoryAdapter mAdapter;
    private SearchView searchView;

    String sid,shopname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_cat);
        Bundle b = this.getIntent().getExtras();

        if(b!=null) {
            sid = b.getString("sid");
            shopname = b.getString("shopname");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose a category:");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        recyclerView = findViewById(R.id.cat_recycler);
        categoryList = new ArrayList<>();
        getCategory();
        mAdapter = new CategoryAdapter(this, categoryList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void getCategory(){

        String tag_string_req = "req_cat_list";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_CAT_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Shops Response: " +response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);

                        categoryList.add(new ProductCategory(
                                shop.getString("prodTypeName"),
                                shop.getString("prodTypeId")
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
                params.put("sid",sid);
                Log.d("////////////","////////////: "+sid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
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
    public void onCategorySelected(ProductCategory productCategory){
//        Toast.makeText(ProdCatActivity.this,productCategory.getName(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,ItemActivity.class);
        intent.putExtra("catname",productCategory.getName());
        intent.putExtra("catid",productCategory.getCatid());
        intent.putExtra("sid",sid);
        startActivity(intent);
    }
}
