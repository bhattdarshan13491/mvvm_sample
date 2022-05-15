package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentAccountSettingsBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.activities.NovForgotPwdActivity

class PatientAccountSettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentAccountSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account_settings, container, false
        )

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.linCHangePassword.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                val intent = Intent(FourBaseCareApp.activityFromApp, NovForgotPwdActivity::class.java)
                intent.putExtra(Constants.SOURCE, "patient_profile")
                startActivity(intent)
                FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
            }
        })

        binding.linDeleteAccount.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DeleteAccountFragment(), this, false
                )
            }
        })




        binding.layoutHeader.tvTitle.text = "Account Settings"
    }


}