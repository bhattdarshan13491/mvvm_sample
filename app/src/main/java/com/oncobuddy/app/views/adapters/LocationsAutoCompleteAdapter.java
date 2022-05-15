package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.hospital_listing.Locations;

import java.util.ArrayList;

public class LocationsAutoCompleteAdapter extends ArrayAdapter<Locations> {
    private final String MY_DEBUG_TAG = "LocationsAdapter";
    private ArrayList<Locations> items;
    private ArrayList<Locations> itemsAll;
    private ArrayList<Locations> suggestions;
    private int viewResourceId;

    public LocationsAutoCompleteAdapter(Context context, int viewResourceId, ArrayList<Locations> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<Locations>) items.clone();
        this.suggestions = new ArrayList<Locations>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Locations customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.tvName);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Locations Name:"+customer.getName());
                customerNameLabel.setText(customer.getName());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Locations)(resultValue)).getName(); 
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Locations customer : itemsAll) {
                    if(customer.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Locations> filteredList = (ArrayList<Locations>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Locations c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}