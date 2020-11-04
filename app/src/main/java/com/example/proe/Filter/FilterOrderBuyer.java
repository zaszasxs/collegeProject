package com.example.proe.Filter;

import android.widget.Filter;

import com.example.proe.Adapter.AdapterOrderBuyer;
import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Model.ModelOrderBuyer;
import com.example.proe.Model.ModelSellItem;

import java.util.ArrayList;

public class FilterOrderBuyer extends Filter {

    private AdapterOrderBuyer adapterOrderBuyer;
    private ArrayList<ModelOrderBuyer> filterList;

    public FilterOrderBuyer(AdapterOrderBuyer adapterOrderBuyer, ArrayList<ModelOrderBuyer> filterList){
        this.adapterOrderBuyer = adapterOrderBuyer;
        this.filterList = filterList;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (constraint != null && constraint.length() > 0 ){
            //change to upper case

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelOrderBuyer> filterModel = new ArrayList<>();
            for (int i = 0; i< filterList.size(); i++){
                if (filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    filterModel.add(filterList.get(i));
                }
            }
            filterResults.count = filterModel.size();
            filterResults.values = filterModel;
        }
        else {
            filterResults.count = filterList.size();
            filterResults.values = filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterOrderBuyer.orderBuyerArrayList =(ArrayList<ModelOrderBuyer>) results.values;

        adapterOrderBuyer.notifyDataSetChanged();
    }
}
