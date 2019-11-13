package com.capstone.autofix;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.capstone.autofix.app.AppConfig;
import com.capstone.autofix.app.AppController;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder>{

    private LayoutInflater inflater;
    public static ArrayList<CartListModel> imageModelArrayList;
    private Context context;
    private boolean isSelected;
    String updateQty;

    public CartListAdapter(Context context, ArrayList<CartListModel> imageModelArrayList){
        inflater = LayoutInflater.from(context);
        this.imageModelArrayList = imageModelArrayList;
        this.context = context;
    }

    @Override
    public CartListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.cv_row_item, parent, false);
        CartListAdapter.MyViewHolder holder = new CartListAdapter.MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final CartListAdapter.MyViewHolder holder, int position){

        if (!isSelected){
            holder.checkBox.setChecked(false);
        }else{
            holder.checkBox.setChecked(true);
        }

        holder.checkBox.setText("CheckBox " +position);
        holder.checkBox.setChecked(imageModelArrayList.get(position).getSelected());
        holder.tvName.setText(imageModelArrayList.get(position).getProdName());
        holder.tvBrand.setText(imageModelArrayList.get(position).getProdBrand());
        holder.tvQty.setText(imageModelArrayList.get(position).getCartQuantity());
        holder.tvPrice.setText(imageModelArrayList.get(position).getProdPrice());
        if (imageModelArrayList.get(position).getProdImage()=="null"){
            Picasso.get().load(AppConfig.MAIN_URL+"uploads/blank.png").resize(300, 300).centerCrop().into(holder.imgItem);
        }else{
            Picasso.get().load(AppConfig.MAIN_URL + imageModelArrayList.get(position).getProdImage()).resize(300, 300).centerCrop().into(holder.imgItem);
        }
        //
        holder.add.setTag(position);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.add.getTag();
                int c = Integer.parseInt(holder.tvQty.getText().toString());
                c++;
                if (c > Integer.parseInt(imageModelArrayList.get(pos).getProdQuantity())){
                    holder.tvQty.setText(imageModelArrayList.get(pos).getProdQuantity());
                    updateQty = holder.tvQty.getText().toString();
                }else{
                    holder.tvQty.setText(Integer.toString(c));
                    updateQty = holder.tvQty.getText().toString();
                }
                updateCart(position);
            }
        });

        holder.min.setTag(position);
        holder.min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer pos = (Integer) holder.min.getTag();
                int c = Integer.parseInt(holder.tvQty.getText().toString());
                c--;
                if (c <= 1){
                    holder.tvQty.setText("1");
                    updateQty = holder.tvQty.getText().toString();
                }else{
                    holder.tvQty.setText(Integer.toString(c));
                    updateQty = holder.tvQty.getText().toString();
                }
                updateCart(position);
            }
        });

        holder.checkBox.setTag(position);
        holder.checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Integer pos = (Integer) holder.checkBox.getTag();
                if(imageModelArrayList.get(pos).getSelected()){
                    imageModelArrayList.get(pos).setSelected(false);
                }else {
                    imageModelArrayList.get(pos).setSelected(true);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return imageModelArrayList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        protected CheckBox checkBox;
        private TextView tvName,tvBrand,tvPrice;
        private EditText tvQty;
        private CircleImageView imgItem;
        private ImageView min,add;

        public MyViewHolder(View itemView){
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_cb);
            imgItem = (CircleImageView) itemView.findViewById(R.id.item_image);
            tvBrand = (TextView) itemView.findViewById(R.id.item_brand);
            tvName = (TextView) itemView.findViewById(R.id.item_name);
            tvQty = (EditText) itemView.findViewById(R.id.item_qty);
            tvQty.setTransformationMethod(null);
            tvPrice = (TextView) itemView.findViewById(R.id.item_price);
            min = (ImageView) itemView.findViewById(R.id.item_min);
            add = (ImageView) itemView.findViewById(R.id.item_plus);
        }
    }

    public void updateCart(int position){

        String tag_string_req = "req_update";
        final double mPrice = Double.parseDouble(imageModelArrayList.get(position).getProdPrice()) * Integer.parseInt(updateQty);

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_CART, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("","Update Response: " +response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e("","Registration Error:" +error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("cartid",imageModelArrayList.get(position).getCartID());
                params.put("cQty",updateQty);
                params.put("cPrice",Double.toString(mPrice));
                params.put("cid",imageModelArrayList.get(position).getCustomerID());
                params.put("pid",imageModelArrayList.get(position).getProdId());
                params.put("sid",imageModelArrayList.get(position).getShopId());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }
}
