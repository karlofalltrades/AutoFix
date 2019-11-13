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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Cart> cartList;
    private List<Cart> cartListFiltered;
    private CartAdapter.CartAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView c_name, c_addr, c_cont,c_email;
        public CircleImageView c_image;

        public MyViewHolder(View view) {
            super(view);
            c_name = view.findViewById(R.id.cart_name);
            c_addr = view.findViewById(R.id.cart_addr);
            c_cont = view.findViewById(R.id.cart_cont);
            c_email = view.findViewById(R.id.cart_email);
            c_image = view.findViewById(R.id.cart_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected Cart in callback
                    listener.onCartSelected(cartListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public CartAdapter(Context context, List<Cart> cartList, CartAdapter.CartAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.cartList = cartList;
        this.cartListFiltered = cartList;
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_row_item, parent, false);

        return new CartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartAdapter.MyViewHolder holder, final int position) {
        final Cart cart = cartListFiltered.get(position);
        holder.c_name.setText(cart.getShopName());
        holder.c_addr.setText(cart.getShopAddress());
        holder.c_cont.setText(cart.getShopContact());
        holder.c_email.setText(cart.getShopEmail());
        if (cart.getShopImage()=="null") {
            Picasso.get().load(AppConfig.MAIN_URL+"uploads/blank.png").resize(300, 300).centerCrop().into(holder.c_image);
        }else{
            Picasso.get().load(AppConfig.MAIN_URL + "uploads/" +cart.getShopImage()).resize(300, 300).centerCrop().into(holder.c_image);
        }
    }

    @Override
    public int getItemCount() {
        return cartListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    cartListFiltered = cartList;
                } else {
                    List<Cart> filteredList = new ArrayList<>();
                    for (Cart row : cartList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getShopName().toLowerCase().contains(charString.toLowerCase()) || row.getShopAddress().toLowerCase().contains(charString.toLowerCase()) || row.getShopEmail().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    cartListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cartListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cartListFiltered = (ArrayList<Cart>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CartAdapterListener {
        void onCartSelected(Cart cart);
    }
}
