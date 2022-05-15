package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentPatientProfileNewBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContact
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactInput
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactsListResponse
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.DoctorListingAdapter

class PatientProfileFragment : BaseFragment(), DoctorListingAdapter.Interaction {

    private lateinit var binding: FragmentPatientProfileNewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var addEmergencyContactInputDialogue: Dialog
    private lateinit var findDoctorInputDialogue: Dialog
    private lateinit var selectBottomEmergencyContactDialogue: FragmentModalBottomSheet
    private lateinit var emergencyContactList: ArrayList<EmergencyContact>
    private lateinit var allCOntactsList: LiveData<List<EmergencyContact>>
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var userObj : LoginDetails
    private lateinit var globalEmergencyContact: EmergencyContact
    private var deletetedPosition = -1
    private lateinit var selectBottomDoctorDialog: FragmentModalBottomSheet
    private lateinit var contactUsBottomDialog: FragmentModalBottomSheet
    private lateinit var doctorListingAdapter: DoctorListingAdapter
    private var IS_ECRF_AVAILABLE = false
    private lateinit var selectedDoctor: Doctor
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var responseConfirmationDialogue: Dialog



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
            R.layout.fragment_patient_profile_new, container, false
        )
        Log.d("user_id_log",""+getUserIdd())
        setupVM()
        if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
            binding.cardPatientCOntactsCOntainer.visibility = View.GONE
            binding.cardContacts.visibility = View.GONE
            binding.cardViewEvents.visibility = View.GONE
            binding.cardViewDocument.visibility = View.GONE
            binding.tvCancerTypeVal.visibility = View.GONE
            binding.tvSubCancerTypeVal.visibility = View.GONE

            //val layout: RelativeLayout = binding.root.findViewById(R.id.relMainBg)
            val params: ViewGroup.LayoutParams = binding.relMainBg.layoutParams
            params.height = resources.getDimension(R.dimen._220sdp).toInt()
            binding.relMainBg.layoutParams = params

            val params2: ViewGroup.LayoutParams = binding.relHeaderBg.layoutParams
            params.height = resources.getDimension(R.dimen._220sdp).toInt()
            binding.relHeaderBg.layoutParams = params2

        }else{
            getEmergencyContactsFromServer()
        }

        //checkEmergencyContactsInDB()
        setupObservers()
        setClickListeners()
        setProfileData()
        logScreenViewEventMP("Patient Profile screen")
    }

    private fun setProfileData() {
        userObj = getUserObject()
        binding.tvName.setText(userObj.firstName)
        if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
            if(userObj.headline.isNullOrEmpty())binding.tvHeadLine.setText("Care Companion") else binding.tvHeadLine.setText(userObj.headline)
        }else{
            if(userObj.headline.isNullOrEmpty())binding.tvHeadLine.setText("Patient")else binding.tvHeadLine.setText(userObj.headline)
        }

        binding.tvPhoneNumber.setText(userObj.phoneNumber)
        binding.tvEmailVal.setText(userObj.email)
        if(userObj.profile != null && userObj.profile.cancerType != null) {
            binding.tvCancerTypeVal.setText(userObj.profile.cancerType.name)
        }else{
            binding.tvCancerTypeVal.setText("NA")
        }

        if(userObj.profile != null && userObj.profile.cancerSubType != null && !userObj.profile.cancerSubType.name.isNullOrEmpty()) {
            binding.tvSubCancerTypeVal.setText(userObj.profile.cancerSubType.name)
            Log.d("set_profile_log","sub cancer name set "+userObj.profile.cancerSubType.name)
        }else{
            Log.d("set_profile_log","Sub cancer name is null")
            binding.tvSubCancerTypeVal.setText("NA")
        }

        if(!userObj.dpLink.isNullOrEmpty())
        Glide.with(FourBaseCareApp.activityFromApp).load(userObj.dpLink).
        placeholder(R.drawable.ic_profile_circular_white_bg)
            .error(R.drawable.ic_profile_circular_white_bg).circleCrop().into(binding.ivProfileImage)


    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }


    private fun setupObservers() {
        profileViewModel.emeregencyContactsListResonseData.observe(this, responseObserver)
        profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        profileViewModel.addContactResonseData.observe(this,addCOntactObserver)
        profileViewModel.alertContactResonseData.observe(this,alertCOntactsObserver)
        profileViewModel.checkEcrfResonseData.observe(this,checkPatientPdfObserver)
        profileViewModel.deleteContactResonseData.observe(this,deleteCOntactObserver)
        profileViewModel.connectionResonseData.observe(this, connectionResponseObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
        profileViewModel.onCheckEcrfError.observe(this, ecrfErrorObserver)
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())
            fragmentManager?.popBackStack()
        })

      /*  binding.tvAddCareTaker.setOnClickListener(View.OnClickListener {
            val intent = Intent(FourBaseCareApp.activityFromApp, CareTakerActivityNew::class.java)
            intent.putExtra(Constants.USER_ROLE, getUserObject().role)
            intent.putExtra(Constants.SOURCE, "patient_profile")
            startActivity(intent)
            FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        })*/

        binding.cardViewEvents.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(IS_ECRF_AVAILABLE){
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        EcrfEventsListingFragment(), this, false
                    )
                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Patient not found on ECRF.")
                }

                //CommonMethods.showImplementationPending(FourBaseCareApp.activityFromApp)
            }
        })


        binding.cardViewDocument.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(IS_ECRF_AVAILABLE){
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        ViewEcrfFragment(), this, false
                    )
                }else{
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Patient not found on ECRF.")
                }

            }
        })




        binding.linMyDoctors.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())showSelectDoctorDialogue()
        })

        binding.linMyCareGiver.setOnClickListener(View.OnClickListener {
               if(!isDoubleClick()){
                if(checkIFCareCOmpanion()){
                    showToast(FourBaseCareApp.activityFromApp, "As a care giver, You cannot add another care giver")
                }else{
                    /*val intent = Intent(FourBaseCareApp.activityFromApp, CareTakerActivityNew::class.java)
                    intent.putExtra(Constants.USER_ROLE, getUserObject().role)
                    intent.putExtra(Constants.SOURCE, "patient_profile")
                    startActivity(intent)
                    FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)*/
                }

            }
        })


        binding.cardContactUS.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) showContactUsDialogue()
        })

        binding.cardChangePassword.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
               /* val intent = Intent(FourBaseCareApp.activityFromApp, ForgotPasswordActivity::class.java)
                intent.putExtra(Constants.SOURCE, "patient_profile")
                startActivity(intent)
                FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)*/
            }
        })

        binding.cardAppHelp.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                logUserClick("App help button")
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    AppHelpVIdeosFragment(), this, false
                )
            }

        })

        binding.cardABout.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())CommonMethods.openWebBrowser("https://4basecare.com",FourBaseCareApp.activityFromApp)
        })


        binding.cardLogout.setOnClickListener {
            if(!isDoubleClick())showLogoutDialog()
            //saveproduct()
        }



       binding.cardContacts.setOnClickListener(View.OnClickListener {
           if(!isDoubleClick()){
           if(::emergencyContactList.isInitialized && !emergencyContactList.isNullOrEmpty()){
               showSelectOrAddEmergencyDialogue()
           }else{
               showAddMemberInputDialogue()
           }
       }
       })

        binding.ivEdit.setOnClickListener(View.OnClickListener {
              if(!isDoubleClick()){
                  CommonMethods.addNextFragment(
                      FourBaseCareApp.activityFromApp,
                      EditProfileFragment(), this, false
                  )
              }

        })

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

    private fun showSelectOrAddEmergencyDialogue() {
        /*val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_emergency_contacts_list, null)

        selectBottomEmergencyContactDialogue = FragmentModalBottomSheet(myView)
        selectBottomEmergencyContactDialogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )
        val rvDoctors: RecyclerView = myView.findViewById(R.id.rvEmergencyCOntacts)
        val ivAddContact: ImageView = myView.findViewById(R.id.ivAddCOntact)
        val ivHeader: ImageView = myView.findViewById(R.id.ivHeader)

        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_sos_contacts).into(ivHeader)


        rvDoctors.apply {
            *//*layoutManager = LinearLayoutManager(activity)
            emergencyContactAdapter =
                EmergencyContactAdapter(emergencyContactList, this@PatientProfileFragment)
            adapter = emergencyContactAdapter
            emergencyContactAdapter.submitList(emergencyContactList)*//*
        }

        ivAddContact.setOnClickListener(View.OnClickListener {
            selectBottomEmergencyContactDialogue.dismiss()
            if(emergencyContactList.size > 2){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, getString(R.string.you_cannot_add_more_than_3_emergency_contact))
            }else{
                     showAddMemberInputDialogue()
            }
        })
*/

    }

    private fun showAddMemberInputDialogue() {

        addEmergencyContactInputDialogue = Dialog(FourBaseCareApp.activityFromApp)
        addEmergencyContactInputDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addEmergencyContactInputDialogue.setContentView(R.layout.dialogue_add_member)
        addEmergencyContactInputDialogue.setCancelable(false)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(addEmergencyContactInputDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = addEmergencyContactInputDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        addEmergencyContactInputDialogue.window?.attributes = lp
        addEmergencyContactInputDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: TextView = addEmergencyContactInputDialogue.findViewById(R.id.btnYes)

        val btnCancel: TextView = addEmergencyContactInputDialogue.findViewById(R.id.btnNo)

        val edName: EditText = addEmergencyContactInputDialogue.findViewById(R.id.edName)
        val edMobile: EditText = addEmergencyContactInputDialogue.findViewById(R.id.edMobileNumber)
        val edEmail: EditText = addEmergencyContactInputDialogue.findViewById(R.id.edEmail)


        btnYes.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(edName).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_name))
            }else if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            }else if(getTrimmedText(edMobile).trim().length < 10){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_invalid_mobile_number))
            }else if(getTrimmedText(edEmail).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_email))
            }else if(!CommonMethods.isValidEmail(getTrimmedText(edEmail))){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_valid_email))
            }else if(emergencyContactList != null && !emergencyContactList.isNullOrEmpty()) {
                var isFound = false
                for(emergencyContact in emergencyContactList){
                    if(emergencyContact.phoneNumber.equals(getTrimmedText(edMobile))){
                        isFound = true
                    }
                }
                if(isFound){
                    showToast(FourBaseCareApp.activityFromApp, "This mobile number already has been added")
                }else{
                    addEmergencyContactInputDialogue.dismiss()
                    addEmergencyContact(getTrimmedText(edEmail), getTrimmedText(edMobile), getTrimmedText(edName))
                }
            } else{
                addEmergencyContactInputDialogue.dismiss()
                addEmergencyContact(getTrimmedText(edEmail), getTrimmedText(edMobile), getTrimmedText(edName))
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            addEmergencyContactInputDialogue.dismiss()

        })

        addEmergencyContactInputDialogue.show()
    }

    private fun showFindDoctorInputDialogue() {

        findDoctorInputDialogue = Dialog(FourBaseCareApp.activityFromApp)
        findDoctorInputDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        findDoctorInputDialogue.setContentView(R.layout.dialogue_add_doctor_name)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(findDoctorInputDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = findDoctorInputDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findDoctorInputDialogue.window?.attributes = lp
        findDoctorInputDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: TextView = findDoctorInputDialogue.findViewById(R.id.btnYes)

        val btnCancel: TextView = findDoctorInputDialogue.findViewById(R.id.btnNo)

        val edMobile: EditText = findDoctorInputDialogue.findViewById(R.id.edMobileNumber)

        btnYes.setOnClickListener(View.OnClickListener {
           if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            }else{
               findDoctorInputDialogue.dismiss()
               CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                findDoctor(getTrimmedText(edMobile))
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            findDoctorInputDialogue.dismiss()

        })

        findDoctorInputDialogue.show()
    }

 /*   override fun onItemSelected(position: Int, item: EmergencyContact, view: View) {
        if(view.id == R.id.ivDelete){
            deletetedPosition = position
            globalEmergencyContact = item
            deleteContact(""+item.id)
            selectBottomEmergencyContactDialogue.dismiss()

        }
    }*/

    private fun addEmergencyContact(emailId: String, phoneNumber: String, name: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("emergency_contact","1")
            var emergencyContact = EmergencyContactInput()
            emergencyContact.emailAddress = emailId
            emergencyContact.phoneNumber = phoneNumber
            emergencyContact.fullName = name
            emergencyContact.mobileCode = "+91"

            globalEmergencyContact = EmergencyContact()
            globalEmergencyContact.emailAddress = emailId
            globalEmergencyContact.phoneNumber = phoneNumber
            globalEmergencyContact.fullName = name
            globalEmergencyContact.mobileCode = "+91"


            Log.d("emergency_contact_log","Inserting new data in Server")

            profileViewModel.addEmergencyContact(
                emergencyContact,
                getUserAuthToken()
            )
            Log.d("emergency_contact","2")

        }
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            profileViewModel.findDoctor(false,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("emergency_contact","2")

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



    private fun alertContacts() {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.alertEmergencyContact(
                getUserAuthToken()
            )

        }
    }

    private fun deleteContact(emergencyContactId:String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.deleteEmergencyContact(
                getUserAuthToken(), emergencyContactId
            )

        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val ecrfErrorObserver = androidx.lifecycle.Observer<String>{message ->
        IS_ECRF_AVAILABLE = false
        binding.tvGetEvents.setTextColor(getResourceColor(R.color.font_gray))
        binding.tvEcrfPdf.setTextColor(getResourceColor(R.color.font_gray))
    }

    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        //binding.loginModel = loginResponseData

        Log.d("api_log", "doctor list Result " + responseObserver.isSuccess)
        if (responseObserver.doctorList == null) {
            Log.d("api_log", "doctor list null ")
        } else {
            Log.d("api_log", "doctor list size " + responseObserver.doctorList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()
        doctorList = ArrayList()
        if (responseObserver.isSuccess) {
            doctorList = ArrayList()
            doctorList.addAll(responseObserver.doctorList)

        }

    }

    private val responseObserver =
        androidx.lifecycle.Observer<EmergencyContactsListResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()
            emergencyContactList = ArrayList()
            emergencyContactList.addAll(responseObserver?.payLoad!!)

            Log.d("emergency_contact_log","Got data from")
            /*for(emergencyCOntact in emergencyContactList){
                saveContactInDb(emergencyCOntact)
            }*/
        }

    private val connectionResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    getEmergencyContactsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
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
                    assignDoctor(""+responseObserver.payLoad.id)
                }else{
                     showNoDoctorFOundDialogue(""+responseObserver.message)
                    //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
                }
            }else{
                showNoDoctorFOundDialogue(""+responseObserver?.message)
                //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
            }


        }
    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    profileViewModel.callGetMyDoctorListing(getUserObject().role,false,
                        getUserAuthToken(), ""+getUserIdd()
                    )
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "Doctor not found!")
                }
            }
        }

    private val localDbDataObserver =
        androidx.lifecycle.Observer<List<EmergencyContact>>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()
            emergencyContactList = ArrayList()
            emergencyContactList.addAll(responseObserver)

            if(emergencyContactList.isNullOrEmpty()){
                Log.d("emergency_contact_log","No data found in local DB, Getting from server...")
                getEmergencyContactsFromServer()
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


    private fun getEmergencyContactsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getEmergencyContactsList(
                getUserAuthToken()
            )
            profileViewModel.callGetMyDoctorListing("",false,
                getUserAuthToken(), ""+getUserIdd()
            )

            profileViewModel.checkPatientECRF(getUserAuthToken(),""+getUserIdd())
        }
    }

    private val addCOntactObserver = Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Contact added successfully!"
            )
            getEmergencyContactsFromServer()
          /*  //checkEmergencyContactsInDB()
            globalEmergencyContact.id ="8"
            saveContactInDb(globalEmergencyContact)*/
            //Log.d("emergency_contact_log","Added new data in the database ")

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
            )
        }


    }

    private val checkPatientPdfObserver = Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            //binding.cardViewEvents.visibility = View.VISIBLE
            //binding.cardViewDocument.visibility = View.VISIBLE
            IS_ECRF_AVAILABLE = true
        } else {
            //binding.cardViewEvents.visibility = View.GONE
            //binding.cardViewDocument.visibility = View.GONE
        }


    }

    private val alertCOntactsObserver = Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "All contacts notified successfully!"
            )

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
            )
        }


    }

    private val deleteCOntactObserver = Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Contact deleted successfully!"
            )
            emergencyContactList.remove(globalEmergencyContact)
            getEmergencyContactsFromServer()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
            )
        }


    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            Log.d("resume_log","Called")
            setProfileData()
        }
    }

    private fun showContactUsDialogue() {
       /* val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_contact_us_dialogue, null)

        contactUsBottomDialog = FragmentModalBottomSheet(myView)
        contactUsBottomDialog.isCancelable = true

        contactUsBottomDialog.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linCall: LinearLayout = myView.findViewById(R.id.linCall)
        val linEmail: LinearLayout = myView.findViewById(R.id.linEmail)
        val ivHeader: ImageView = myView.findViewById(R.id.ivHeader)

        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_contact_us_new).into(ivHeader)


        linCall.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+918105171596")
            startActivity(intent)
            contactUsBottomDialog.dismiss()
        })

        linEmail.setOnClickListener(View.OnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:info@4basecare.com")
            }
            startActivity(Intent.createChooser(emailIntent, "Application query"))
            contactUsBottomDialog.dismiss()
        })
*/


    }

        private fun showSelectDoctorDialogue() {
       /* val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_emergency_contacts_list, null)

        selectBottomDoctorDialog = FragmentModalBottomSheet(myView)
        selectBottomDoctorDialog.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )
        val rvDoctors: RecyclerView = myView.findViewById(R.id.rvEmergencyCOntacts)
        val ivAddContact: ImageView = myView.findViewById(R.id.ivAddCOntact)
        val ivHeader : ImageView = myView.findViewById(R.id.ivHeader)
        val tvHeaderTitle : TextView = myView.findViewById(R.id.tvHeaderTitle)

        ivHeader.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_circular_blue_doctor))
        tvHeaderTitle.setText(getString(R.string.doctors))

        if(!::doctorList.isInitialized || doctorList == null){
            doctorList = ArrayList()
        }

        rvDoctors.apply {
            layoutManager = LinearLayoutManager(activity)
            doctorListingAdapter =
                DoctorListingAdapter(doctorList, this@PatientProfileFragment,false,true)
            adapter = doctorListingAdapter
            doctorListingAdapter.submitList(doctorList)
        }

        ivAddContact.setOnClickListener(View.OnClickListener {
            selectBottomDoctorDialog.dismiss()
            //showAddDoctorInputDialogue()
            showFindDoctorInputDialogue()
        //CommonMethods.showImplementationPending(FourBaseCareApp.activityFromApp)
        })*/
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

            if(::selectBottomDoctorDialog.isInitialized) selectBottomDoctorDialog.dismiss()

            selectedDoctor = Doctor()
            selectedDoctor.firstName = doctorDetails.firstName
            selectedDoctor.doctorId = doctorDetails.doctorId
            selectedDoctor.displayPicUrl = doctorDetails.dpLink
            selectedDoctor.designation = doctorDetails.designation
            selectedDoctor.verified = doctorDetails.isVerified
            selectedDoctor.consultationFee = doctorDetails.consultationFee.toDouble()

            Log.d("selected_dr_log","0.0 id "+doctorDetails.doctorId)
            Log.d("selected_dr_log","0 id "+selectedDoctor.doctorId)
            Log.d("selected_dr_log","0 fees "+selectedDoctor.firstName)
            if(shouldBookAppointment){
                var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                var bundle = Bundle()
                bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                addOrEditAppointmentFragment.arguments = bundle
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addOrEditAppointmentFragment, this, false
                )
            }else{
                assignDoctor(""+doctorDetails.doctorId)
            }

        })

        Log.d("details_log","5")
        doctorDetailsDialogue.show()
    }

    override fun onItemSelected(position: Int, item: Doctor, view: View) {
        if(view.id == R.id.relImage){
            // dplink, name, specialization, designation, hospital name, experience,verified
                if(item.isRequestPending != null && item.isRequestPending){

                }else{
                    var doctorDetails = com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails()
                    doctorDetails.doctorId = item.doctorId
                    doctorDetails.consultationFee = item.consultationFee.toInt()
                    doctorDetails.firstName = item.firstName
                    doctorDetails.dpLink = item.displayPicUrl
                    doctorDetails.specialization = item.specialization
                    doctorDetails.designation = item.designation
                    doctorDetails.hospital = item.hospital
                    doctorDetails.isVerified = item.verified
                    showDoctorDetailsDialogue(doctorDetails, true)
                }

        }else if(view.id == R.id.ivYes){
            Log.d("doctor_response_log","Yes")
            showResponseConfirmDialogue(""+item.doctorId,true)
        }else if(view.id == R.id.ivNo){
            Log.d("doctor_response_log","No")
            showResponseConfirmDialogue(""+item.doctorId,false)
        }
    }

    private fun showResponseConfirmDialogue(patientId: String,isAccepted: Boolean) {
        responseConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        responseConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(responseConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = responseConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        responseConfirmationDialogue.window?.setAttributes(lp)
        responseConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: Button = responseConfirmationDialogue.findViewById(R.id.btnDelete)
        val btnCancel: Button = responseConfirmationDialogue.findViewById(R.id.btnCancel)
        val tvMsg: TextView = responseConfirmationDialogue.findViewById(R.id.tvMsg)

        btnDelete.setText("Yes")
        btnCancel.setText("No")
        if(isAccepted){
            tvMsg.setText("Are you sure you want to accept connection with this doctor?")
        }else{
            tvMsg.setText("Are you sure you do not want to accept connection for this doctor?")
        }


        btnDelete.setOnClickListener(View.OnClickListener {
            doConnectionResponse(patientId,isAccepted)
            responseConfirmationDialogue.dismiss()
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            responseConfirmationDialogue.dismiss()
        })

        responseConfirmationDialogue.show()
    }

    private fun doConnectionResponse(patientId: String, isAccepted: Boolean) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            selectBottomDoctorDialog.dismiss()
            profileViewModel.responseCOnnectionRequest(getUserAuthToken(),patientId, false,isAccepted)
            Log.d("connection_log", "API called")
        }
    }




}