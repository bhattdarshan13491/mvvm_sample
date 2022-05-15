package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAppointmentConfirmedBinding
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*



class AppointmentConfirmedFragment : BaseFragment() {

    private lateinit var  binding : FragmentAppointmentConfirmedBinding
    private lateinit var  askOverlayPermissionDialogue: Dialog
    private lateinit var doctor: Doctor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_appointment_confirmed,container,false)

        if(Constants.IS_FROM_HOME_SCREEN){
            binding.tvBackToHome.setText("Go back to Home")
            Constants.IS_FROM_HOME_SCREEN = false
        }

        if(arguments != null){
            doctor = arguments?.getParcelable("DOCTOR")!!
            binding.tvApptId.text = "ID : "+arguments?.getString("APPOINTMENT_ID")
            binding.tvDoctorName.text = arguments?.getString("DOCTOR_NAME")
            binding.tvSpeciality.text = ""+doctor.specialization
            binding.tvDate.text = CommonMethods.convertTimeSlotFormat1(""+arguments?.getString("DATE_TIME"))
            binding.tvTime.text = changeTimeFormat(""+arguments?.getString("DATE_TIME"))

            if (doctor.verified) {
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified)
                    .into(binding.ivVerified)
            } else {
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified)
                    .into(binding.ivVerified)
            }
            if(!doctor.displayPicUrl.isNullOrEmpty()){
                Glide.with(FourBaseCareApp.activityFromApp).load(doctor.displayPicUrl).placeholder(R.drawable.ic_doctor).transform(
                    CircleCrop()
                ).into(binding.ivDoctorImage)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_doctor).placeholder(R.drawable.ic_doctor).transform(
                    CircleCrop()
                ).into(binding.ivDoctorImage)
            }
        }

        binding.tvBackToHome.setOnClickListener(View.OnClickListener {
                val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
                val count: Int = fm.getBackStackEntryCount()
                Constants.IS_LIST_UPDATED = true
                for (i in 0 until count) {
                    fm.popBackStackImmediate()
                }


        })

    }

    fun changeTimeFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                "hh:mm aa",
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }

    }

    private fun showOverlayPermissionDialogue() {
        askOverlayPermissionDialogue = Dialog(FourBaseCareApp.activityFromApp)
        askOverlayPermissionDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        askOverlayPermissionDialogue.setContentView(R.layout.dialogue_cancel_appointment)

        val ivLogo: ImageView = askOverlayPermissionDialogue.findViewById(R.id.ivLogo)
        ivLogo.setImageDrawable(FourBaseCareApp.activityFromApp.getDrawable(R.drawable.ic_cancel_alert))

        val tvTitleText: TextView = askOverlayPermissionDialogue.findViewById(R.id.tvTitleText)
        /*if(Constants.IS_FROM_ADD_APPOINTMENT){
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }else{
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }*/
        tvTitleText.setText("For seamless calling experience, You need to enable overlay feature from your device")

        val btnNo: TextView = askOverlayPermissionDialogue.findViewById(R.id.btnNo)
        btnNo.visibility = View.GONE

        val linNo: LinearLayout = askOverlayPermissionDialogue.findViewById(R.id.linNo)
        linNo.visibility = View.GONE

        val btnYes: TextView = askOverlayPermissionDialogue.findViewById(R.id.btnYes)
            btnYes.setText(getString(R.string.goto_settings))



        btnYes.setOnClickListener(View.OnClickListener {
            askOverlayPermissionDialogue.dismiss()
            //fragmentManager?.popBackStack()
            requestOverLayPermission()
        })

        askOverlayPermissionDialogue.show()
    }
}