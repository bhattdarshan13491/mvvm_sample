package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.LayoutDoctorEditProfileBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment

class DoctorEditProfileOptionsFragment : BaseFragment() {

    private lateinit var binding: LayoutDoctorEditProfileBinding


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
            R.layout.layout_doctor_edit_profile, container, false
        )
        binding.layoutHeader.tvTitle.setText("Edit Profile")
        Constants.IS_ACC_SETUP_MODE = false
        setupClickListeners()

    }

    private fun setupClickListeners() {
        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.cardBasicInfo.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorPersonalInfoFragment(), this, false
                )
            }
        })

        binding.cardEduDetails.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    EduBackgroundNovFragment(), this, false
                )
            }
        })

        binding.cardSpecialization.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DocExpAndSpecializationNovFragment(), this, false
                )
            }
        })

        binding.cardDocVerify.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DocVerifyNovFragment(), this, false
                )
            }
        })
    }


}