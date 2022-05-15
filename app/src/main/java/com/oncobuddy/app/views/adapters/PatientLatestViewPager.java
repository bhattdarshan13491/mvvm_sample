package com.oncobuddy.app.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.NextAppointment;
import com.oncobuddy.app.models.pojo.NextForum;

import java.util.List;

public class PatientLatestViewPager extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    // The items to display in your RecyclerView
    private List<Object> items;

    private final int FORUM = 0, APPOINTMENT = 1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PatientLatestViewPager(List<Object> items) {
        this.items = items;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof NextAppointment) {
            return APPOINTMENT;
        } else if (items.get(position) instanceof NextForum) {
            return FORUM;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case FORUM:
                View v1 = inflater.inflate(R.layout.raw_forum, viewGroup, false);
                viewHolder = new ViewHolder1(v1);
                break;
            case APPOINTMENT:
                View v2 = inflater.inflate(R.layout.raw_forum, viewGroup, false);
                viewHolder = new ViewHolder2(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.raw_forum, viewGroup, false);
                viewHolder = new ViewHolder1(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case FORUM:
                ViewHolder1 vh1 = (ViewHolder1) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case APPOINTMENT:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                ViewHolder1 vh = (ViewHolder1) viewHolder;
                configureViewHolder1(vh, position);
                break;
        }
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder {

        //private TextView label1, label2;

        public ViewHolder1(View v) {
            super(v);
          /*  label1 = (TextView) v.findViewById(R.id.text1);
            label2 = (TextView) v.findViewById(R.id.text2);*/
        }

        /*public TextView getLabel1() {
            return label1;
        }

        public void setLabel1(TextView label1) {
            this.label1 = label1;
        }

        public TextView getLabel2() {
            return label2;
        }

        public void setLabel2(TextView label2) {
            this.label2 = label2;
        }*/
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        //private ImageView ivExample;

        public ViewHolder2(View v) {
            super(v);
          //  ivExample = (ImageView) v.findViewById(R.id.ivExample);
        }

        /*public ImageView getImageView() {
            return ivExample;
        }

        public void setImageView(ImageView ivExample) {
            this.ivExample = ivExample;
        }*/
    }

    private void configureViewHolder1(ViewHolder1 vh1, int position) {
       /* Forum forum = (User) items.get(position);
        if (user != null) {
            vh1.getLabel1().setText("Name: " + user.name);
            vh1.getLabel2().setText("Hometown: " + user.hometown);
        }*/
    }

    private void configureViewHolder2(ViewHolder2 vh2, int position) {
        //vh2.getImageView().setImageResource(R.drawable.sample_golden_gate);
    }


}
