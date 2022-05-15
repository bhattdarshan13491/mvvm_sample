package com.oncobuddy.app.views.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
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
import com.oncobuddy.app.models.pojo.care_giver_details.AssignCGInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.invite.InviteInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.login_response.Profile
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetails
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetailsByCGResponse
import com.oncobuddy.app.models.pojo.profile.CancerType
import com.oncobuddy.app.models.pojo.profile.CancerTypesListResponse
import com.oncobuddy.app.models.pojo.registration_process.AppUser
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.CancerSpinerAdapter
import kotlinx.android.synthetic.main.activity_nov_account_setup_activity.*
import kotlinx.android.synthetic.main.activity_nov_cg_account_setup_activity.*
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import kotlinx.android.synthetic.main.layout_cancer_selection.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Nov c g account setup activity
 * THis screen comes after the basic account creation by the care giver
 * @constructor Create empty Nov c g account setup activity
 */


class NovCGAccountSetupActivity : BaseActivity() {

    private lateinit var binding: ActivityNovCgAccountSetupActivityBinding
    private val CAMERA_PERMISSION_REQUEST_CODE = 1

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var forumsViewModel: ForumsViewModel

    private lateinit var cancerTypesList: ArrayList<CancerType>
    private lateinit var cancerSubTypesList: ArrayList<CancerType>
    private lateinit var cancerSpinnerAdapter: CancerSpinerAdapter
    private lateinit var subCancerSpinnerAdapter: CancerSpinerAdapter
    private lateinit var patientProfileDetails: PatientDetails
    private var FORM_STAGE = 0
    private var cancerTypeId = 0L
    private var cancerSubTypeId = 0L
    private var selectedCancerType = CancerType()
    private var selectedSUbCancerType = CancerType()
    private var ADDED_CARE_GIVER = false
    private var IS_DOCTOR_ASSIGNED = false
    private var IS_DOCTOR_SEARCHED = false
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var deleteConfirmationDialogue: Dialog
    private var doctorDetailsId = ""
    private var addedCGId = ""
    private var IS_FIRST_TIME = true
    private var IS_PATIENT_FOUND = false


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
        binding = DataBindingUtil.setContentView(
            this@NovCGAccountSetupActivity,
            R.layout.activity_nov_cg_account_setup_activity
        )
        setupVM()
        setupObservers()
        FORM_STAGE = 0
        setFormPerStep()
        setupCLickListeners()
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
        cancerSpinnerAdapter =
            CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
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
        subCancerSpinnerAdapter =
            CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
        binding.spPrimaryLocation.adapter = subCancerSpinnerAdapter
    }

    private fun getCancerSubTypesFromServer(cancerTypeId: String) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getCSubancerTypes(
                getUserAuthToken(), cancerTypeId
            )
        }
    }

    private fun isValidMobileNoInput(): Boolean {
        if (getTrimmedText(binding.etPatientMobileNumber).isNullOrEmpty()) {
            showToast(getString(R.string.validation_enter_mobile_number))
            return false
        } else if (getTrimmedText(binding.etPatientMobileNumber).trim().length < 10) {
            showToast(getString(R.string.validation_invalid_mobile_number))
            return false
        }
        return true
    }

    private fun assignPatientTOCG() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidMobileNoInput()) {
            Log.d("patient_found","patient found "+IS_PATIENT_FOUND)
            if(!IS_PATIENT_FOUND){
                showToast("Patient not found on the system. Please provide a valid care code.")
            } else if (getTrimmedText(binding.etPatientCareCode).isNullOrEmpty()) {
                showToast("Please enter patient code")
            } else if (spRelationType.selectedItemPosition == 0) {
                showToast("Please select relationship")
            } else {
                val relArray: Array<String> =
                    getResources().getStringArray(R.array.relationship_type)
                var assignInput = AssignCGInput()
                assignInput.careCode = getTrimmedText(binding.etPatientCareCode)
                assignInput.relationshipType =
                    CommonMethods.returnRelationSHipEnum(relArray.get(spRelationType.selectedItemPosition))
                profileViewModel.callAssignPatientToCG(getUserAuthToken(), assignInput)
            }


        }
    }

    private fun setupObservers() {
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.patientDetailsByCGData.observe(this, patientDetailsObserver)
        profileViewModel.assignPatientToCGData.observe(this, assignPatientResponseObserver)
        profileViewModel.cancerTypesListResonseData.observe(this, cancerTypesListObserver)
        profileViewModel.cancerSubTypesListResonseData.observe(this, cancerTypesSubListObserver)
        profileViewModel.addCareCompanionResponse.observe(this, addCareTakerObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.inviteResonseData.observe(this, inviteDoctorResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)

        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
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

    private val doctorDetailsResponseObserver =
        androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            Log.d("details_log", "3")
            if (responseObserver.isSuccess) {
                Log.d("details_log", "4")
                doctorDetailsId = "" + responseObserver.payLoad.doctorId
                val intent =
                    Intent(this@NovCGAccountSetupActivity, NovDoctorDetailsActivity::class.java)
                intent.putExtra(Constants.DOCTOR_DATA, responseObserver.payLoad)
                startActivityForResult(intent, Constants.DOCTOR_DETAILS_RESULT)
                overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
                //showDoctorDetailsDialogue(responseObserver.payLoad)
            } else {
                showToast("Something went wrong while getting doctor details!")
            }

        }

    private val cancerTypesListObserver = Observer<CancerTypesListResponse> { responseObserver ->
        cancerTypesList = ArrayList()
        if (responseObserver.success) {
            Log.d("cancer_types", "0")
            var cancerType = CancerType()
            cancerType.id = 0
            cancerType.name = "Select Primary Cancer Site"
            cancerTypesList.add(cancerType)
            Log.d("cancer_types", "aDDED " + cancerTypesList.size)
            cancerTypesList.addAll(responseObserver.payLoad)
            Log.d("cancer_types", "aDDED ALL")
            val array = arrayOfNulls<CancerType>(cancerTypesList.size)
            cancerTypesList.toArray(array)

            cancerSpinnerAdapter =
                CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
            binding.spPrmarySite.adapter = cancerSpinnerAdapter

            Log.d("cancer_types", "cancer type if found "+cancerTypeId)
            if (cancerTypeId != 0L) {
                var itemPosition = 0
                for (cancerItem in cancerTypesList) {
                    Log.d("match_log", "cancer type " + cancerItem.name)
                    Log.d("match_log", "cancer type " + cancerItem.id)
                    if (cancerTypeId == cancerItem.id.toLong()) {
                        Log.d("match_log", "cancer type matched " + cancerItem.name)
                        itemPosition = cancerTypesList.indexOf(cancerItem)
                    }
                }
                binding.spPrmarySite.setSelection(itemPosition)
            }

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


    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?> { responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if (responseObserver.success) {
                    IS_DOCTOR_ASSIGNED = true
                    CommonMethods.showToast(this@NovCGAccountSetupActivity, "Request sent")
                    CommonMethods.changeActivity(
                        this@NovCGAccountSetupActivity,
                        NovIntroductionScreensActivity::class.java,
                        true
                    )
                    /*    FORM_STAGE = 2
                        setFormPerStep()*/
                    /*  var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                      var bundle = Bundle()
                      bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                      addOrEditAppointmentFragment.arguments = bundle
                      CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)*/
                } else {
                    CommonMethods.showToast(this@NovCGAccountSetupActivity, "Doctor not found!")
                }
            }
        }

    private val inviteDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?> { responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if (responseObserver.success) {
                    showToast("Invitation has been sent successfully!")
                    /*FORM_STAGE = 2
                    setFormPerStep()*/
                    CommonMethods.changeActivity(
                        this@NovCGAccountSetupActivity,
                        NovIntroductionScreensActivity::class.java,
                        true
                    )
                    /*  var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                      var bundle = Bundle()
                      bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                      addOrEditAppointmentFragment.arguments = bundle
                      CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)*/
                } else {
                    CommonMethods.showToast(this@NovCGAccountSetupActivity, "Doctor not found!")
                }
            }
        }

    private val assignPatientResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?> { responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()
            Log.d("patient_found","1")
            if (responseObserver != null) {
                if (responseObserver.success) {
                    Log.d("care_code","saved "+getTrimmedText(binding.etPatientCareCode))
                    var userObj = getUserObject()
                    userObj.careCode = getTrimmedText(binding.etPatientCareCode)
                    userObj.patientConnected = true
                    val gson = Gson()
                    val userStr = gson.toJson(userObj)
                    FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)
                    showToast("" + responseObserver.message)
                    FORM_STAGE = 1
                    setFormPerStep()
                    //getCancerTypesFromServer()

                } else {
                    CommonMethods.showToast(this@NovCGAccountSetupActivity, "Doctor not found!")
                }
            }
        }


    private val findDoctorResponseObserver =
        androidx.lifecycle.Observer<FindDoctorResponse?> { responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if (responseObserver.isSuccess) {
                    //showToast(FourBaseCareApp.activityFromApp, "Doctor found! Assigning doctor to you!")
                    Log.d("assign_log", "0 " + responseObserver.payLoad.id)
                    getDoctorDetails("" + responseObserver.payLoad.id)
                } else {
                    showHideInvitationViews(true)
                    showNoDoctorFOundDialogue("" + responseObserver.message)
                    //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
                }
            } else {
                showHideInvitationViews(true)
                showNoDoctorFOundDialogue("" + responseObserver?.message)
                //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
            }


        }


    private val addCareTakerObserver =
        androidx.lifecycle.Observer<AddCareTakerResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.isSuccess) {
                CommonMethods.showToast(
                    this@NovCGAccountSetupActivity,
                    "Care giver added successfully!"
                )
                addedCGId = "" + responseObserver.payLoad.id
                if (!ADDED_CARE_GIVER) {
                    setupCGTwoView()
                } else {
                    CommonMethods.changeActivity(
                        this@NovCGAccountSetupActivity,
                        NovIntroductionScreensActivity::class.java,
                        true
                    )
                }
            } else {
                CommonMethods.showToast(
                    this@NovCGAccountSetupActivity,
                    "Something went wrong while adding care giver"
                )
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val patientDetailsObserver =
        androidx.lifecycle.Observer<PatientDetailsByCGResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            Log.d("patient_found","0")
            if (responseObserver.isSuccess) {
                Log.d("patient_found","true")
                IS_PATIENT_FOUND = true
                patientProfileDetails = responseObserver.payLoad
                binding.etPatientName.setText(patientProfileDetails.firstName)
                binding.etPatientMobileNumber.setText(patientProfileDetails.phoneNumber)

                if (responseObserver.payLoad.cancerType != null) {
                    cancerTypeId = responseObserver.payLoad.cancerType.id.toLong()
                    Log.d("cancer_types", "cancer type if found 2 "+cancerTypeId)
                    Log.d("cancer_stage_log", "set data " + cancerTypeId)
                }
                if (responseObserver.payLoad.cancerType != null) {
                    cancerSubTypeId = responseObserver.payLoad.cancerSubType.id.toLong()
                    Log.d("cancer_stage_log", "set sub data " + cancerSubTypeId)

                }
                getCancerTypesFromServer()

            } else {
                Log.d("patient_found","false")
                IS_PATIENT_FOUND = false
                CommonMethods.showToast(
                    this@NovCGAccountSetupActivity,
                    "Something went wrong while getting patient Details"
                )
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        }


    private val updateProfileObserver = Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            if (FORM_STAGE == 1) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Saved")
                Log.d("form_log", "1")
                var userObj = getUserObject()
                userObj.cancerTypeId = cancerTypeId
                userObj.cancerSubTypeId = cancerSubTypeId

                var profile = Profile()

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
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)


                Log.d("form_log", "2")
                FORM_STAGE = 2
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
            //cancerSubTypesList = ArrayList()
            if (responseObserver.payLoad != null && responseObserver.payLoad.size > 0) {

                cancerSubTypesList = ArrayList()
                var cancerSubType = CancerType()
                cancerSubType.id = 0
                cancerSubType.name = "Select Primary Cancer Location"
                cancerSubTypesList.add(cancerSubType)
                cancerSubTypesList.addAll(responseObserver.payLoad)
                val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
                cancerSubTypesList.toArray(array)

                subCancerSpinnerAdapter = CancerSpinerAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_spinner,
                    array
                )
                binding.spPrimaryLocation.adapter = subCancerSpinnerAdapter

                Log.d("cancer_stage_log", "After subtype loading data " + cancerSubTypeId)
                if (cancerSubTypeId != 0L) {
                    var itemPosition = 0
                    for (cancerSubItem in cancerSubTypesList) {
                        Log.d("match_log", "sub cancer type " + cancerSubItem.name)
                        Log.d("match_log", "sub cancer type " + cancerSubItem.id)
                        if (cancerSubTypeId == cancerSubItem.id.toLong()) {
                            Log.d("match_log", "cancer type matched")
                            itemPosition = cancerSubTypesList.indexOf(cancerSubItem)
                        }
                    }
                    binding.spPrimaryLocation.setSelection(itemPosition)
                }
            } else {
                cancerSubTypeId = 0L
                cancerSubTypesList = ArrayList()
                var cancerSubType = CancerType()
                cancerSubType.id = 0
                cancerSubType.name = "Select Primary Cancer Location"
                cancerSubTypesList.add(cancerSubType)
                val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
                cancerSubTypesList.toArray(array)
                subCancerSpinnerAdapter = CancerSpinerAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_spinner,
                    array
                )
                binding.spPrimaryLocation.adapter = subCancerSpinnerAdapter

                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.no_sub_type_found)
                )
            }
        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
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
                this@NovCGAccountSetupActivity,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            IS_DOCTOR_SEARCHED = true
            profileViewModel.findDoctor(
                false,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("find_doctor", "2")

        }
    }


    private fun setupCLickListeners() {

        binding.ivPatientSearch.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (getTrimmedText(binding.etPatientCareCode).isNullOrBlank()) {
                    showToast("Please enter patient care code")
                } else {
                    if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                        Log.d("details_log", "1")
                        profileViewModel.callGetPatientDetailsByCG(
                            getUserAuthToken(),
                            getTrimmedText(binding.etPatientCareCode)
                        )
                        binding.etPatientMobileNumber.setText("")
                        binding.etPatientName.setText("")
                    }
                }
            }

        })

        binding.tvSkip.setOnClickListener(View.OnClickListener {
            if (FORM_STAGE == 0) {
                FORM_STAGE = 1
                setFormPerStep()
            } else if (FORM_STAGE == 1) {
                FORM_STAGE = 2
                setFormPerStep()
            } else {
                CommonMethods.changeActivity(
                    this@NovCGAccountSetupActivity,
                    NovIntroductionScreensActivity::class.java,
                    true
                )
            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            gotoPreviousScreen()
        })

        binding.ivSearch.setOnClickListener(View.OnClickListener {
            if (getTrimmedText(binding.etDoctorMobileNumber).isNullOrEmpty()) {
                showToast("Please enter mobile number")
            } else if (getTrimmedText(binding.etDoctorMobileNumber).length != 10) {
                showToast("Mobile number should contain exact 10 digits")
            } else {
                findDoctor(getTrimmedText(binding.etDoctorMobileNumber))

            }
        })

        /*    binding.layoutcardCareGIver.ivCardMenu.setOnClickListener(View.OnClickListener {
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
            })*/

        binding.ivQRCode.setOnClickListener(View.OnClickListener {
            if (checkPermissionForCamera()) {
                openQRCOdeActivity()
            } else {
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
                Log.d("cancer_types", "onitem selected 0 pos "+position)
                if (::cancerTypesList.isInitialized) {

                    if (position != 0) {
                        Log.d("cancer_types", "selected pos " + position)
                        Log.d("cancer_types", "cancer id " + cancerTypesList.get(position).id)
                        Log.d("cancer_types", "cancer name " + cancerTypesList.get(position).name)
                        cancerTypeId = cancerTypesList.get(position).id.toLong()
                        selectedCancerType = cancerTypesList.get(position)
                        getCancerSubTypesFromServer(cancerTypeId.toString())
                        Log.d("cancer_log", "cancer type " + cancerTypeId)
                    }

                    /*selectedCancerType = cancerTypesList.get(position)
                    cancerTypeId = cancerTypesList.get(position).id.toLong()
                    Log.d("cancer_types", "cancer type if found 3 "+cancerTypeId)
                    getCancerSubTypesFromServer("" + cancerTypeId)*/
                } else {
                    cancerTypeId = 0L
                    Log.d("cancer_types", "cancer type if found 0 "+cancerTypeId)
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
                if (::cancerSubTypesList.isInitialized && !IS_FIRST_TIME) {
                    cancerSubTypeId = cancerSubTypesList.get(position).id.toLong()
                    selectedSUbCancerType = cancerSubTypesList.get(position)
                    Log.d("cancer_stage_log", "After primary location spinner " + cancerSubTypeId)
                    //getCancerSubTypesFromServer(cancerTypeId.toString())
                    Log.d("cancer_log", "cancer sub type " + cancerSubTypeId)
                } else {
                    IS_FIRST_TIME = false
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
            binding.spRelationType.performClick()
        })

        binding.ivDropDownCGTwo.setOnClickListener(View.OnClickListener {
            binding.spCGTwoRelationShip.performClick()
        })

        binding.relContinue.setOnClickListener(View.OnClickListener {
            if (FORM_STAGE == 0) {
                assignPatientTOCG()
            } else if (FORM_STAGE == 1) {
                if (isValidCancerInput()) {
                    updateCancerInfo()
                }
            } else {
                Log.d("continue_log", "0")
                if (IS_DOCTOR_ASSIGNED) {
                    /*      FORM_STAGE = 2
                          Log.d("continue_log","1")
                          setFormPerStep()*/
                    CommonMethods.changeActivity(
                        this@NovCGAccountSetupActivity,
                        NovIntroductionScreensActivity::class.java,
                        true
                    )
                } else {
                    if (IS_DOCTOR_SEARCHED) {
                        Log.d("continue_log", "2")
                        if (CommonMethods.getTrimmedText(binding.etDoctorMobileNumber)
                                .isNullOrEmpty()
                        ) {
                            showToast("Please enter mobile number to send invitation")
                        } else if (getTrimmedText(binding.etDoctorMobileNumber).length != 10) {
                            showToast("Mobile number should contain exact 10 digits")
                        } else {
                            Log.d("continue_log", "3")
                            inviteDoctor()

                            /*FORM_STAGE = 2
                            setFormPerStep()*/
                        }
                    } else {
                        Log.d("continue_log", "4")
                        showToast("Please search doctor either by using QR or using mobile number.")
                    }
                }
            }
        })

        binding.relAddMore.setOnClickListener(View.OnClickListener {
            binding.relAddMore.visibility = View.GONE
            binding.linCGTwo.visibility = View.VISIBLE
        })
    }

    private fun openQRCOdeActivity() {
        val intent = Intent(this@NovCGAccountSetupActivity, QRScanActivity::class.java)
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
        if (cancerSubTypeId != 0L) registrationInput.cancerSubTypeId = cancerSubTypeId

        var appUser = RegistrationInput()
        appUser.userId = getUserObject().userIdd
        appUser.appUser = registrationInput
        appUser.cancerTypeId = cancerTypeId
        if (cancerSubTypeId != 0L) appUser.cancerSubTypeId = cancerSubTypeId
        profileViewModel.updateProfile(getUserAuthToken(), appUser, null, Constants.ROLE_PATIENT)
    }


    private fun setupCGTwoView() {
        val relArray: Array<String> = getResources().getStringArray(R.array.relationship_type)
        binding.linCGOne.visibility = View.GONE
        //binding.layoutcardCareGIver.relCardCG.visibility = View.VISIBLE
        binding.relAddMore.visibility = View.VISIBLE
        //binding.layoutcardCareGIver.tvName.text = getTrimmedText(binding.etCGOneName)
        /*binding.layoutcardCareGIver.tvRelation.text =
            relArray.get(spCGOneRelationShip.selectedItemPosition)*/
        ADDED_CARE_GIVER = true

    }


    private fun setFormPerStep() {
        Log.d("form_log", "Form step " + FORM_STAGE)
        if (FORM_STAGE == 0) {
            binding.tvCurrentStep.setText("Patient Details")
            binding.tvNextStep.setText("Next: Medical History")
            binding.linPatientDetails.visibility = View.VISIBLE
            binding.linMedicalHistory.visibility = View.GONE
            binding.linDoctorDetails.visibility = View.GONE
            binding.tvSkip.visibility = View.GONE
            //binding.linCGDetails.visibility = View.GONE
            Glide.with(this@NovCGAccountSetupActivity).load(R.drawable.ic_step_one)
                .into(binding.ivStep)
        } else if (FORM_STAGE == 1) {
            binding.tvCurrentStep.setText("Medical History")
            binding.tvNextStep.setText("Next: Doctor Details")
            binding.linPatientDetails.visibility = View.GONE
            binding.linMedicalHistory.visibility = View.VISIBLE
            binding.linDoctorDetails.visibility =
                View.GONE//binding.linCGDetails.visibility = View.GONE
            Glide.with(this@NovCGAccountSetupActivity).load(R.drawable.ic_step_two)
                .into(binding.ivStep)
            binding.tvSkip.visibility = View.VISIBLE

        } else if (FORM_STAGE == 2) {
            binding.tvCurrentStep.setText("Doctor Details")
            binding.tvNextStep.setText("Next: Get Started")
            binding.linPatientDetails.visibility = View.GONE
            binding.linMedicalHistory.visibility = View.GONE
            binding.linDoctorDetails.visibility = View.VISIBLE
            //binding.linCGDetails.visibility = View.VISIBLE
            Glide.with(this@NovCGAccountSetupActivity).load(R.drawable.ic_step_three)
                .into(binding.ivStep)
            /*  Glide.with(this@NovAccountSetupActivity).load(R.drawable.ic_tick_green_bg)
                    .into(binding.ivStepTwo)*/
            binding.tvSkip.visibility = View.VISIBLE

        }

    }

    private fun isValidCancerInput(): Boolean {
        if (cancerTypeId == 0L) {
            showToast("Please select cancer type")
            return false
        }
        return true
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("qr_log", "request " + requestCode + " result " + resultCode)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.QR_SCAN_RESULT) {
                if (!data?.getStringExtra("qr_code").isNullOrEmpty()) {
                    Log.d("qr_log", "qr code " + data?.getStringExtra("qr_code"))
                    getDoctorDetails("" + data?.getStringExtra("qr_code"))
                }

            } else if (requestCode == Constants.DOCTOR_DETAILS_RESULT) {
                if (data?.getBooleanExtra("should_assign", false) == true) {
                    Log.d(
                        "qr_log",
                        "should_assign " + data?.getBooleanExtra("should_assign", false)
                    )
                    assignDoctor("" + doctorDetailsId)
                }
            }
        }
    }

    private fun getDoctorDetails(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log", "1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun inviteDoctor() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log", "1")

            var inviteInput = InviteInput()
            inviteInput.phoneNumber = getTrimmedText(binding.etDoctorMobileNumber)
            inviteInput.email = getTrimmedText(binding.etEmail)
            inviteInput.name = getTrimmedText(binding.etFullName)
            profileViewModel.callDOctorInvite(getUserAuthToken(), inviteInput)


        }
    }

    private fun showDoctorDetailsDialogue(
        doctorDetails: com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails,
        shouldBookAppointment: Boolean = false
    ) {

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


        if (!doctorDetails.dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(doctorDetails.dpLink)
                .placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        tvName.setText(doctorDetails.firstName)
        tvDesignation.setText(doctorDetails.designation)
        tvSpecialization.setText(doctorDetails.specialization)
        //tvExperience.setText("05 years")
        if (doctorDetails.experience != null) {
            tvExperience.setText(CommonMethods.getStringWithOnePadding(doctorDetails.experience) + " years")
        } else {
            linExp.visibility = View.GONE
        }
        tvHospitalName.setText(doctorDetails.hospital)
        if (doctorDetails.isVerified) {
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified)
                .into(ivVerified)
        } else {
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified)
                .into(ivVerified)
        }
        tvAddDoctor.setOnClickListener(View.OnClickListener {
            Log.d("details_log", "6")
            doctorDetailsDialogue.dismiss()
            doctorDetailsDialogue.hide()

        })

        Log.d("details_log", "5")
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
        deleteConfirmationDialogue = Dialog(this@NovCGAccountSetupActivity)
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
        val tvMsg: TextView = deleteConfirmationDialogue.findViewById(R.id.tvMsg)

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
        if (checkInterNetConnection(this@NovCGAccountSetupActivity)) {
            profileViewModel.deleteCareGiver(getUserAuthToken(), addedCGId)
        }
    }


    private fun assignDoctor(doctorId: String) {
        Log.d("assign_log", "0.1")
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("assign_log", "1")
            profileViewModel.assignDoctor(
                false,
                doctorId,
                getUserAuthToken()
            )
            Log.d("assign_log", "2")

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

    private fun showHideInvitationViews(shouldSHow: Boolean) {
        if (shouldSHow) {
            binding.relName.visibility = View.VISIBLE
            binding.relEmail.visibility = View.VISIBLE
            binding.tvInstruction.visibility = View.VISIBLE
        } else {
            binding.relName.visibility = View.GONE
            binding.relEmail.visibility = View.GONE
            binding.tvInstruction.visibility = View.GONE
        }
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@NovCGAccountSetupActivity,
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
                this@NovCGAccountSetupActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissionForCamera(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            this@NovCGAccountSetupActivity,
            Manifest.permission.CAMERA
        )
        return resultCamera == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openQRCOdeActivity()
                } else {
                    CommonMethods.showToast(
                        this@NovCGAccountSetupActivity,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
        }
    }
}