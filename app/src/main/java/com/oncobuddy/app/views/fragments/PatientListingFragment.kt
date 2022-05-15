package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentPatientsListBinding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.care_giver_details.AddUserByCCInput
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails
import com.oncobuddy.app.models.pojo.patient_list.PatientsListResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.PatientAdapter
import kotlinx.android.synthetic.main.dialogue_add_user.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class PatientListingFragment : BaseFragment(), PatientAdapter.Interaction{

    private lateinit var binding : FragmentPatientsListBinding
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var patientListAdapter: PatientAdapter
    private lateinit var findPatientInputDialogue: Dialog
    private lateinit var deleteConfirmationDialogue: Dialog
    private lateinit var responseConfirmationDialogue: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_patients_list, container, false
        )
        setBottomSectionVisibility()
        if(arguments!= null){

        if(arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
            if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)) {
                binding.ivBack.visibility = View.GONE
            }else{
                binding.ivBack.visibility = View.VISIBLE
            }
        }

        }
        binding.edSearchDoctor.setHint("Search your patient")
        binding.edSearchDoctor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                if(::patientListAdapter.isInitialized)
                patientListAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        setupVM()
        getAppointmentsFromServer()
        setupObservers()
        setClickListeners()
    }

    private fun getAppointmentsFromServer() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                var searchQueryInput = SearchQueryInput()
                searchQueryInput.pageSize = 500
                searchQueryInput.reqPageNo = 0
                searchQueryInput.searchKey = ""
                searchQueryInput.sortBy = "id"
                searchQueryInput.sortOrder = "Asc"
                if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
                    appointmentViewModel.callGetPatientList(Constants.ROLE_CARE_COMPANION,getUserAuthToken(), searchQueryInput)
                }else{
                    appointmentViewModel.callGetPatientList(Constants.ROLE_PATIENT, getUserAuthToken(), searchQueryInput)
                }
            }

        }else {
            Toast.makeText(FourBaseCareApp.activityFromApp,getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupObservers() {
        appointmentViewModel.patientListResponseData.observe(this,responseObserver)
        appointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(this, errorMessageObserver)


        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.connectionResonseData.observe(this, connectionResponseObserver)
        profileViewModel.addUserByCCResonseData.observe(this,addDoctorResponseObserver)
        profileViewModel.isViewLoading.observe(this,isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val findDoctorResponseObserver =
        androidx.lifecycle.Observer<FindDoctorResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.isSuccess){
                    //showToast(FourBaseCareApp.activityFromApp, "Patient found! Assigning patient to you!")
                    Log.d("assign_log","0 "+responseObserver.payLoad.id)
                    assignDoctor(true,""+responseObserver.payLoad.id)
                }else{
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
                }
            }else{
                showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
            }


        }
    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    getAppointmentsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }

    private val connectionResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    getAppointmentsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }

    private val deletePatientResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, "Patient deleted successfully!")
                    getAppointmentsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }


    private val responseObserver = Observer<PatientsListResponse?>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver != null) {
            if(responseObserver.success && !responseObserver.payLoad.isNullOrEmpty()){
                showHideNoData(false)
                setRecyclerView(responseObserver.payLoad.asReversed())
            }else{
                showHideNoData(true)
            }
        }else{
            showHideNoData(true)
        }


    }

    private val addDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, "Patient added successfully!")
                    getAppointmentsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("assign_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val isAssignViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("assign_log_", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun showAddPatientByCCInputDialogue() {

        findPatientInputDialogue = Dialog(FourBaseCareApp.activityFromApp)
        findPatientInputDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        findPatientInputDialogue.setContentView(R.layout.dialogue_add_user)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(findPatientInputDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = findPatientInputDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findPatientInputDialogue.window?.attributes = lp
        findPatientInputDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: TextView = findPatientInputDialogue.findViewById(R.id.btnYes)

        val btnCancel: TextView = findPatientInputDialogue.findViewById(R.id.btnNo)

        val edName: EditText = findPatientInputDialogue.findViewById(R.id.edName)
        edName.setHint("Please enter patient name")
        val edMobile: EditText = findPatientInputDialogue.findViewById(R.id.edMobileNumber)

        val tvTitle : TextView = findPatientInputDialogue.findViewById(R.id.tvTitle)
        tvTitle.setText("Add Patient")

        btnYes.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(edName).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_name))
            }
            else if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            }else{
                if(getTrimmedText(edMobile).isNullOrBlank()){
                    showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
                }else{
                    findPatientInputDialogue.dismiss()
                    CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                    addDoctor(getTrimmedText(edName),getTrimmedText(edMobile))
                    CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                }

                //findDoctor(getTrimmedText(edMobile))
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            findPatientInputDialogue.dismiss()

        })

        findPatientInputDialogue.show()
    }

    private fun addDoctor(name : String, phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            var addUserInput = AddUserByCCInput()
            addUserInput.name = name
            addUserInput.mobileNumber = phoneNumber
            profileViewModel.addUserByCC(true, getUserAuthToken(), addUserInput)
            Log.d("find_doctor","2")

        }
    }


    private fun showFindDoctorInputDialogue() {

        findPatientInputDialogue = Dialog(FourBaseCareApp.activityFromApp)
        findPatientInputDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        findPatientInputDialogue.setContentView(R.layout.dialogue_add_doctor_name)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(findPatientInputDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = findPatientInputDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findPatientInputDialogue.window?.attributes = lp
        findPatientInputDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: TextView = findPatientInputDialogue.findViewById(R.id.btnYes)

        val btnCancel: TextView = findPatientInputDialogue.findViewById(R.id.btnNo)

        val edMobile: EditText = findPatientInputDialogue.findViewById(R.id.edMobileNumber)

        val tvTitle : TextView = findPatientInputDialogue.findViewById(R.id.tvTitle)
        tvTitle.setText("Add Patient")

        btnYes.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            }else{
                if(getTrimmedText(edMobile).isNullOrBlank()){
                    showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
                }else{
                    findPatientInputDialogue.dismiss()
                    CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                    findDoctor(getTrimmedText(edMobile))
                    CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                }

                //findDoctor(getTrimmedText(edMobile))
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            findPatientInputDialogue.dismiss()

        })

        findPatientInputDialogue.show()
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            profileViewModel.findDoctor(true,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("find_doctor","2")

        }
    }

    private fun assignDoctor(shouldAssignPatient : Boolean,doctorId: String) {
        Log.d("assign_log","0.1")
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("assign_log","1")
            profileViewModel.assignDoctor(shouldAssignPatient,
                doctorId,
                getUserAuthToken()
            )
            Log.d("assign_log","2")

        }
    }


    private fun setClickListeners() {

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.floatAddPatient.setOnClickListener(View.OnClickListener {
                if(checkIFCareCOmpanion()){
                    showAddPatientByCCInputDialogue()
                }else{
                    showFindDoctorInputDialogue()
                }

        })


        /*binding.ivStartSearch.setOnClickListener(View.OnClickListener {
            binding.linTitleContainer.visibility = View.GONE
            binding.relSearchContainer.visibility = View.VISIBLE

        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            binding.linTitleContainer.visibility = View.VISIBLE
            binding.relSearchContainer.visibility = View.GONE
        })*/
    }

    private fun setupVM() {
        appointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun showHideNoData(shouldShowNoData: Boolean){
        if(shouldShowNoData){
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }


    private fun setRecyclerView(list: List<PatientDetails>?) {

        val arrayList = list?.let { ArrayList<PatientDetails>(it) }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            patientListAdapter = PatientAdapter(arrayList!!, this@PatientListingFragment)
            adapter = patientListAdapter
            patientListAdapter.submitList(arrayList)


        }
    }

    override fun onPatientSelected(position: Int, item: PatientDetails, view: View) {
        //CommonMethods.showImplementationPending(FourBaseCareApp.activityFromApp)

            if(view.id == R.id.ivYes){
                showResponseConfirmDialogue(""+item.id,true)
            }else if(view.id == R.id.ivNo){
                showResponseConfirmDialogue(""+item.id,false)
            }else{
            if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
                var bundle  = Bundle()
                bundle.putString(Constants.PATIENT_ID,""+item.id)
                bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                Log.d("record_back","0")
                var medicalRecordFragment = MedicalRecordsListingFragmentNew()
                medicalRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    medicalRecordFragment, this, false
                )
            }else if(checkIFDoctor()){
                if(item.isRequestPending != null && item.isRequestPending == true){
                      showToast(FourBaseCareApp.activityFromApp, "You will only be able to view the patient profile after they accept the invite.")
                }else{
                    var bundle  = Bundle()
                    bundle.putString(Constants.PATIENT_ID,""+item.id)
                    bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                    bundle.putSerializable(Constants.PATIENT_DATA,item)
                    Log.d("record_back","from doctor")
                    var medicalRecordFragment = PatientRecordsListingByDrFragment()
                    medicalRecordFragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        medicalRecordFragment, this, false
                    )
                }

            }
            }

    }

    /*override fun onCancerTypeSelected(position: Int, item: PatientDetails, view: View) {
        TODO("Not yet implemented")
    }*/

    private fun setBottomSectionVisibility() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden")
                        binding.floatAddPatient.visibility = View.GONE

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        binding.floatAddPatient.visibility = View.VISIBLE
                    }
                }
            })
    }

    private fun showDeleteConfirmDialogue(patientId: String) {
        deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
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

        tvMsg.setText("Are you sure you want to delete this patient?")


        btnDelete.setOnClickListener(View.OnClickListener {
            deletePatient(patientId)
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
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
            tvMsg.setText("Are you sure you want to accept connection with this patient?")
        }else{
            tvMsg.setText("Are you sure you do not want to accept connection for this patient?")
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


    private fun deletePatient(patientId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.deletePatientByCC(getUserAuthToken(),patientId)
            Log.d("delete_log", "API called")

        }
    }

    private fun doConnectionResponse(patientId: String, isAccepted: Boolean) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.responseCOnnectionRequest(getUserAuthToken(),patientId, true,isAccepted)
            Log.d("delete_log", "API called")

        }
    }



}