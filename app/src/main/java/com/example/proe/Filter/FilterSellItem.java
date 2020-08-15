package com.example.proe.Filter;

import android.widget.Filter;

import com.example.proe.Adapter.AdapterSellItem;
import com.example.proe.Model.ModelSellItem;

import java.util.ArrayList;

public class FilterSellItem extends Filter {

    private AdapterSellItem adapterSellItem;
    private ArrayList<ModelSellItem> filterList;

    public FilterSellItem(AdapterSellItem adapterSellItem, ArrayList<ModelSellItem> filterList){
        this.adapterSellItem = adapterSellItem;
        this.filterList = filterList;
    }
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (constraint != null && constraint.length() > 0 ){
            //change to upper case

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelSellItem> filterModel = new ArrayList<>();
            for (int i = 0; i< filterList.size(); i++){
                if (filterList.get(i).getItemtitle().toUpperCase().contains(constraint) ||
                filterList.get(i).getItemcategory().toUpperCase().contains(constraint)){
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
        adapterSellItem.sellItemslist =(ArrayList<ModelSellItem>) results.values;

        adapterSellItem.notifyDataSetChanged();
    }
}
