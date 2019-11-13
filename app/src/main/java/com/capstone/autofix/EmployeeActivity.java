package com.capstone.autofix;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class EmployeeActivity extends AppCompatActivity  implements ContactsAdapter.ContactsAdapterListener{
    private static final String TAG = EmployeeActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Employee> employeeList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;

    int sid;
    String shopname,coid,stat,bookt,bookd,caddress,brand,model,year,plate_num,desc,edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        Bundle b = this.getIntent().getExtras();
        if(b!=null) {
            sid = b.getInt("sid");
            shopname = b.getString("shopname");
            coid = b.getString("cid");
            stat = b.getString("stat");
            bookt = b.getString("booktime");
            bookd = b.getString("bookdate");
            caddress = b.getString("address");
            brand = b.getString("brand");
            model = b.getString("model");
            year = b.getString("year");
            plate_num = b.getString("plate_num");
            desc = b.getString("desc");
            edit = b.getString("edit");
        }
        Log.d("",""+bookt+":"+bookd);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(shopname);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        recyclerView = findViewById(R.id.recycler_view);
        employeeList = new ArrayList<>();
        mAdapter = new ContactsAdapter(this, employeeList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchEmployee();
    }

    private void fetchEmployee(){

        String tag_string_req = "req_emp_list";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_EMP_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Shops Response: " +response.toString());
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);

                        employeeList.add(new Employee(
                                shop.getString("emp_id"),
                                shop.getString("emp_name"),
                                shop.getString("emp_con"),
                                shop.getString("emp_img")
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
                params.put("sid",Integer.toString(sid));
                Log.d("////////////","////////////: "+sid);
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
    public void onContactSelected(Employee employee){
        Toast.makeText(EmployeeActivity.this,employee.getName(),Toast.LENGTH_SHORT).show();
        String eid = employee.getEmpID();
        final ProgressDialog progressDialog = new ProgressDialog(EmployeeActivity.this, R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Booking Your Schedule...");
        progressDialog.show();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            creatRequest(eid);
                            AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeActivity.this, R.style.MyAlertDialog);
                            builder.setTitle("BOOKING REQUEST");
                            builder.setMessage("You will be notified when your booking request is accepted by the shop.");
                            builder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BookingFragment()).commit();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }, 3000);
    }

    public void creatRequest(String eid){
        String tag_string_req = "req_book";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_BOOK_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Update Response: " +response.toString());
                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//                    if (!error){
//                        JSONObject item = jObj.getJSONObject("user");
//                        String msg = item.getString("msg");
//                        Toast.makeText(SettingsActivity.this,msg,Toast.LENGTH_LONG).show();
//                        logoutUser();
//                    }else{
//                        String errorMsg = jObj.getString("error_msg");
//                        Toast.makeText(SettingsActivity.this,errorMsg,Toast.LENGTH_LONG).show();
//                    }
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject book = array.getJSONObject(i);
                        String bid = book.getString("bookingID");
                        String bAddress = book.getString("bookingAddress");
                        String bDate = book.getString("bookingDate");
                        String bTime = book.getString("bookingTime");
                        String bStat = book.getString("bookingStatus");
                        String bCustID = book.getString("CustomerID");
                        String bshopId = book.getString("shopId");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(EmployeeActivity.this, "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,"Booking Error:" +error.getMessage());
                Toast.makeText(EmployeeActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("sid",Integer.toString(sid));
                params.put("cid",coid);
                params.put("stat","PENDING");
                params.put("booktime",bookt);
                params.put("bookdate",bookd);
                params.put("address",caddress);
                params.put("brand",brand);
                params.put("model",model);
                params.put("year",year);
                params.put("plate_num",plate_num);
                params.put("desc",desc);
                params.put("eid",eid);
                params.put("edit",edit);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }
}
