package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalDetails;

import java.util.ArrayList;

public class HospitalsAutoCOmpleteAdapter extends ArrayAdapter<HospitalDetails> {
    private final String MY_DEBUG_TAG = "HospitalDetailsAdapter";
    private ArrayList<HospitalDetails> items;
    private ArrayList<HospitalDetails> itemsAll;
    private ArrayList<HospitalDetails> suggestions;
    private int viewResourceId;

    public HospitalsAutoCOmpleteAdapter(Context context, int viewResourceId, ArrayList<HospitalDetails> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<HospitalDetails>) items.clone();
        this.suggestions = new ArrayList<HospitalDetails>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        HospitalDetails customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.tvName);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView HospitalDetails Name:"+customer.getName());
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
            String str = ((HospitalDetails)(resultValue)).getName(); 
            return str;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (HospitalDetails customer : itemsAll) {
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
            ArrayList<HospitalDetails> filteredList = (ArrayList<HospitalDetails>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (HospitalDetails c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };

}