package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentCareCompanionBinding
import com.oncobuddy.app.databinding.FragmentTrackReportBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.DeleteAccountInput
import com.oncobuddy.app.models.pojo.ProfileStatus
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamMember
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamResponse
import com.oncobuddy.app.models.pojo.care_team_details.PayLoad
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.genetic_report.GeneticResponse
import com.oncobuddy.app.models.pojo.genetic_report.TestStatusesItem
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.DoctorListingForAppointmentAdapter
import com.oncobuddy.app.views.adapters.GeneticReportAdapter
import com.oncobuddy.app.views.adapters.ProgressTrackerAdapter

class GeneticReportFragment : BaseFragment() {

    private lateinit var binding: FragmentTrackReportBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var stagesList: ArrayList<TestStatusesItem>
    private lateinit var stagesTrackerAdapter: GeneticReportAdapter


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
            R.layout.fragment_track_report, container, false
        )

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        setupVM()
        setupObservers()
        callGetGeneticReportsApi()
       // setupObservers()
        //callGetDetailsApi()

        //setProgressRecyclerView()
    }

    private fun setProgressRecyclerView() {
        var profileStatusList = ArrayList<TestStatusesItem>()

        for (i in 0..4) {
            profileStatusList.add(TestStatusesItem())
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                FourBaseCareApp.activityFromApp,
                LinearLayoutManager.VERTICAL,
                false
            )
            var progressTrackerAdapter = GeneticReportAdapter(profileStatusList)
            adapter = progressTrackerAdapter
            progressTrackerAdapter.submitList(profileStatusList)
        }
    }

    private fun callGetGeneticReportsApi() {
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            profileViewModel.callGetGeneticReport(getUserObject().authToken, ""+getUserObject().userIdd)
        }

    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
       // profileViewModel.careTeamDetailsResonseData.observe(this,careTeamDetailsResponse )
        profileViewModel.geneticResonseData.observe(this, geneticReportResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)
    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(context!!, message)
    }

    private val geneticReportResponseObserver = androidx.lifecycle.Observer<GeneticResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            stagesList = ArrayList()
            stagesList.addAll(responseObserver.payLoad.testStatuses)
            setRecyclerView(stagesList)

            Log.d("genetic_report","Size "+stagesList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun setRecyclerView(list: List<TestStatusesItem>?) {

        val arrayList = list?.let { ArrayList<TestStatusesItem>(it) }

        if(arrayList != null && arrayList.size > 0){
            //showHideNoData(false)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                stagesTrackerAdapter = GeneticReportAdapter(arrayList!!, null)
                adapter = stagesTrackerAdapter
                stagesTrackerAdapter.submitList(arrayList)
            }
        }else{
            //showHideNoData(true)
        }
    }

}