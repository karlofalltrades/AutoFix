package com.capstone.autofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.capstone.autofix.app.AppConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> implements Filterable {
private Context context;
private List<PlaceOrder> orderList;
private List<PlaceOrder> orderListFiltered;
private PlaceAdapter.PlaceAdapterListener listener;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView p_qty,p_price,p_name,p_brand;
    public CircleImageView p_image;

    public MyViewHolder(View view) {
        super(view);
        p_image = view.findViewById(R.id.place_image);
        p_brand = view.findViewById(R.id.place_brand);
        p_name = view.findViewById(R.id.place_name);
        p_qty = view.findViewById(R.id.place_qty);
        p_price = view.findViewById(R.id.place_price);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected order in callback
                listener.onPlaceSelected(orderListFiltered.get(getAdapterPosition()));
            }
        });
    }
}

    public PlaceAdapter(Context context, List<PlaceOrder> orderList, PlaceAdapter.PlaceAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.orderList = orderList;
        this.orderListFiltered = orderList;
    }

    @Override
    public PlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_row_item, parent, false);

        return new PlaceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlaceAdapter.MyViewHolder holder, final int position) {
        final PlaceOrder order = orderListFiltered.get(position);
        holder.p_name.setText(order.getProdName());
        holder.p_brand.setText(order.getProdBrand());
        holder.p_qty.setText(order.getCartQuantity());
        holder.p_price.setText(order.getCartPrice());
        if (order.getProdImage()=="null") {
            Picasso.get().load(AppConfig.MAIN_URL +"uploads/blank.png").resize(300, 300).centerCrop().into(holder.p_image);
        }else{
            Picasso.get().load(AppConfig.MAIN_URL+"uploads/" + order.getProdImage()).resize(300, 300).centerCrop().into(holder.p_image);
        }

    }

    @Override
    public int getItemCount() {
        return orderListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    orderListFiltered = orderList;
                } else {
                    List<PlaceOrder> filteredList = new ArrayList<>();
                    for (PlaceOrder row : orderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getShopId().toLowerCase().contains(charString.toLowerCase()) || row.getProdId().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    orderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = orderListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                orderListFiltered = (ArrayList<PlaceOrder>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

public interface PlaceAdapterListener {
    void onPlaceSelected(PlaceOrder place);
}
}
