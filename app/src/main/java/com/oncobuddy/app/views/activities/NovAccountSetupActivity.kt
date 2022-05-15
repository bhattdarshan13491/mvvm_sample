package com.oncobuddy.app.views.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.invite.InviteInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.login_response.Profile
import com.oncobuddy.app.models.pojo.profile.CancerType
import com.oncobuddy.app.models.pojo.profile.CancerTypesListResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.models.pojo.registration_process.AppUser
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.CancerSpinerAdapter
import kotlinx.android.synthetic.main.activity_nov_account_setup_activity.*
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Nov account setup activity
 * THis screen comes after the basic account creation by the patient
 * Takes all needed details like cancer type, doctor details and care giver details from the patient
 * @constructor Create empty Nov account setup activity
 */
class NovAccountSetupActivity : BaseActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    private lateinit var binding : ActivityNovAccountSetupActivityBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var cancerTypesList: ArrayList<CancerType>
    private lateinit var cancerSubTypesList: ArrayList<CancerType>
    private lateinit var cancerSpinnerAdapter: CancerSpinerAdapter
    private lateinit var subCancerSpinnerAdapter: CancerSpinerAdapter
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var deleteConfirmationDialogue: Dialog

    private var FORM_STAGE = 0
    private var cancerTypeId = 0L
    private var cancerSubTypeId = 0L
    private var ADDED_CARE_GIVER = false
    private var IS_DOCTOR_ASSIGNED = false
    private var IS_DOCTOR_SEARCHED = false
    private var doctorDetailsId = ""
    private var addedCGId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FourBaseCareApp.activityFromApp = this
        init()
    }

    override fun onResume() {
        super.onResume()
        FourBaseCareApp.activityFromApp = this
    }



    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovAccountSetupActivity, R.layout.activity_nov_account_setup_activity)
        setupVM()
        setupObservers()
        FORM_STAGE = 0
        setFormPerStep()
        setupCLickListeners()
        getCancerTypesFromServer()
        Log.d("dob_log", "token initial " + getUserObject().authToken)
        showHideInvitationViews(false)

        initializeFIrstSpinner()
        initializeCancerSUbTypes()


    }

    private fun initializeFIrstSpinner() {
        cancerTypesList = ArrayList()
        var cancerType = CancerType()
        cancerType.id = 0
        cancerType.name = "Select Primary Cancer Site"
        cancerTypesList.add(cancerType)
        val array = arrayOfNulls<CancerType>(cancerTypesList.size)
        cancerTypesList.toArray(array)
        cancerSpinnerAdapter = CancerSpinerAdapter(FourBaseCareApp.activityFromApp,R.layout.raw_spinner, array)
        binding.spPrmarySite.adapter = cancerSpinnerAdapter

    }

    private fun initializeCancerSUbTypes() {
        cancerSubTypesList = ArrayList()
        var cancerType = CancerType()
        cancerType.id = 0
        cancerType.name = "Primary Cancer location"
        cancerSubTypesList.add(cancerType)
        val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
        cancerSubTypesList.toArray(array)
        subCancerSpinnerAdapter = CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
        binding.spPrimaryLocation.adapter = subCancerSpinnerAdapter
    }

    private fun getCancerSubTypesFromServer(cancerTypeId: String) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getCSubancerTypes(
                getUserAuthToken(), cancerTypeId
            )
        }
    }

    private fun setupObservers() {
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.cancerTypesListResonseData.observe(this, cancerTypesListObserver)
        profileViewModel.cancerSubTypesListResonseData.observe(this, cancerTypesSubListObserver)
        profileViewModel.addCareCompanionResponse.observe(this, addCareTakerObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.deleteCGResonseData.observe(this,removeCareTakerObserver)
        profileViewModel.inviteResonseData.observe(this, inviteDoctorResponseObserver)

        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)

        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)

        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
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

    private val doctorDetailsResponseObserver = androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            doctorDetailsId = ""+responseObserver.payLoad.doctorId
            val intent = Intent(this@NovAccountSetupActivity, NovDoctorDetailsActivity::class.java)
            intent.putExtra(Constants.DOCTOR_DATA, responseObserver.payLoad)
            startActivityForResult(intent, Constants.DOCTOR_DETAILS_RESULT)
            overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
            //showDoctorDetailsDialogue(responseObserver.payLoad)
        }else{
            showToast("Something went wrong while getting doctor details!")
        }

    }

    private val cancerTypesListObserver = Observer<CancerTypesListResponse> { responseObserver ->
        cancerTypesList = ArrayList()
        if (responseObserver.success) {
            Log.d("cancer_types","0")
            var cancerType = CancerType()
            cancerType.id = 0
            cancerType.name = "Select Primary Cancer Site"
            cancerTypesList.add(cancerType)
            Log.d("cancer_types","aDDED "+cancerTypesList.size)
            cancerTypesList.addAll(responseObserver.payLoad)
            Log.d("cancer_types","aDDED ALL")
            val array = arrayOfNulls<CancerType>(cancerTypesList.size)
            cancerTypesList.toArray(array)

            cancerSpinnerAdapter = CancerSpinerAdapter(FourBaseCareApp.activityFromApp,R.layout.raw_spinner, array)
            binding.spPrmarySite.adapter = cancerSpinnerAdapter


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding cancer type"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()

    }

    // observers

    private val removeCareTakerObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(
                this@NovAccountSetupActivity,
                "Care giver removed successfully!"
            )
            ADDED_CARE_GIVER = false
            setupCGOneView()
        }
        else
            CommonMethods.showToast(this@NovAccountSetupActivity, "Something went wrong while removing care giver/")


        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    IS_DOCTOR_ASSIGNED = true
                    CommonMethods.showToast(this@NovAccountSetupActivity, "Request sent")
                    FORM_STAGE = 2
                    setFormPerStep()
                  /*  var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                    var bundle = Bundle()
                    bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                    addOrEditAppointmentFragment.arguments = bundle
                    CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)*/
                }else{
                    CommonMethods.showToast(this@NovAccountSetupActivity, "Doctor not found!")
                }
            }
        }

    private val inviteDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast("Invitation has been sent successfully!")
                    FORM_STAGE = 2
                    setFormPerStep()
                    /*  var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                      var bundle = Bundle()
                      bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                      addOrEditAppointmentFragment.arguments = bundle
                      CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)*/
                }else{
                    CommonMethods.showToast(this@NovAccountSetupActivity, "Doctor not found!")
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


    private val addCareTakerObserver = androidx.lifecycle.Observer<AddCareTakerResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.isSuccess){
            CommonMethods.showToast(this@NovAccountSetupActivity, "Care giver added successfully!")
            addedCGId = ""+responseObserver.payLoad.id
            if(!ADDED_CARE_GIVER){
                setupCGTwoView()
            }else{
                CommonMethods.changeActivity(this@NovAccountSetupActivity, NovIntroductionScreensActivity::class.java, true)
            }
        }else{
            CommonMethods.showToast(this@NovAccountSetupActivity, "Something went wrong while adding care giver")
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val updateProfileObserver = Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            if(FORM_STAGE == 0){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Saved")
                Log.d("form_log","1")
                var userObj = getUserObject()
                userObj.cancerTypeId = cancerTypeId
                userObj.cancerSubTypeId = cancerSubTypeId

                var profile  = Profile()

                var cancerType = com.oncobuddy.app.models.pojo.login_response.CancerType()
                cancerType.id = cancerTypeId
                cancerType.name = cancerTypesList.get(0).name

                var subCancerType = com.oncobuddy.app.models.pojo.login_response.CancerType()
                subCancerType.id = cancerSubTypeId
                subCancerType.name = cancerTypesList.get(0).name

                profile.cancerType = cancerType
                profile.cancerSubType = subCancerType
                userObj.profile = profile


                val gson = Gson()
                val userStr = gson.toJson(userObj)
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ,userStr)


                Log.d("form_log","2")
                FORM_STAGE = 1
                setFormPerStep()


            }
        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()

    }

    private val cancerTypesSubListObserver = Observer<CancerTypesListResponse> { responseObserver ->
        if (responseObserver.success) {
            cancerSubTypesList = ArrayList()
            var cancerType = CancerType()
            cancerType.id = 0
            cancerType.name = "Select primary cancer location"
            cancerSubTypesList.add(cancerType)
            if(responseObserver.payLoad != null && responseObserver.payLoad.size > 0){
                cancerSubTypesList.addAll(responseObserver.payLoad)
            }else{
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, getString(R.string.no_sub_type_found))
            }
            val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
            cancerSubTypesList.toArray(array)
            subCancerSpinnerAdapter = CancerSpinerAdapter(FourBaseCareApp.activityFromApp,R.layout.raw_spinner, array)
            binding.spPrimaryLocation.adapter = subCancerSpinnerAdapter

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }



    private fun getCancerTypesFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.getCancerTypes(
                        getUserAuthToken()
                    )
                }
            }
        } else {
            Toast.makeText(
                this@NovAccountSetupActivity,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            IS_DOCTOR_SEARCHED = true
            profileViewModel.findDoctor(false,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("find_doctor","2")

        }
    }




    private fun setupCLickListeners() {

        binding.tvSkip.setOnClickListener(View.OnClickListener {
            if (FORM_STAGE == 0) {
                FORM_STAGE = 1
                setFormPerStep()
            }else if(FORM_STAGE == 1){
                FORM_STAGE = 2
                setFormPerStep()
            }else{
                CommonMethods.changeActivity(this@NovAccountSetupActivity, NovIntroductionScreensActivity::class.java, true)
            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            gotoPreviousScreen()
        })

        binding.ivSearch.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(binding.etDoctorMobileNumber).isNullOrEmpty()){
                showToast("Please enter mobile number")
            }else if(getTrimmedText(binding.etDoctorMobileNumber).length != 10){
                showToast("Mobile number should contain exact 10 digits")
            }else{
                       findDoctor(getTrimmedText(binding.etDoctorMobileNumber))
            }
        })

        binding.layoutcardCareGIver.ivCardMenu.setOnClickListener(View.OnClickListener {
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, binding.layoutcardCareGIver.ivCardMenu)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_care_giver_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_delete -> {
                        //showDeleteConfirmDialogue(item)
                        showDeleteCareGiverDialogue()
                    }
                }
                true
            }

            // Showing the popup menu
            popupMenu.show()
        })

        binding.ivQRCode.setOnClickListener(View.OnClickListener {
            if(checkPermissionForCamera()){
                openQRCOdeActivity()
            }else{
                requestPermissionForCameraAndMicrophone()
            }

        })


        binding.spPrmarySite.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                    if(::cancerTypesList.isInitialized && position > 0){
                        Log.d("cancer_type","selected pos "+position)
                        Log.d("cancer_type","cancer id "+cancerTypesList.get(position).id)
                        Log.d("cancer_type","cancer name "+cancerTypesList.get(position).name)
                        cancerTypeId = cancerTypesList.get(position).id.toLong()
                        getCancerSubTypesFromServer(""+cancerTypeId)
                    }else{
                        cancerTypeId =  0L
                    }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.spPrimaryLocation.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if(::cancerSubTypesList.isInitialized && position > 0){
                    Log.d("cancer_type","sub selected pos "+position)
                    Log.d("cancer_type","sub cancer id "+cancerSubTypesList.get(position).id)
                    Log.d("cancer_type","sub cancer name "+cancerSubTypesList.get(position).name)
                    cancerSubTypeId = cancerSubTypesList.get(position).id.toLong()

                }else{
                    cancerSubTypeId =  0L
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.ivDropDownOne.setOnClickListener(View.OnClickListener {
            binding.spPrmarySite.performClick()
        })

        binding.ivDropDownTwo.setOnClickListener(View.OnClickListener {
            binding.spPrimaryLocation.performClick()
        })

        binding.ivDropDownCGOne.setOnClickListener(View.OnClickListener {
            binding.spCGOneRelationShip.performClick()
        })

        binding.ivDropDownCGTwo.setOnClickListener(View.OnClickListener {
            binding.spCGTwoRelationShip.performClick()
        })

        binding.relContinue.setOnClickListener(View.OnClickListener {
            if (FORM_STAGE == 0) {
                //FORM_STAGE = 1
                    if(isValidCancerInput()){
                        updateCancerInfo()
                    }

            } else if (FORM_STAGE == 1) {
                if(IS_DOCTOR_ASSIGNED){
                    FORM_STAGE = 2
                    setFormPerStep()
                }else{
                    if(IS_DOCTOR_SEARCHED){
                        if(CommonMethods.getTrimmedText(binding.etDoctorMobileNumber).isNullOrEmpty()){
                            showToast("Please enter mobile number to send invitation")
                        }else if(getTrimmedText(binding.etDoctorMobileNumber).length != 10){
                            showToast("Mobile number should contain exact 10 digits")
                        }
                        else{
                            inviteDoctor()

                            /*FORM_STAGE = 2
                            setFormPerStep()*/
                        }
                    }else{
                        showToast("Please search doctor either by using QR or using mobile number.")
                    }
                }

            } else {
                addCareTaker()
                //showToast("Everything done")
            }
        })

        binding.relAddMore.setOnClickListener(View.OnClickListener {
            binding.relAddMore.visibility = View.GONE
            binding.linCGTwo.visibility = View.VISIBLE
        })
    }

    private fun openQRCOdeActivity() {
        val intent = Intent(this@NovAccountSetupActivity, QRScanActivity::class.java)
        startActivityForResult(intent, Constants.QR_SCAN_RESULT)
    }

    private fun updateCancerInfo() {
        var registrationInput = AppUser()
        registrationInput.firstName = getUserObject().firstName
        registrationInput.lastName = ""
        registrationInput.headline = ""
        //registrationInput.email = ""+getUserObject().email
        registrationInput.phoneNumber = "" + getUserObject().phoneNumber

        /*if(!getUserObject().email.isNullOrEmpty()){
                            if(!getUserObject().email.equals(getTrimmedText(binding.etEmail))){
                                registrationInput.email = getTrimmedText(binding.etEmail)
                            }
                        }else{
                            registrationInput.email = getTrimmedText(binding.etEmail)
                        }*/

        registrationInput.phoneNumber = getUserObject().phoneNumber
        registrationInput.cancerTypeId = cancerTypeId
        if(cancerSubTypeId != 0L )registrationInput.cancerSubTypeId = cancerSubTypeId

        var appUser = RegistrationInput()
        appUser.userId =getUserObject().userIdd
        appUser.appUser = registrationInput
        appUser.cancerTypeId = cancerTypeId
        if(cancerSubTypeId != 0L)appUser.cancerSubTypeId = cancerSubTypeId
        profileViewModel.updateProfile(getUserAuthToken(), appUser, null, Constants.ROLE_PATIENT)
    }


    private fun addCareTaker() {
        if (!isDoubleClick() && checkInterNetConnection(this@NovAccountSetupActivity)) {

            val relArray: Array<String> = getResources().getStringArray(R.array.relationship_type)


            if(!ADDED_CARE_GIVER){
                if(isValidCGInput()){
                    var addCareCompanionInput = AddCareCompanionInput()
                    addCareCompanionInput.name = getTrimmedText(binding.etCGOneName)
                    addCareCompanionInput.mobileNumber = getTrimmedText(binding.etMobileNumberCG1)
                    addCareCompanionInput.relationship = CommonMethods.returnRelationSHipEnum(relArray.get(spCGOneRelationShip.selectedItemPosition))
                    profileViewModel.addCareCompanion(addCareCompanionInput, getUserObject().authToken, Constants.ROLE_PATIENT)
                }

            }else{
                if(binding.linCGTwo.visibility == View.VISIBLE){
                    if(isValidCGTwoInput()){
                        var addCareCompanionInput = AddCareCompanionInput()
                        addCareCompanionInput.name = getTrimmedText(binding.etCGTwoName)
                        addCareCompanionInput.mobileNumber = getTrimmedText(binding.etMobileNumberCGTwo)
                        addCareCompanionInput.relationship = CommonMethods.returnRelationSHipEnum(relArray.get(spCGTwoRelationShip.selectedItemPosition))
                        profileViewModel.addCareCompanion(addCareCompanionInput, getUserObject().authToken, Constants.ROLE_PATIENT)
                    }
                }else{
                    CommonMethods.changeActivity(this@NovAccountSetupActivity, NovIntroductionScreensActivity::class.java, true)
                }

            }


        }
    }

    private fun setupCGTwoView() {
        val relArray: Array<String> = getResources().getStringArray(R.array.relationship_type)
        binding.linCGOne.visibility = View.GONE
        binding.layoutcardCareGIver.relCardCG.visibility = View.VISIBLE
        binding.relAddMore.visibility = View.VISIBLE
        binding.layoutcardCareGIver.tvName.text = getTrimmedText(binding.etCGOneName)
        binding.layoutcardCareGIver.tvRelation.text =
            relArray.get(spCGOneRelationShip.selectedItemPosition)
        ADDED_CARE_GIVER = true

    }

    private fun setupCGOneView() {
        val relArray: Array<String> = getResources().getStringArray(R.array.relationship_type)
        binding.linCGOne.visibility = View.VISIBLE
        binding.layoutcardCareGIver.relCardCG.visibility = View.GONE
        binding.relAddMore.visibility = View.GONE
        binding.linCGTwo.visibility = View.GONE
        binding.etCGOneName.setText("")
        binding.etMobileNumberCG1.setText("")
        binding.spCGOneRelationShip.setSelection(0)
        ADDED_CARE_GIVER = false
    }

    private fun setFormPerStep() {
        Log.d("form_log","Form step "+FORM_STAGE)
        if(FORM_STAGE == 0){
            binding.tvCurrentStep.setText("Patient Details")
            binding.tvNextStep.setText("Next: Doctor Details")
            binding.linMedicalHistory.visibility = View.VISIBLE
            binding.linDoctorDetails.visibility = View.GONE
            binding.linCGDetails.visibility = View.GONE
            Glide.with(this@NovAccountSetupActivity).load(R.drawable.ic_step_one).into(binding.ivStep)
        }else if(FORM_STAGE == 1){
            binding.tvCurrentStep.setText("Doctor Details")
            binding.tvNextStep.setText("Next: Care Giver Details")
            binding.linMedicalHistory.visibility = View.GONE
            binding.linDoctorDetails.visibility = View.VISIBLE
            binding.linCGDetails.visibility = View.GONE
            Glide.with(this@NovAccountSetupActivity).load(R.drawable.ic_step_two).into(binding.ivStep)
        }else if(FORM_STAGE == 2){
            binding.tvCurrentStep.setText("Care Giver Details")
            binding.tvNextStep.setText("Next: Get Started")
            binding.linMedicalHistory.visibility = View.GONE
            binding.linDoctorDetails.visibility = View.GONE
            binding.linCGDetails.visibility = View.VISIBLE
            Glide.with(this@NovAccountSetupActivity).load(R.drawable.ic_step_three).into(binding.ivStep)
        /*  Glide.with(this@NovAccountSetupActivity).load(R.drawable.ic_tick_green_bg)
                .into(binding.ivStepTwo)*/
        }

    }

    private fun isValidCancerInput() : Boolean{
        if(cancerTypeId == 0L){
            showToast("Please select cancer type")
            return  false
        }
        return true
    }

    private fun isValidCGInput() : Boolean {

        if(getTrimmedText(binding.etCGOneName).isNullOrBlank()) {
            showToast("Please enter care giver name")
            return false
        }
        else if(getTrimmedText(binding.etMobileNumberCG1).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        }
        else if(getTrimmedText(binding.etMobileNumberCG1).toString().trim().length < 10){
            showToast(getString(R.string.validation_invalid_mobile_number))
            return  false
        }
        else if(!ADDED_CARE_GIVER && getUserObject() !=null && !getUserObject().phoneNumber.isNullOrEmpty() &&
            getTrimmedText(binding.etMobileNumberCG1).equals(getUserObject().phoneNumber)){
            showToast("Your mobile number cannot be added as a care giver number")
            return  false
        }
        return true
    }

    private fun isValidCGTwoInput() : Boolean {

        if(getTrimmedText(binding.etCGTwoName).isNullOrBlank()) {
            showToast("Please enter care giver name")
            return false
        }
        else if(getTrimmedText(binding.etMobileNumberCGTwo).isNullOrBlank()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        }
        else if(getTrimmedText(binding.etMobileNumberCGTwo).toString().trim().length < 10){
            showToast(getString(R.string.validation_invalid_mobile_number))
            return  false
        }
        else if(getUserObject() !=null && !getUserObject().phoneNumber.isNullOrEmpty() && getTrimmedText(binding.etMobileNumberCGTwo)
                .equals(getUserObject().phoneNumber)){
            showToast("Your mobile number cannot be added as a care giver number")
            return  false
        } else if(getTrimmedText(binding.etMobileNumberCG1).equals(getTrimmedText(binding.etMobileNumberCGTwo))){
            showToast("You have already added this mobile number!")
            return  false
        }else if(ADDED_CARE_GIVER && getUserObject() !=null && !getUserObject().phoneNumber.isNullOrEmpty() && getTrimmedText(binding.etMobileNumberCGTwo)
                .equals(getUserObject().phoneNumber)){
            showToast("Your mobile number cannot be added as a care giver number")
            return  false
        } else if(ADDED_CARE_GIVER && getTrimmedText(binding.etMobileNumberCG1).equals(getTrimmedText(binding.etMobileNumberCGTwo))){
            showToast("You have already added this mobile number!")
            return  false
        } else if(binding.spCGTwoRelationShip.selectedItemPosition == 0){
            showToast("Please select relationship type")
            return false
        }
        return true
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

    private fun showDoctorDetailsDialogue(doctorDetails: com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails, shouldBookAppointment: Boolean = false) {

        doctorDetailsDialogue = Dialog(FourBaseCareApp.activityFromApp)
        doctorDetailsDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        doctorDetailsDialogue.setContentView(R.layout.dialogue_doctor_details)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(doctorDetailsDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = doctorDetailsDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        doctorDetailsDialogue.window?.attributes = lp
        doctorDetailsDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val ivProfilePic: ImageView = doctorDetailsDialogue.findViewById(R.id.ivDoctorImage)
        val tvName: TextView = doctorDetailsDialogue.findViewById(R.id.tvDoctorName)
        val tvSpecialization: TextView = doctorDetailsDialogue.findViewById(R.id.tvSpecialization)
        val tvDesignation: TextView = doctorDetailsDialogue.findViewById(R.id.tvDesignation)
        val tvHospitalName: TextView = doctorDetailsDialogue.findViewById(R.id.tvHospitalName)
        val tvExperience: TextView = doctorDetailsDialogue.findViewById(R.id.tvExp)
        val tvAddDoctor: TextView = doctorDetailsDialogue.findViewById(R.id.tvAddDoctor)
        val ivVerified: ImageView = doctorDetailsDialogue.findViewById(R.id.ivVerified)
        val linExp: LinearLayout = doctorDetailsDialogue.findViewById(R.id.linExp)

        tvAddDoctor.setText("Book an appointment")


        if(!doctorDetails.dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(doctorDetails.dpLink).placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        tvName.setText(doctorDetails.firstName)
        tvDesignation.setText(doctorDetails.designation)
        tvSpecialization.setText(doctorDetails.specialization)
        //tvExperience.setText("05 years")
        if(doctorDetails.experience != null){
            tvExperience.setText(CommonMethods.getStringWithOnePadding(doctorDetails.experience)+" years")
        }else{
            linExp.visibility = View.GONE
        }
        tvHospitalName.setText(doctorDetails.hospital)
        if(doctorDetails.isVerified){
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(ivVerified)
        }else{
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(ivVerified)
        }
        tvAddDoctor.setOnClickListener(View.OnClickListener {
            Log.d("details_log","6")
            doctorDetailsDialogue.dismiss()
            doctorDetailsDialogue.hide()

        })

        Log.d("details_log","5")
        doctorDetailsDialogue.show()
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

    private fun showDeleteCareGiverDialogue() {
        deleteConfirmationDialogue = Dialog(this@NovAccountSetupActivity)
        deleteConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(deleteConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = deleteConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        deleteConfirmationDialogue.window?.setAttributes(lp)
        deleteConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = deleteConfirmationDialogue.findViewById(R.id.btnDelete)
        val tvMsg:TextView = deleteConfirmationDialogue.findViewById(R.id.tvMsg)

        tvMsg.setText("Are you sure you want to delete care giver?")


        btnDelete.setOnClickListener(View.OnClickListener {
            removeCareGiver()
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
        //showToast("SHpwing dialogue")
    }

    private fun removeCareGiver() {
        if (checkInterNetConnection(this@NovAccountSetupActivity)) {
            profileViewModel.deleteCareGiver(getUserAuthToken(),addedCGId)
        }
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

    override fun onBackPressed() {
        gotoPreviousScreen()
    }

    private fun gotoPreviousScreen() {
        if (FORM_STAGE == 2) {
            FORM_STAGE = 1
            setFormPerStep()
        } else if (FORM_STAGE == 1) {
            FORM_STAGE = 0
            setFormPerStep()
        } else {
            finish()
            overridePendingTransition(R.anim.anim_right_out, R.anim.anim_left_in)
        }
    }

    private fun showHideInvitationViews(shouldSHow: Boolean){
        if(shouldSHow){
            binding.relName.visibility = View.VISIBLE
            binding.relEmail.visibility = View.VISIBLE
            binding.tvInstruction.visibility = View.VISIBLE
        }else{
            binding.relName.visibility = View.GONE
            binding.relEmail.visibility = View.GONE
            binding.tvInstruction.visibility = View.GONE
        }
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@NovAccountSetupActivity,
                Manifest.permission.CAMERA
            )
        ) {
            Toast.makeText(
                applicationContext,
                R.string.permissions_needed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@NovAccountSetupActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissionForCamera(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            this@NovAccountSetupActivity,
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
                    CommonMethods.showToast(this@NovAccountSetupActivity,getString(R.string.msg_allow_permission))
                }
                return
            }
        }
    }
}