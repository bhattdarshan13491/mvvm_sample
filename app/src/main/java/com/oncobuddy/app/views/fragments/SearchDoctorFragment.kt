package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentSearchDoctorBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.invite.InviteInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.NovDoctorDetailsActivity
import com.oncobuddy.app.views.activities.QRScanActivity

class SearchDoctorFragment : BaseFragment() {

    private lateinit var binding: FragmentSearchDoctorBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var forumsViewModel: ForumsViewModel
    private var IS_DOCTOR_SEARCHED = false
    private var doctorDetailsId = ""
    private val CAMERA_PERMISSION_REQUEST_CODE = 1

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
            R.layout.fragment_search_doctor, container, false
        )

        Log.d("search_dr","Opened")
        setupVM()
        setupObservers()
        setCLickListeners()
        showHideInvitationViews(false)
        setTitle()
    }

    private fun setTitle() {
        binding.layoutHeader.tvTitle.setText("Search Doctors")
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)

    }

    private fun setupObservers() {
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
        profileViewModel.inviteResonseData.observe(this, inviteDoctorResponseObserver)

        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)

        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun openQRCOdeActivity() {
        val intent = Intent(FourBaseCareApp.activityFromApp, QRScanActivity::class.java)
        startActivityForResult(intent, Constants.QR_SCAN_RESULT)
    }

    private fun checkPermissionForCamera(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.CAMERA
        )
        return resultCamera == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openQRCOdeActivity()
                } else {
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp,getString(R.string.msg_allow_permission))
                }
                return
            }
        }
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                FourBaseCareApp.activityFromApp,
                Manifest.permission.CAMERA
            )
        ) {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                R.string.permissions_needed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                FourBaseCareApp.activityFromApp,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun setCLickListeners() {
        binding.ivQRCode.setOnClickListener(View.OnClickListener {
            if(checkPermissionForCamera()){
                openQRCOdeActivity()
            }else{
                requestPermissionForCameraAndMicrophone()
            }
        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivSearch.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(binding.etDoctorMobileNumber).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp,"Please enter mobile number")
            }else if(getTrimmedText(binding.etDoctorMobileNumber).length != 10){
                showToast(FourBaseCareApp.activityFromApp,"Mobile number should contain exact 10 digits")
            }else{
                IS_DOCTOR_SEARCHED = true
                findDoctor(getTrimmedText(binding.etDoctorMobileNumber))
            }
        })

        binding.tvSave.setOnClickListener(View.OnClickListener {
            if(IS_DOCTOR_SEARCHED){
                if(CommonMethods.getTrimmedText(binding.etDoctorMobileNumber).isNullOrEmpty()){
                    showToast(FourBaseCareApp.activityFromApp,"Please enter mobile number to send invitation")
                }else if(getTrimmedText(binding.etDoctorMobileNumber).length != 10){
                    showToast(FourBaseCareApp.activityFromApp,"Mobile number should contain exact 10 digits")
                }
                else{
                    inviteDoctor()
                    /*FORM_STAGE = 2
                    setFormPerStep()*/
                }
            }else{
                showToast(FourBaseCareApp.activityFromApp,"Please search doctor either by using QR or using mobile number.")
            }
        })

    }

    private fun inviteDoctor() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","1")

            var inviteInput = InviteInput()
            inviteInput.phoneNumber = getTrimmedText(binding.etDoctorMobileNumber)
            inviteInput.email = getTrimmedText(binding.etEmail)
            inviteInput.name = getTrimmedText(binding.etFullName)
            profileViewModel.callDOctorInvite(getUserAuthToken(), inviteInput)


        }
    }

    private fun showHideInvitationViews(shouldSHow: Boolean){
        if(shouldSHow){
            binding.relName.visibility = View.VISIBLE
            binding.relEmail.visibility = View.VISIBLE
            binding.tvInstruction.visibility = View.VISIBLE
            binding.tvSave.visibility = View.VISIBLE
        }else{
            binding.relName.visibility = View.GONE
            binding.relEmail.visibility = View.GONE
            binding.tvInstruction.visibility = View.GONE
            binding.tvSave.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("qr_log","request "+requestCode+" result "+resultCode)
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.QR_SCAN_RESULT){
                if (!data?.getStringExtra("qr_code").isNullOrEmpty()) {
                    Log.d("qr_log","qr code "+data?.getStringExtra("qr_code"))
                    getDoctorDetails(""+data?.getStringExtra("qr_code"))
                }

            }else if(requestCode == Constants.DOCTOR_DETAILS_RESULT){
                if (data?.getBooleanExtra("should_assign", false) == true) {
                    Log.d("qr_log","should_assign "+data?.getBooleanExtra("should_assign", false))
                    assignDoctor(""+doctorDetailsId)
                }
            }
        }
    }

    private fun getDoctorDetails(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun showNoDoctorFOundDialogue(message: String) {
        android.app.AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle("No doctor found!")
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    dialogInterface.dismiss()
                })
            .show()
    }


    private fun assignDoctor(doctorId: String) {
        Log.d("assign_log","0.1")
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("assign_log","1")
            profileViewModel.assignDoctor(false,
                doctorId,
                getUserAuthToken()
            )
            Log.d("assign_log","2")

        }
    }

    // observers

    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                     fragmentManager?.popBackStack()
                /*  var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                      var bundle = Bundle()
                      bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                      addOrEditAppointmentFragment.arguments = bundle
                      CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)*/
                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Doctor not found!")
                }
            }



        }

    private val findDoctorResponseObserver =
        androidx.lifecycle.Observer<FindDoctorResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.isSuccess){
                    //showToast(FourBaseCareApp.activityFromApp, "Doctor found! Assigning doctor to you!")
                    Log.d("assign_log","0 "+responseObserver.payLoad.id)
                    getDoctorDetails(""+responseObserver.payLoad.id)
                }else{
                    showHideInvitationViews(true)
                    showNoDoctorFOundDialogue(""+responseObserver.message)
                    //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
                }
            }else{
                showHideInvitationViews(true)
                showNoDoctorFOundDialogue(""+responseObserver?.message)
                //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
            }


        }

    private val inviteDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp,"Invitation has been sent successfully!")
                    fragmentManager?.popBackStack()

                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Failed to send invitation!")
                }
            }
        }

    private val doctorDetailsResponseObserver = androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            doctorDetailsId = ""+responseObserver.payLoad.doctorId
            val intent = Intent(FourBaseCareApp.activityFromApp, NovDoctorDetailsActivity::class.java)
            intent.putExtra(Constants.DOCTOR_DATA, responseObserver.payLoad)
            startActivityForResult(intent, Constants.DOCTOR_DETAILS_RESULT)
            FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
            //showDoctorDetailsDialogue(responseObserver.payLoad)
        }else{
            showToast(FourBaseCareApp.activityFromApp,"Something went wrong while getting doctor details!")
        }

    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            profileViewModel.findDoctor(false,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("find_doctor","2")

        }
    }


}