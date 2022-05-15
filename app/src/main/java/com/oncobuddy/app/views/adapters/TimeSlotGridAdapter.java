package com.oncobuddy.app.views.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.doctors.time_slots_listing.TimeSlot;

import java.util.List;


public class TimeSlotGridAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    final Context context;
    private final List<TimeSlot> timeSlotDataList;
    private final TimeSlotItemClickListener listener;
    private int selectedItem = -1;

    public TimeSlotGridAdapter(Context context, TimeSlotItemClickListener listener, List<TimeSlot> timeSlotDataList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.timeSlotDataList = timeSlotDataList;
        this.listener = listener;
        selectedItem = -1;
    }



    @Override
    public int getCount() {
        return timeSlotDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        @SuppressLint("ViewHolder")
        View rowView = inflater.inflate(R.layout.time_slot, parent, false);

        holder.tvScheduledTime = rowView.findViewById(R.id.tvScheduledTime);
        holder.llTimeSlot = rowView.findViewById(R.id.linTimeSLot);
        holder.ivSelected = rowView.findViewById(R.id.ivSelected);

        if(timeSlotDataList.get(position).isAvailable()){

            holder.llTimeSlot.setBackground(ContextCompat.getDrawable(context, R.drawable.medical_records_card));

            if(selectedItem >= 0){

                if(selectedItem == position) {
                    holder.tvScheduledTime.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    holder.ivSelected.setVisibility(View.VISIBLE);
                    //holder.llTimeSlot.setBackgroundResource(R.drawable.old_rounded_border_blue);
                }else{
                    holder.tvScheduledTime.setTextColor(context.getResources().getColor(R.color.title_text_color));
                    holder.ivSelected.setVisibility(View.INVISIBLE);
                    //holder.llTimeSlot.setBackgroundResource(R.drawable.old_rounded_border_grey);
                }
            }else {
                holder.tvScheduledTime.setTextColor(context.getResources().getColor(R.color.title_text_color));
                //holder.llTimeSlot.setBackground(ContextCompat.getDrawable(context, R.drawable.medical_records_card));
                //holder.llTimeSlot.setBackgroundResource(R.drawable.old_rounded_border_grey);
            }
        }else{
            holder.llTimeSlot.setBackground(ContextCompat.getDrawable(context, R.drawable.medical_records_gray));
            //holder.tvScheduledTime.setTextColor(context.getResources().getColor(R.color.gray_font));
            //holder.llTimeSlot.setBackgroundResource(R.drawable.old_rounded_border_grey);
        }
        holder.tvScheduledTime.setText(timeSlotDataList.get(position).getTime());

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(timeSlotDataList.get(position).isAvailable()) {
                    selectedItem = position;
                    notifyDataSetChanged();
                    if(listener != null)
                    listener.onTimeSlotItemClick(timeSlotDataList.get(position));
                }else{
                    Toast.makeText(context, "This timeslot is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rowView;
    }

    public class Holder {
        TextView tvScheduledTime;
        ImageView ivSelected;
        LinearLayout llTimeSlot;
    }




}