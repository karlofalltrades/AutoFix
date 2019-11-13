package com.capstone.autofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<OrderItems> orderList;
    private List<OrderItems> orderListFiltered;
    private OrderAdapter.OrderAdapterListener listener;
    String URL="http://192.168.43.93/";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView o_id, o_qty,o_amt,o_stat,o_date,p_stat,o_shop,o_prod;
        public CircleImageView o_image;

        public MyViewHolder(View view) {
            super(view);
            o_qty = view.findViewById(R.id.order_qty);
            o_amt = view.findViewById(R.id.order_amt);
            o_date = view.findViewById(R.id.order_date);
            o_shop = view.findViewById(R.id.order_shop);
            o_prod = view.findViewById(R.id.order_prod);
            o_image = view.findViewById(R.id.order_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected order in callback
                    listener.onOrderSelected(orderListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public OrderAdapter(Context context, List<OrderItems> orderList, OrderAdapter.OrderAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.orderList = orderList;
        this.orderListFiltered = orderList;
    }

    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row_item, parent, false);

        return new OrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrderAdapter.MyViewHolder holder, final int position) {
        final OrderItems order = orderListFiltered.get(position);
        holder.o_qty.setText("Quantity: "+order.getOrderQuantity());
        holder.o_amt.setText("Price: "+order.getOrderAmount());
        holder.o_date.setText("Date Ordered: "+order.getOrderDate());
        holder.o_shop.setText("Shop Name: "+order.getShopId());
        holder.o_prod.setText("Product Name: "+order.getProdId());
        if (order.getProdImage()=="null") {
            Picasso.get().load(URL+"AutoFix/uploads/blank.png").resize(300, 300).centerCrop().into(holder.o_image);
        }else{
            Picasso.get().load(URL+"AutoFix/" + order.getProdImage()).resize(300, 300).centerCrop().into(holder.o_image);
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
                    List<OrderItems> filteredList = new ArrayList<>();
                    for (OrderItems row : orderList) {

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
                orderListFiltered = (ArrayList<OrderItems>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OrderAdapterListener {
        void onOrderSelected(OrderItems order);
    }
}