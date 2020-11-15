package com.example.proe.Filter;

import android.widget.Filter;

import com.example.proe.Adapter.AdapterBuyer;
import com.example.proe.Adapter.AdapterOrderBuyer;
import com.example.proe.Model.ModelBuyerUI;

import java.util.ArrayList;

public class FilterBuyer extends Filter {

    private AdapterBuyer adapterBuyer;
    private ArrayList<ModelBuyerUI> filterList;

    public FilterBuyer(AdapterBuyer adapterBuyer, ArrayList<ModelBuyerUI> filterList) {
        this.adapterBuyer = adapterBuyer;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (constraint != null && constraint.length() > 0 ){
            //change to upper case

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelBuyerUI> filterModel = new ArrayList<>();
            for (int i = 0; i< filterList.size(); i++){
                if (filterList.get(i).getCompleteAddress().toUpperCase().contains(constraint) ||
                        filterList.get(i).getShopName().toUpperCase().contains(constraint)){
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
        adapterBuyer.buyerList =(ArrayList<ModelBuyerUI>) results.values;

        adapterBuyer.notifyDataSetChanged();
    }
}
