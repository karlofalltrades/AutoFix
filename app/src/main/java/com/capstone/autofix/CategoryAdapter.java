package com.capstone.autofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<ProductCategory> categoryList;
    private List<ProductCategory> categoryListFiltered;
    private CategoryAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView catname;
        public MyViewHolder(View view) {
            super(view);
            catname = view.findViewById(R.id.cat_name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCategorySelected(categoryListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
    public CategoryAdapter(Context context, List<ProductCategory> categoryList, CategoryAdapter.CategoryAdapterListener listener){
        this.context = context;
        this.listener = listener;
        this.categoryList = categoryList;
        this.categoryListFiltered = categoryList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.MyViewHolder holder, final int position){
        final ProductCategory productCategory = categoryListFiltered.get(position);
        holder.catname.setText(productCategory.getName());
    }

    @Override
    public int getItemCount() {
        return categoryListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    categoryListFiltered = categoryList;
                } else {
                    List<ProductCategory> filteredList = new ArrayList<>();
                    for (ProductCategory row : categoryList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    categoryListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = categoryListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                categoryListFiltered = (ArrayList<ProductCategory>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CategoryAdapterListener{
        void onCategorySelected(ProductCategory productCategory);
    }
}
