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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Item> itemList;
    private List<Item> itemListFiltered;
    private ItemAdapter.ItemAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, brand, qty, price;
        public CircleImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.item_name);
            brand = view.findViewById(R.id.item_brand);
            qty = view.findViewById(R.id.item_qty);
            price = view.findViewById(R.id.item_price);
            thumbnail = view.findViewById(R.id.item_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onItemSelected(itemListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ItemAdapter(Context context, List<Item> itemList, ItemAdapter.ItemAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.itemList = itemList;
        this.itemListFiltered = itemList;
    }

    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prod_row_item, parent, false);

        return new ItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.MyViewHolder holder, final int position) {
        final Item items = itemListFiltered.get(position);
        holder.name.setText(items.getProdName());
        holder.brand.setText(items.getProdBrand());
        holder.qty.setText("Quantity: "+items.getProdQuantity());
        holder.price.setText(items.getProdPrice());

        if (items.getProdImage()=="null") {
            Picasso.get().load(AppConfig.MAIN_URL +"uploads/blank.png").resize(300, 300).centerCrop().into(holder.thumbnail);
        }else{
            Picasso.get().load(AppConfig.MAIN_URL+"uploads/" + items.getProdImage()).resize(300, 300).centerCrop().into(holder.thumbnail);
        }


    }

    @Override
    public int getItemCount() {
        return itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    itemListFiltered = itemList;
                } else {
                    List<Item> filteredList = new ArrayList<>();
                    for (Item row : itemList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProdName().toLowerCase().contains(charString.toLowerCase()) || row.getProdBrand().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemListFiltered = (ArrayList<Item>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ItemAdapterListener {
        void onItemSelected(Item items);
    }
}
