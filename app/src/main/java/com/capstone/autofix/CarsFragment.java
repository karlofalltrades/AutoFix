package com.capstone.autofix;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CarsFragment extends Fragment implements CarsAdapter.CarsAdapterListener{
    private RecyclerView recyclerView;
    private List<CarsModel> carsList;
    private CarsAdapter mAdapter;
    int sid;
    String cid,shopname,address;
    private SQLiteHandler db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cars,container,false);
        db = new SQLiteHandler(this.getActivity());
        HashMap<String,String> user = db.getUserDetails();
        cid = user.get("cid");
        recyclerView = (RecyclerView)view.findViewById(R.id.mycars_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity().getBaseContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        carsList = new ArrayList<>();
        getCarsList();
        mAdapter = new CarsAdapter(this.getActivity(), carsList,this);
        recyclerView.setAdapter(mAdapter);

        return view;
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
                Toast.makeText(getActivity(), "ERROR: "+ error.getMessage(),Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.addmenu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(getActivity(), NewCarActivity.class);
        intent.putExtra("cid",cid);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCarsSelected(CarsModel carsModel) {
        Toast.makeText(getActivity(),carsModel.getCarBrand(),Toast.LENGTH_LONG).show();
    }
}
