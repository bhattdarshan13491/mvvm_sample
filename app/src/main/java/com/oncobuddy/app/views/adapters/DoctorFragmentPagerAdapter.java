package com.oncobuddy.app.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.oncobuddy.app.R;
import com.oncobuddy.app.views.fragments.AppointmentsListingFragmentNew;
import com.oncobuddy.app.views.fragments.DoctorAppointmentListFragment;
import com.oncobuddy.app.views.fragments.DoctorHomeFragment;
import com.oncobuddy.app.views.fragments.DoctorPatientListingFragment;
import com.oncobuddy.app.views.fragments.NovOncoFragment;
import com.oncobuddy.app.views.fragments.PatientListingFragment;

public class DoctorFragmentPagerAdapter extends FragmentStatePagerAdapter {
   private String tabTitles[] = new String[] { "Home", "My Patients", "Appointments", "OncoHub",};
   private int[] imageResId = { R.drawable.nav_home,R.drawable.nav_patient,R.drawable.nav_appointments,R.drawable.nav_onco_hub};
   private Context context;

    public DoctorFragmentPagerAdapter(@NonNull FragmentManager fm, Context context) {
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
                    return new DoctorHomeFragment();
            case 1:
                    return new DoctorPatientListingFragment();
            case 2:
                    return new DoctorAppointmentListFragment();
            case 3:
                    return new NovOncoFragment();
            default:
                    return new DoctorHomeFragment();
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}