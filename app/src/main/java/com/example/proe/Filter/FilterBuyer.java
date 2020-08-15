package com.example.proe.Filter;

import android.widget.Filter;

import com.example.proe.Adapter.AdapterBuyer;
import com.example.proe.Model.ModelBuyerUI;

import java.util.ArrayList;

public class FilterBuyer extends Filter {

    private AdapterBuyer adapterBuyer;
    private ArrayList<ModelBuyerUI> filterlist;

    public FilterBuyer(AdapterBuyer adapterBuyer, ArrayList<ModelBuyerUI> filterlist) {
        this.adapterBuyer = adapterBuyer;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){
            //change to upper case, to make  case insensitive
            constraint = constraint.toString().toUpperCase();
            //out filtered list
            ArrayList<ModelBuyerUI> filterbuyer = new ArrayList<>();
            for (int i = 0 ; i< filterlist.size();i++){
                if (filterlist.get(i).getShopName().toUpperCase().contains(constraint) ||
                        filterlist.get(i).getShopOpen().toUpperCase().contains(constraint)){
                    filterbuyer.add(filterlist.get(i));
                }
            }
            results.count = filterbuyer.size();
            results.values = filterbuyer;
        }
        else {
            results.count = filterlist.size();
            results.values = filterlist;
        }

        return null;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterBuyer.buyerList = (ArrayList<ModelBuyerUI>) results.values;
        //refresh adapter
        adapterBuyer.notifyDataSetChanged();
    }
}
