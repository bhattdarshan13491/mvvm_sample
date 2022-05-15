package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.MedicalRecord;

import java.util.List;


public class RecentForumsAdapter extends PagerAdapter implements View.OnClickListener{


    private List<MedicalRecord> forumsEventList;
    private LayoutInflater inflater;
    private Context context;
    //private GlideUtils glideUtils;
    private OnItemClickListener onItemClickListener;


    public RecentForumsAdapter(Context context, List<MedicalRecord> forumsEventList) {
        this.context = context;
        this.forumsEventList = forumsEventList;
        inflater = LayoutInflater.from(context);



    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getCount() {
        return forumsEventList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.raw_recent_forums, view, false);

        assert imageLayout != null;
        final ImageView imageView =imageLayout.findViewById(R.id.ivRecentEvent);
        final TextView tvName=imageLayout.findViewById(R.id.tvEventName);
        final TextView tvTime=imageLayout.findViewById(R.id.tvEventTime);

        tvName.setText("Daily life in cancer!");
        tvTime.setText("11 Feb 2021");
        Glide.with(context).load("https://homepages.cae.wisc.edu/~ece533/images/monarch.png").into(imageView);



            imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (onItemClickListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //onItemClickListener.onItemClick(view, forumsEventList.get(position));
                        }
                    }, 0);
                }
            }
        });

        view.addView(imageLayout, 0);


        return imageLayout;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void addData(List<MedicalRecord> premotionalEventList)
    {
        this.forumsEventList =premotionalEventList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onItemClick(View view, MedicalRecord viewModel);

    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


    @Override
    public void onClick(final View view) {
        if (onItemClickListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onItemClickListener.onItemClick(view, (MedicalRecord) view.getTag());
                }
            }, 200);
        }
    }
}
