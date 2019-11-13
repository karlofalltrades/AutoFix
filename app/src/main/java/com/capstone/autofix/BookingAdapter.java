package com.capstone.autofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Booking> bookList;
    private List<Booking> bookListFiltered;
    private BookingAdapter.BookingAdapterListener listener;
    onLongItemClickListener mOnLongItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView b_id, b_addr, b_date,b_time,b_stat,c_name,b_shop,b_emp;

        public MyViewHolder(View view) {
            super(view);
            b_addr = view.findViewById(R.id.book_addr);
            b_date = view.findViewById(R.id.book_date);
            b_time = view.findViewById(R.id.book_time);
            b_stat = view.findViewById(R.id.book_stat);
            c_name = view.findViewById(R.id.cust_name);
            b_shop = view.findViewById(R.id.book_shop);
            b_emp = view.findViewById(R.id.book_emp);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected book in callback
                    listener.onBookSelected(bookListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public BookingAdapter(Context context, List<Booking> bookList, BookingAdapter.BookingAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.bookList = bookList;
        this.bookListFiltered = bookList;
    }

    @Override
    public BookingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_row_item, parent, false);

        return new BookingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookingAdapter.MyViewHolder holder, final int position) {
        final Booking book = bookListFiltered.get(position);
        holder.b_addr.setText("Address: "+book.getBookingAddress());
        holder.b_date.setText("Date: "+book.getBookingDate());
        holder.b_time.setText("Time: "+book.getBookingTime());
        holder.b_stat.setText("Status: "+book.getBookingStatus());
        holder.c_name.setText("Name: "+book.getCustomerID());
        holder.b_shop.setText("Shop Name: "+book.getShopId());
        holder.b_emp.setText("Mekaniko: "+book.getEmpName());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnLongItemClickListener != null){
                    mOnLongItemClickListener.ItemLongClicked(v,position);
                }
                return true;
            }
        });

    }

    public void setOnLongItemClickListener(onLongItemClickListener onLongItemClickListener){
        mOnLongItemClickListener = onLongItemClickListener;
    }

    @Override
    public int getItemCount() {
        return bookListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    bookListFiltered = bookList;
                } else {
                    List<Booking> filteredList = new ArrayList<>();
                    for (Booking row : bookList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getBookingDate().toLowerCase().contains(charString.toLowerCase()) || row.getBookingTime().toLowerCase().contains(charString.toLowerCase()) || row.getShopId().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    bookListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = bookListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                bookListFiltered = (ArrayList<Booking>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface BookingAdapterListener {
        void onBookSelected(Booking book);
    }
    public interface onLongItemClickListener{
        void ItemLongClicked(View v, int position);
    }
}
