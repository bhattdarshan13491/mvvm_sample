package com.oncobuddy.app.views.fragments

import com.oncobuddy.app.models.factory_providers.DoctorSetupChangeListener
import android.os.Bundle
import android.view.View
import com.oncobuddy.app.R
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.databinding.ActivityNovDoctorAccountSetupBinding
import com.oncobuddy.app.utils.base_classes.BaseActivity

class DoctorAccountSetupNovFragment : BaseActivity(),
    DoctorSetupChangeListener {

    private lateinit var binding : ActivityNovDoctorAccountSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nov_doctor_account_setup)

        val personalInfoNovFragment =
            DrUpdatePersonalInfoNovFragment()
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().add(R.id.frameLayout, personalInfoNovFragment)
            .commit()
        
        setupCLickListeners()


    }

    private fun setupCLickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener { onBackPressed() })
    }


    override fun doctorSetupChange(fragmentName: String) {
        when (fragmentName) {
            "PersonalInfoNovFragment" -> {
                binding.tvCurrentStep.text = "Personal Information"
                binding.tvNextStep.text = "Next : Experience and Specialization"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_one))
            }
            "DocExpAndSpecializationNovFragment" -> {
                binding.tvCurrentStep.text = "Experience and Specialization"
                binding.tvNextStep.text = "Next : Education Background"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_two))
            }
            "EduBackgroundNovFragment" -> {
                binding.tvCurrentStep.text = "Education Background"
                binding.tvNextStep.text = "Next : Practice Information"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_three))
            }
            "PracticeInfoNovFragment" -> {
                binding.tvCurrentStep.text = "Practice Information"
                binding.tvNextStep.text = "Next : Document Verification"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_three))
            }
            "DocVerifyNovFragment" -> {
                binding.tvCurrentStep.text = "Document Verification"
                binding.tvNextStep.text = "Next : Available time Slot and Fee"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_three))
            }
            "VirtualConsultNovFragment" -> {
                binding.tvCurrentStep.text = "Virtual Consultation Setup"
                binding.tvNextStep.text = "Next : Get Started"
                binding.ivStep.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_step_three))
            }
        }
    }
}