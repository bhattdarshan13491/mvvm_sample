package com.oncobuddy.app.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityNovDoctorDetailsBinding
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity

/**
 * Nov doctor details activity
 * SHows all the doctor details to the patient or care giver
 * @constructor Create empty Nov doctor details activity
 */
class NovDoctorDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityNovDoctorDetailsBinding
    private lateinit var doctorDetails : DoctorDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovDoctorDetailsActivity, R.layout.activity_nov_doctor_details)

        binding.btBookConsultation.setText("Connect")
        binding.ivShare.visibility = View.GONE
        doctorDetails = intent.getParcelableExtra(Constants.DOCTOR_DATA)!!
        binding.tvDoctorName.text = doctorDetails.firstName
        binding.tvSpeciality.text = doctorDetails.specialization
        binding.tvQualification.text = doctorDetails.designation
        binding.tvExperience.text = doctorDetails.experience+" years of experience"
        binding.tvFees.text = "Fee Rs "+doctorDetails.consultationFee+" per\nconsultation"

        if(doctorDetails.isVerified != null && !doctorDetails.isVerified){
            binding.tvVerified.text = "Unverified"
            binding.tvVerified.setTextColor(ContextCompat.getColor(this@NovDoctorDetailsActivity, R.color.red))
            Glide.with(this@NovDoctorDetailsActivity).load(R.drawable.ic_not_verified).into(binding.ivVerified)
            Glide.with(this@NovDoctorDetailsActivity).load(R.drawable.profile_unverified_bg).into(binding.ivProfileBG)
            binding.tvVerified.background = ContextCompat.getDrawable(this@NovDoctorDetailsActivity,R.drawable.light_red_bg)
        }

        if(!doctorDetails.description.isNullOrEmpty())
        binding.tvAboutVal.text = doctorDetails.description

        binding.btBookConsultation.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@NovDoctorDetailsActivity, NovAccountSetupActivity::class.java)
            intent.putExtra("should_assign", true)
            setResult(Activity.RESULT_OK, intent)
            finish()

        })

        binding.ivBackArrow.setOnClickListener(){
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
            finish()
        }


        if(!doctorDetails.dpLink.isNullOrEmpty())
            Glide.with(this@NovDoctorDetailsActivity)
                .load(doctorDetails.dpLink)
                .placeholder(R.drawable.ic_user_image)
                .circleCrop()
                .into(binding.ivProfilePic)

    }

}