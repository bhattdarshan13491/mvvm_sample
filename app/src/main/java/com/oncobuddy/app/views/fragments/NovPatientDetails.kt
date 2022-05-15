package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentCareCompanionBinding
import com.oncobuddy.app.databinding.FragmentPatientDetailsBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.view_models.RecordsViewModel

class NovPatientDetails : BaseFragment() {

    private lateinit var binding: FragmentPatientDetailsBinding
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var patientDetails: PatientDetails
    private val ADD_DOCUMENT_PERMISSION = 103
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }

    private fun setupVM() {
      recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_patient_details, container, false
        )
        setupVM()
        getDataFromArguments()
        setUserData()
        setupClickListeners()

    }

    private fun getDataFromArguments() {
        if (getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)) {
            binding.ivEdit.visibility = View.VISIBLE
            binding.cardRecords.visibility = View.GONE
            binding.cardAppointments.visibility = View.GONE
        }
        patientDetails = arguments?.getSerializable(Constants.PATIENT_DATA) as PatientDetails
    }

    private fun setupObservers() {
        recordsViewModel.viewReportResponseData.observe(this, responseObserver)
        recordsViewModel.viewSummaryResponseData.observe(this, responseObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val responseObserver = androidx.lifecycle.Observer<GeneticReportResponse> { responseObserver ->
        Log.d("list_log", "response came")
        if(responseObserver.isSuccess){
            showPdfFile(responseObserver.payLoad.link)
        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }



    private fun setupClickListeners() {

        binding.cardGeneticReport.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if(getUserObject().profile != null && getUserObject().profile.patientType != null && getUserObject().profile.patientType.equals(Constants.PATIENT_TYPE_TEST_TAKER)){
                    /*CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp, GeneticReportFragment(), this, false
                    )*/

                    if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                        recordsViewModel.callGetGeneticReportPdf(
                            getUserAuthToken(),
                            getUserIdd().toString()
                        )
                    }

                    /*showPdfFile()*/
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "This information is available for test takers only")
                }

            }
        })

        binding.cardSummary.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if(getUserObject().profile != null && getUserObject().profile.patientType != null && getUserObject().profile.patientType.equals(Constants.PATIENT_TYPE_TEST_TAKER)){
                    if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                            recordsViewModel.callGetSummaryPdf(getUserAuthToken(), getUserIdd().toString())
                        }
                    }
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "This information is available for test takers only")
                }

            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivEdit.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) {
                var bundle = Bundle()
                Constants.PATIENT_ID_FOR_RECORDS = "" + patientDetails.id
                bundle.putString(Constants.PATIENT_ID, "" + patientDetails.id)
                bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                bundle.putSerializable(Constants.PATIENT_DATA, patientDetails)
                var medicalRecordFragment = EditPatientProfileByCGFragment()
                medicalRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    medicalRecordFragment, this, false,Constants.FRAGMENT_RESULT
                )
            }
        })

        binding.cardRecords.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) {
                var bundle = Bundle()
                Constants.PATIENT_ID_FOR_RECORDS = "" + patientDetails.id
                bundle.putString(Constants.PATIENT_ID, "" + patientDetails.id)
                bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                bundle.putSerializable(Constants.PATIENT_DATA, patientDetails)
                var medicalRecordFragment = PatientRecordsListingByDrFragment()
                medicalRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    medicalRecordFragment, this, false
                )
            }
        })
    }

    private fun showPdfFile(link:String) {
        var bundle = Bundle()
        bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
        bundle.putString(Constants.SERVER_FILE_URL, link)
        bundle.putString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_REPORT)
        var fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
        fullScreenPDFViewFragment.arguments = bundle
        if (checkPermission(FourBaseCareApp.activityFromApp)) {
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                fullScreenPDFViewFragment, this, false

            )

        } else {
            Log.d("item_click_1", "double click captured")
        }
    }

    private fun setUserData() {

        if(!patientDetails.cancerStage.isNullOrEmpty()){
            binding.tvCancerStage.setText("Stage "+patientDetails.cancerStage+" Cancer")
        }

        binding.tvName.text = patientDetails.firstName
        Log.d("set_data_log","cancer type 3 "+patientDetails.cancerType.name)
        if(patientDetails.cancerType != null){
            binding.tvCancerType.text = patientDetails.cancerType.name
        }else{
            binding.tvCancerType.visibility = View.GONE
        }
        if(patientDetails.cancerSubType != null){
            binding.tvCancerSUbType.text = patientDetails.cancerSubType.name
        }else{
            binding.tvCancerSUbType.visibility = View.GONE
        }
        if(!patientDetails.dpLink.isNullOrEmpty()){
            Glide.with(FourBaseCareApp.activityFromApp).load(patientDetails.dpLink).
            placeholder(R.drawable.ic_profile_circular_white_bg)
                .error(R.drawable.ic_profile_circular_white_bg).circleCrop().into(binding.ivProfilePic)

        }

        if(!patientDetails.height.isNullOrEmpty()){
            binding.tvHeight.setText(patientDetails.height+" inch")
        }else{
            binding.tvHeight.setText("NA")
        }

        if(!patientDetails.weight.isNullOrEmpty()){
            binding.tvWeight.setText(patientDetails.weight+" kg")
        }else{
            binding.tvWeight.setText("NA")
        }
        if(!patientDetails.dob.isNullOrEmpty()){
            var age = ""+CommonMethods.getAge(CommonMethods.getDateForAge(patientDetails.dob))
            Log.d("patient_details","age "+age)

            binding.tvAge.setText(age+" Years")
        }else{
            binding.tvAge.setText("NA")
        }

        if(!patientDetails.gender.isNullOrEmpty()){
            binding.tvGender.setText(CommonMethods.returnGenderName(patientDetails.gender))
        }else{
            binding.tvGender.setText("NA")
        }


    }

    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    Log.d("permission_log","Coming here")
                    val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(
                        context
                    )
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_necessary)
                    alertBuilder.setMessage(R.string.external_storage_permission_needed)
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            ),
                            ADD_DOCUMENT_PERMISSION
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        ),
                        ADD_DOCUMENT_PERMISSION
                    )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                var patientDetailsByCGObj = getPatientObjectByCG()
                if (patientDetailsByCGObj != null) {
                    patientDetails.firstName = patientDetailsByCGObj.firstName
                    patientDetails.email = patientDetailsByCGObj.email
                    patientDetails.phoneNumber = patientDetailsByCGObj.phoneNumber
                    patientDetails.height = patientDetailsByCGObj.height.toString()
                    patientDetails.weight = patientDetailsByCGObj.weight.toString()
                    patientDetails.gender = patientDetailsByCGObj.gender
                    patientDetails.dob = patientDetailsByCGObj.dob
                    patientDetails.dpLink = patientDetailsByCGObj.dpLink
                    patientDetails.id = patientDetailsByCGObj.id
                    patientDetails.cancerStage = patientDetailsByCGObj.cancerStage
                    patientDetails.aadharNumber = patientDetailsByCGObj.aadharNumber

                    Log.d("set_data_log","cancer type 0 "+patientDetailsByCGObj.cancerType.name)
                    Log.d("set_data_log","cancer type 0 "+patientDetailsByCGObj.cancerType.id)
                    if(patientDetailsByCGObj.cancerType != null) {
                        var cancerType = com.oncobuddy.app.models.pojo.profile.CancerType()
                        cancerType.id = patientDetailsByCGObj.cancerType.id
                        cancerType.name = patientDetailsByCGObj.cancerType.name
                        patientDetails.cancerType = cancerType
                        Log.d("set_data_log","cancer type set 1 "+patientDetails.cancerType.name)

                    }

                    if(patientDetailsByCGObj.cancerSubType != null) {
                        var cancerSubType = com.oncobuddy.app.models.pojo.profile.CancerType()
                        cancerSubType.id = patientDetailsByCGObj.cancerSubType.id
                        cancerSubType.name = patientDetailsByCGObj.cancerSubType.name
                        patientDetails.cancerSubType = cancerSubType
                    }
                    setUserData()

                }

            }


        }

    }



}