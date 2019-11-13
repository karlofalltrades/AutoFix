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
import com.capstone.autofix.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarsListActivity extends AppCompatActivity implements CarsAdapter.CarsAdapterListener{
    private RecyclerView recyclerView;
    private List<CarsModel> carsList;
    private CarsAdapter mAdapter;
    private SearchView searchView;
    int sid;
    String cid,shopname,address;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            sid = b.getInt("sid");
            shopname = b.getString("shopname");
        }
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pick a Car:");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        setContentView(R.layout.activity_cars_list);
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");
        address = user.get("address");
        recyclerView = findViewById(R.id.cars_recycler);
        carsList = new ArrayList<>();
        getCarsList();
        mAdapter = new CarsAdapter(this, carsList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void getCarsList(){

        String tag_string_req = "req_cat_list";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_CARS_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("","Shops Response: " +response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);

                        carsList.add(new CarsModel(
                                shop.getString("car_id"),
                                shop.getString("carBrand"),
                                shop.getString("carModel"),
                                shop.getString("carYear"),
                                shop.getString("carPlate_number"),
                                shop.getString("CustomerID")
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
                Log.e("///////////////", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "ERROR: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cid",cid);
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
    public void onCarsSelected(CarsModel carsModel){
//        Toast.makeText(ProdCatActivity.this,productCategory.getName(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,BookOldActivity.class);
        intent.putExtra("brand",carsModel.getCarBrand());
        intent.putExtra("model",carsModel.getCarModel());
        intent.putExtra("year",carsModel.getCarYear());
        intent.putExtra("plate_number",carsModel.getCarPlate_number());
        intent.putExtra("cid",cid);
        intent.putExtra("address",address);
        intent.putExtra("sid",sid);
        intent.putExtra("shopname",shopname);
        startActivity(intent);
    }
}
