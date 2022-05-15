package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.oncobuddy.app.R;
import com.oncobuddy.app.views.fragments.AlliedCategoriesFragment;
import com.oncobuddy.app.views.fragments.AppointmentsListingFragmentNew;
import com.oncobuddy.app.views.fragments.MedicalRecordsListingFragmentNew;
import com.oncobuddy.app.views.fragments.NovAlliedCareDoctorsFragment;
import com.oncobuddy.app.views.fragments.NovOncoFragment;
import com.oncobuddy.app.views.fragments.PatientHomeScreenNewFragment;

public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
   private String tabTitles[] = new String[] { "Home", "Records", "Appointments", "OncoHub", "Allied\nCare" };
   private int[] imageResId = { R.drawable.nav_home,R.drawable.nav_records,R.drawable.nav_appointments,R.drawable.nav_onco_hub, R.drawable.nav_allied_care };
   private Context context;

    public SampleFragmentPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(tabTitles[position]);
        ImageView img = (ImageView) v.findViewById(R.id.ivIcon);
        img.setImageResource(imageResId[position]);
        return v;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                    return new PatientHomeScreenNewFragment();
            case 1:
                    return new MedicalRecordsListingFragmentNew();
            case 2:
                    return new AppointmentsListingFragmentNew();
            case 3:
                    return new NovOncoFragment();
            case 4:
                    return new NovAlliedCareDoctorsFragment();
            default:
                    return new PatientHomeScreenNewFragment();
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}