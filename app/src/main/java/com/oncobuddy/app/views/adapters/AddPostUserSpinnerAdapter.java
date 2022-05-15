package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.AddPostUserOption;

public class AddPostUserSpinnerAdapter extends ArrayAdapter<AddPostUserOption> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private AddPostUserOption[] values;

    public AddPostUserSpinnerAdapter(Context context, int textViewResourceId,
                                     AddPostUserOption[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
       return values.length;
    }

    @Override
    public AddPostUserOption getItem(int position){
       return values[position];
    }

    @Override
    public long getItemId(int position){
       return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextAppearance(R.style.DropdownText);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values[position].getName());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextAppearance(R.style.DropdownText);
        label.setText(values[position].getName());

        return label;
    }
}