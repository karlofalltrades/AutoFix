package com.capstone.autofix;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment implements CartAdapter.CartAdapterListener {

    private List<Cart> cartList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;
    private CartAdapter adapter;
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
        View view =  inflater.inflate(R.layout.fragment_cart,container,false);
        db = new SQLiteHandler(this.getActivity());
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");
        recyclerView = (RecyclerView)view.findViewById(R.id.cart_listview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getBaseContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartList = new ArrayList<>();
        loadBookings();
        adapter = new CartAdapter(this.getActivity(), cartList ,this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void loadBookings(){
        String tag_string_req = "req_cartlist";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Cart...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_GET_CART_SHOP, new Response.Listener<String>() {
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
                        cartList.add(new Cart(
                                shop.getString("shopId"),
                                shop.getString("shopImage"),
                                shop.getString("shopName"),
                                shop.getString("shopAddress"),
                                shop.getString("shopContact"),
                                shop.getString("shopEmail")
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
                Log.e("","Registration Error:" +error.getMessage());
                Toast.makeText(getActivity().getBaseContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cartid",cid);
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
    public void onCartSelected(Cart cart) {
        Intent intent = new Intent(this.getActivity(),CartActivity.class);
        intent.putExtra("sid",cart.getShopId());
        intent.putExtra("cid",cid);
        startActivity(intent);
    }
}
