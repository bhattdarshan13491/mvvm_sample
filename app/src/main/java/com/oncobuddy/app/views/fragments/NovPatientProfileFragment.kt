package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R

import com.oncobuddy.app.databinding.FragmentNovPatientProfileBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.patient_details_by_cg.CancerType
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetails
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetailsByCGResponse
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.view_models.RecordsViewModel

class NovPatientProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentNovPatientProfileBinding
    private val ADD_DOCUMENT_PERMISSION = 103
    private lateinit var recordsViewModel: RecordsViewModel
    private lateinit var profileViewModel: ProfileViewModel

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
            R.layout.fragment_nov_patient_profile, container, false
        )
         binding.tvName.setText(getUserObject().firstName)
        if(!getUserObject().dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink).
            placeholder(R.drawable.ic_profile_circular_white_bg)
                .error(R.drawable.ic_profile_circular_white_bg).circleCrop().into(binding.ivProfileImage)

        setOnClickListeners()
        setupVM()
        setupObservers()

        if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            binding.tvSubName.text = "Basic Info"
            binding.linMyPatient.visibility = View.VISIBLE
            binding.cardTrackGR.visibility = View.GONE
            binding.cardSummary.visibility = View.GONE
            binding.tvMyCareCode.text = "Linked Care Code"
            binding.tvMyFavourites.text = "Care Companion"
            binding.cardMyDoctors.visibility = View.GONE
            binding.tvMyCC.text = "Care Companion"

            binding.cardMyPatient.setOnClickListener(View.OnClickListener {
                if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                    profileViewModel.callGetPatientDetailsByCG(
                        getUserAuthToken(),
                        ""
                    )
                }
            })

        }

    }

    private fun setupObservers() {
        recordsViewModel.viewReportResponseData.observe(this, responseObserver)
        recordsViewModel.viewSummaryResponseData.observe(this, responseObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)

        profileViewModel.patientDetailsByCGData.observe(this, patientDetailsObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun setupVM() {

        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

        recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
    }

    private val responseObserver = androidx.lifecycle.Observer<GeneticReportResponse> { responseObserver ->
        Log.d("list_log", "response came")
        if(responseObserver.isSuccess){
            showPdfFile(responseObserver.payLoad.link)
        }
    }

    private val patientDetailsObserver = androidx.lifecycle.Observer<PatientDetailsByCGResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.isSuccess) {
                var patientDetailsByCGObj = responseObserver.payLoad
                var patientDetails = com.oncobuddy.app.models.pojo.patient_list.PatientDetails()
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

                if(patientDetailsByCGObj.cancerType != null) {
                    var cancerType = com.oncobuddy.app.models.pojo.profile.CancerType()
                    cancerType.id = patientDetailsByCGObj.cancerType.id
                    cancerType.name = patientDetailsByCGObj.cancerType.name
                    patientDetails.cancerType = cancerType
                }

                if(patientDetailsByCGObj.cancerSubType != null) {
                    var cancerSubType = com.oncobuddy.app.models.pojo.profile.CancerType()
                    cancerSubType.id = patientDetailsByCGObj.cancerSubType.id
                    cancerSubType.name = patientDetailsByCGObj.cancerSubType.name
                    patientDetails.cancerSubType = cancerSubType
                }

                var bundle  = Bundle()
                bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                bundle.putSerializable(Constants.PATIENT_DATA,patientDetails)
                Log.d("record_back","from doctor")
                var medicalRecordFragment = NovPatientDetails()
                medicalRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    medicalRecordFragment, this, false
                )

            } else {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Something went wrong while getting patient Details"
                )
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun setOnClickListeners() {

        binding.linHeader.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        CGPersonalInfoFragment(), this, false
                    )
                }else{
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        EditProfileFragment(), this, false
                    )
                }

            }
        })

        binding.linLogout.setOnClickListener {
            if(!isDoubleClick())showLogoutDialog()
            //saveproduct()
        }

        binding.linAccountSettings.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    PatientAccountSettingsFragment(), this, false
                )
            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) fragmentManager?.popBackStack()
        })

        binding.linTerms.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", Constants.TERMS_URL)
                bundle.putString("title",getString(R.string.terms_of_service_simple))

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.linPrivacyPolicy.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", Constants.PRIVACY_POLICY_URL)
                bundle.putString("title",getString(R.string.privacy_policy_simple))

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.linFaq.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString("url", "https://www.google.com")
                bundle.putString("title","FAQ")

                var fragment = NovWebViewFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.cardMyDoctors.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    NovPatientDoctorsListingFragment(), this, false
                )
            }
        })

        binding.cardTrackGR.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if(getUserObject().profile.patientType != null && getUserObject().profile.patientType.equals(Constants.PATIENT_TYPE_TEST_TAKER)){
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
                if(getUserObject().profile.patientType != null && getUserObject().profile.patientType.equals(Constants.PATIENT_TYPE_TEST_TAKER)){
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

        binding.cardMyCC.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    CareCOmpanionFragment(), this, false
                )
            }
        })

        binding.linInvite.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.shareApp(FourBaseCareApp.activityFromApp,"Download Onco buddy app : https://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID)
            }
        })

        binding.linAbout.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.openWebBrowser("https://4basecare.com/about-us/overview/", FourBaseCareApp.activityFromApp)
            }
        })

        binding.linMyCareCode.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    MyCareCode(), this, false
                )
            }
        })

        binding.cardTransactionHistory.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    TransactionHistoryFragment(), this, false
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

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialogInterface, which ->
                        doLogoutProcess()
                    }).setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.show()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            binding.tvName.setText(getUserObject().firstName)
            if(!getUserObject().dpLink.isNullOrEmpty())
                Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink).
                placeholder(R.drawable.ic_profile_circular_white_bg)
                    .error(R.drawable.ic_profile_circular_white_bg).circleCrop().into(binding.ivProfileImage)
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
}