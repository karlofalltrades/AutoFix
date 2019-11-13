package com.capstone.autofix;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements ShopAdapter.OnItemClickListener{
    private static final String TAG = "HomeFragment";

    private List<MyShop> shopList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;
    private ShopAdapter adapter;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static double shop_lat = 0.0;
    public static double shop_long = 0.0;
    public static float distance = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.shop_listview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getBaseContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        shopList = new ArrayList<>();
        loadShops();
        adapter = new ShopAdapter(this.getActivity(), shopList ,this);
        recyclerView.setAdapter(adapter);

        return view;
    }


    public void getUserLocation(){
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("",""+Double.toString(latitude)+":"+Double.toString(longitude));
            }else{
                latitude = 10.30264;
                longitude = 123.895302;
            }
        }
    }

    private void loadShops(){

        String tag_string_req = "req_shopslist";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Searching for shops...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_SHOP_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Shops Response: " +response.toString());
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray array = json.getJSONArray("mymessage");
                    for (int i = 0; i<array.length(); i++){
                        JSONObject shop = array.getJSONObject(i);
                        int shopId = shop.getInt("shopId");
                        String shopName = shop.getString("shopName");
                        String shopAddress = shop.getString("shopAddress");
                        String shopImage = shop.getString("shopImage");
                        String shopContact = shop.getString("shopContact");
                        String shopEmail = shop.getString("shopEmail");
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                                try {
                                    List<Address> addressList = geocoder.getFromLocationName(shopAddress,10);
                                    if (addressList != null && addressList.size() > 0) {
                                        Address mAddress = addressList.get(0);
                                        shop_lat = mAddress.getLatitude();
                                        shop_long = mAddress.getLongitude();
                                    }
                                }catch (IOException e){
                                    Log.e("", "Unable to connect to Geocoder", e);
                                }
                        Location crntLocation = new Location("crntlocation");
                        crntLocation.setLatitude(latitude);
                        crntLocation.setLongitude(longitude);
                        Location newLocation = new Location("newlocation");
                        newLocation.setLatitude(shop_lat);
                        newLocation.setLongitude(shop_long);
                        distance = crntLocation.distanceTo(newLocation)/1000;
                        DecimalFormat df = new DecimalFormat("#.00");
                        String format = df.format(distance);
                        double dist = Double.parseDouble(format);
                        shopList.add(new MyShop(shopId, shopName, shopAddress, dist, shopImage, shopContact, shopEmail));
                    }
                    Collections.sort(shopList, new Comparator<MyShop>() {
                        @Override
                        public int compare(MyShop o1, MyShop o2) {
                            Double a = o1.getDistance();
                            Double b = o2.getDistance();
                            Log.d("","DISTANSYA!!!!"+o1.getDistance()+":::"+o2.getDistance());
                            int i = a.intValue();
                            int j = b.intValue();
                            if (i>j){
                                return 1;
                            }else {
                                return -1;
                            }
                        }
                    });
                    adapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getUserLocation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Registration Error:" +error.getMessage());
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
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
    public void onItemClick(MyShop myShop){
        Intent intent = new Intent(this.getActivity(),ShopActivity.class);
        intent.putExtra("sid",myShop.getSid());
        intent.putExtra("shopname",myShop.getShopname());
        startActivity(intent);
    }

}
