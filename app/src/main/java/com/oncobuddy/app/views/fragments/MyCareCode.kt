package com.oncobuddy.app.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentMyCareCodeBinding
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment


class MyCareCode : BaseFragment() {

    private lateinit var binding: FragmentMyCareCodeBinding


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
            R.layout.fragment_my_care_code, container, false
        )

        Log.d("care_code","1 "+getUserObject().careCode)
        //Log.d("care_code","2 "+getUserObject().profile.careCode)
        if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            binding.tvMyCode.text = getUserObject().careCode
        }else{
            binding.tvMyCode.text = getUserObject().profile.careCode
        }

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.linShare.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.shareApp(FourBaseCareApp.activityFromApp,binding.tvMyCode.text.toString() + " is my care code.\n\nDownload Onco buddy app : https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID)
            }
       /*     val clipboard: ClipboardManager = FourBaseCareApp.activityFromApp.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Care code", binding.tvMyCode.text)
            clipboard.setPrimaryClip(clip)
            //showToast(FourBaseCareApp.activityFromApp, "Code copied!")
            val li = layoutInflater
*/
     /*       val layout: View = li.inflate(
                R.layout.layout_toast,
                FourBaseCareApp.activityFromApp.findViewById(R.id.custom_toast_layout) as ViewGroup?
            )

            val tvMessage = layout.findViewById<TextView>(R.id.tvMessage)
            val toast: Toast = Toast.makeText(FourBaseCareApp.activityFromApp, "message", Toast.LENGTH_SHORT)
            toast.setText("new message")
            tvMessage.setText("Code copied!")
            toast.setGravity(Gravity.BOTTOM, 0, 200)
            //other setters
            //other setters
            toast.setView(layout)
            toast.show()*/
        })

        binding.layoutHeader.tvTitle.text = "Care Code"
    }


}