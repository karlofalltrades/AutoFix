package com.capstone.autofix;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookingFragment extends Fragment implements BookingAdapter.BookingAdapterListener {

    private List<Booking> bookList = new ArrayList<>();
    RecyclerView recyclerView;
    SearchView searchView;
    private BookingAdapter adapter;
    private SQLiteHandler db;
    String cid;
    private int mCurrentItemPosition;

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
        bookList = new ArrayList<>();
        loadBookings();
        adapter = new BookingAdapter(this.getActivity(), bookList ,this);
        recyclerView.setAdapter(adapter);
        this.registerForContextMenu(recyclerView);
        adapter.setOnLongItemClickListener(new BookingAdapter.onLongItemClickListener() {
            @Override
            public void ItemLongClicked(View v, int position) {
                mCurrentItemPosition = position;
                v.showContextMenu();
            }
        });
        return view;
    }

    public void loadBookings(){
        String tag_string_req = "req_booklist";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Booking Requests...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_BOOK_LIST, new Response.Listener<String>() {
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
                        bookList.add(new Booking(
                                shop.getString("bookingID"),
                                shop.getString("bookingAddress"),
                                shop.getString("bookingDate"),
                                shop.getString("bookingTime"),
                                shop.getString("bookingStatus"),
                                shop.getString("fullname"),
                                shop.getString("shopName"),
                                shop.getString("emp_name")
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
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.update:

                Intent intent = new Intent(this.getActivity(),EditBookingActivity.class);
                intent.putExtra("bookingID",bookList.get(mCurrentItemPosition).getBookingID());
                intent.putExtra("cid",cid);
                startActivity(intent);
                break;
            case R.id.cancel_req:
                final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.MyAlertDialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating Your Schedule...");
                progressDialog.show();
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        String bid = bookList.get(mCurrentItemPosition).getBookingID();
                        cancelRequest(bid);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialog);
                        builder.setTitle("SCHEDULE UPDATE");
                        builder.setMessage("Your have cancelled your request");
                        builder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //logoutUser();
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }, 3000);
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void cancelRequest(String bid){

        String tag_string_req = "req_book";

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_CANCEL_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("EditBookingActivity","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error){
                        JSONObject item = jObj.getJSONObject("user");
                        String msg = item.getString("msg");
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
                    }else{
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity(),errorMsg,Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Json error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("EditBookingActivity","Booking Error:" +error.getMessage());
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("bid",bid);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.contextmenu, menu);
        menu.setHeaderTitle("PICK ACTION");
    }

    @Override
    public void onBookSelected(Booking book) {
        Intent intent = new Intent(this.getActivity(),BookCompleteActivity.class);
        intent.putExtra("bid",book.getBookingID());
        intent.putExtra("baddr",book.getBookingAddress());
        intent.putExtra("bdate",book.getBookingDate());
        intent.putExtra("btime",book.getBookingTime());
        intent.putExtra("bstat",book.getBookingStatus());
        intent.putExtra("bshop",book.getShopId());
        intent.putExtra("bemp",book.getEmpName());
        startActivity(intent);
    }
}
