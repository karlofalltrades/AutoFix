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

import androidx.recyclerview.widget.RecyclerView;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<CarsModel> CarsList;
    private List<CarsModel> CarsListFiltered;
    private CarsAdapter.CarsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cbrand,cmodel,cyear,pnum;
        public MyViewHolder(View view) {
            super(view);
            cbrand = view.findViewById(R.id.c_brand);
            cmodel = view.findViewById(R.id.c_model);
            cyear = view.findViewById(R.id.c_year);
            pnum = view.findViewById(R.id.p_num);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCarsSelected(CarsListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
    public CarsAdapter(Context context, List<CarsModel> CarsList, CarsAdapter.CarsAdapterListener listener){
        this.context = context;
        this.listener = listener;
        this.CarsList = CarsList;
        this.CarsListFiltered = CarsList;
    }
    @Override
    public CarsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cars_row_item, parent, false);

        return new CarsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarsAdapter.MyViewHolder holder, final int position){
        final CarsModel carsModel = CarsListFiltered.get(position);
        holder.cbrand.setText(carsModel.getCarBrand());
        holder.cmodel.setText(carsModel.getCarModel());
        holder.cyear.setText(carsModel.getCarYear());
        holder.pnum.setText(carsModel.getCarPlate_number());
    }

    @Override
    public int getItemCount() {
        return CarsListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    CarsListFiltered = CarsList;
                } else {
                    List<CarsModel> filteredList = new ArrayList<>();
                    for (CarsModel row : CarsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCarModel().toLowerCase().contains(charString.toLowerCase()) || row.getCarBrand().toLowerCase().contains(charString.toLowerCase()) || row.getCarYear().toLowerCase().contains(charString.toLowerCase()) || row.getCarPlate_number().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    CarsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = CarsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                CarsListFiltered = (ArrayList<CarsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CarsAdapterListener{
        void onCarsSelected(CarsModel carsModel);
    }
}