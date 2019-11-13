package com.capstone.autofix;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyShopViewHolder> implements Filterable {

    private Context context;
    private List<MyShop> myShopList;
    public static List<MyShop> myShopListFiltered;
    private OnItemClickListener mItemClickListener;
    String URL="http://192.168.43.93/";

    public class MyShopViewHolder extends RecyclerView.ViewHolder{
        TextView shopname,shopAddress,shopContact,shopEmail,shopDist;
        CircleImageView shop_image;

        public MyShopViewHolder(View itemview){
            super(itemview);
            shopname = itemview.findViewById(R.id.shop_name);
            shopAddress = itemview.findViewById(R.id.shop_addr);
            shop_image = itemview.findViewById(R.id.shop_image);
            shopContact = itemview.findViewById(R.id.shop_contact);
            shopEmail = itemview.findViewById(R.id.shop_email);
            shopDist = itemview.findViewById(R.id.shop_dist);
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(myShopListFiltered.get(getAdapterPosition()));
                }
            });
        }

    }

    public ShopAdapter(Context context, List<MyShop> myShopList, OnItemClickListener mItemClickListener){
        this.context = context;
        this.myShopList = myShopList;
        this.myShopListFiltered = myShopList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public MyShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list,parent,false);
        return new MyShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyShopViewHolder holder, int position){
        final MyShop myShop = myShopListFiltered.get(position);
        holder.shopname.setText(myShop.getShopname());
        holder.shopAddress.setText(myShop.getShopAddress());
        holder.shopContact.setText(myShop.getShopContact());
        holder.shopEmail.setText(myShop.getShopEmail());
        holder.shopDist.setText(myShop.getDistance()+" km");
        if (myShop.getImage()=="null") {
            Picasso.get().load(URL+"AutoFix/uploads/blank.png").resize(300, 300).centerCrop().into(holder.shop_image);
        }else{
            Picasso.get().load(URL+"AutoFix/" + myShop.getImage()).resize(300, 300).centerCrop().into(holder.shop_image);
        }
    }

    @Override
    public int getItemCount(){
        return myShopListFiltered.size();
    }

    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence charSequence){
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    myShopListFiltered = myShopList;
                }else {
                    List<MyShop> filteredList = new ArrayList<>();
                    for (MyShop row : myShopList){
                        if (row.getShopname().toLowerCase().contains(charString.toLowerCase()) || row.getShopAddress().toLowerCase().contains(charString.toLowerCase())){
                            filteredList.add(row);
                        }
                    }
                    myShopListFiltered = myShopList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = myShopListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                myShopListFiltered = (ArrayList<MyShop>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener{
        void onItemClick(MyShop myShop);
    }
}
