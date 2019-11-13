package com.capstone.autofix;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderFragment extends Fragment implements OrderAdapter.OrderAdapterListener {

    private List<OrderItems> orderItemsList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;
    private OrderAdapter adapter;
    private SQLiteHandler db;
    String cid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_booking,container,false);
        db = new SQLiteHandler(this.getActivity());
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");
        recyclerView = (RecyclerView)view.findViewById(R.id.booking_listview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getBaseContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderItemsList = new ArrayList<>();
        loadOrders();
        adapter = new OrderAdapter(this.getActivity(), orderItemsList ,this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void loadOrders(){
        String tag_string_req = "req_booklist";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Ordered Items...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ORDER_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("","Shops Response: " +response.toString());
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);
                        orderItemsList.add(new OrderItems(
                                shop.getString("OrderID"),
                                shop.getString("OrderQuantity"),
                                shop.getString("OrderAmount"),
                                shop.getString("OrderStatus"),
                                shop.getString("OrderDate"),
                                shop.getString("PaymentStatus"),
                                shop.getString("shopName"),
                                shop.getString("prodName"),
                                shop.getString("fullname"),
                                shop.getString("prodImage")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        searchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();
        if (id == R.id.action_search){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOrderSelected(OrderItems order) {

    }
}
