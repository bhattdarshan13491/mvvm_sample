package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentCareCompanionBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.DeleteAccountInput
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamMember
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamResponse
import com.oncobuddy.app.models.pojo.care_team_details.PayLoad
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel

class CareCOmpanionFragment : BaseFragment() {

    private lateinit var binding: FragmentCareCompanionBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var careTeamMemberDetails: PayLoad


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
            R.layout.fragment_care_companion, container, false
        )

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        setupVM()
        setupObservers()
        callGetDetailsApi()

    }
    private fun callGetDetailsApi() {
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            profileViewModel.callGetCareTeamMemberDetails(getUserObject().authToken, getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER))
        }

    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.careTeamDetailsResonseData.observe(this,careTeamDetailsResponse )
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "No Care Companion associated to this profile. Please reach out to customer care if any questions.")
    }


    private val careTeamDetailsResponse = androidx.lifecycle.Observer<CareTeamResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.isSuccess) {
            careTeamMemberDetails = responseObserver.payLoad
            binding.tvDoctorName.setText(careTeamMemberDetails.careTeamMember.firstName)
            binding.tvQualification.setText(careTeamMemberDetails.careTeamMember.headline)
            binding.tvSpeciality.setText("Care Team")
            if(careTeamMemberDetails.totalYearsExperience.isNullOrEmpty() || careTeamMemberDetails.totalYearsExperience.equals("null",false)){
                careTeamMemberDetails.totalYearsExperience = "00"
                binding.tvExperience.setText("00 years of experience")
            }else{
                binding.tvExperience.setText(careTeamMemberDetails.totalYearsExperience+"years of experience")
            }

            binding.tvLanguages.setText("English, Hindi")
            binding.tvAboutVal.setText(""+careTeamMemberDetails.about)
            binding.tvTotalPatientsTitleVal.setText(""+CommonMethods.getStringWithOnePadding(careTeamMemberDetails.supportedPatientsCount.toString()))

            if(!careTeamMemberDetails.careTeamMember.dpLink.isNullOrEmpty())
                Glide.with(FourBaseCareApp.activityFromApp).load(careTeamMemberDetails.careTeamMember.dpLink)
                    .placeholder(R.drawable.ic_user_image).circleCrop().into(binding.ivProfilePic)



        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "No Care Companion associated to this profile. Please reach out to customer care if any questions.")

        binding.executePendingBindings()
        binding.invalidateAll()
    }





}