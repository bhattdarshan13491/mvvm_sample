package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentNovDoctorPatientsBinding
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
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.PatientAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class DoctorPatientListingFragment : BaseFragment(), PatientAdapter.Interaction {

    private lateinit var binding: FragmentNovDoctorPatientsBinding
    private lateinit var patientsAdapter: PatientAdapter
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var confirmedPatientsList: ArrayList<PatientDetails>
    private lateinit var pendingPatientsList: ArrayList<PatientDetails>
    private lateinit var findPatientInputDialogue: Dialog
    private lateinit var responseConfirmationDialogue: Dialog
    private var IS_VIEWING_CONFIRMED = true

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
            R.layout.fragment_nov_doctor_patients, container, false)

        setupVM()
        setupObservers()
        setClickListeners()
        getPatientsFromServer()
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
            }else if(getTrimmedText(edMobile).length != 10){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_invalid_mobile_number))
            }else{
                Log.d("add_patient","0")
                findPatientInputDialogue.dismiss()
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                findDoctor(getTrimmedText(edMobile))
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            findPatientInputDialogue.dismiss()

        })

        findPatientInputDialogue.show()
    }

    private fun setClickListeners() {
        binding.ivAdd.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                /*CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    NovPatientDetails(), this, false
                )*/
                showAddPatientByCCInputDialogue()

            }
        })

        binding.linMyPatients.setOnClickListener(View.OnClickListener {
            IS_VIEWING_CONFIRMED = true
            setSelectionColor()
            setRecyclerView()
        })


        binding.linPendingRequest.setOnClickListener(View.OnClickListener {
            IS_VIEWING_CONFIRMED = false
            setSelectionColor()
            setRecyclerView()
        })

    }

    private fun setSelectionColor() {
        if (IS_VIEWING_CONFIRMED) {
            binding.ivMyPatientsLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvMyPatients.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )

            binding.ivPendingRequestsLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvPendingRequests.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))


        } else {
            binding.ivPendingRequestsLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvPendingRequests.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )

            binding.ivMyPatientsLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvMyPatients.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }
    }


    private val addDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, "Patient added successfully!")
                    getPatientsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }


    private fun setupObservers() {
        appointmentViewModel.patientListResponseData.observe(this,responseObserver)
        profileViewModel.addUserByCCResonseData.observe(this,addDoctorResponseObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.connectionResonseData.observe(this, connectionResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        appointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(this, errorMessageObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
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
            //binding.linAppointmentsContainer.visibility = View.GONE
            //binding.ivStartSearch.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            //binding.linAppointmentsContainer.visibility = View.VISIBLE
            //binding.ivStartSearch.visibility = View.VISIBLE
        }
    }

    //observers
    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("assign_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val responseObserver = Observer<PatientsListResponse?>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver != null) {
            if(responseObserver.success && !responseObserver.payLoad.isNullOrEmpty()){
                showHideNoData(false)
                setDataInLists(responseObserver.payLoad.asReversed())
            }else{
                showHideNoData(true)
            }
        }else{
            showHideNoData(true)
        }
    }

    private fun setDataInLists(list: List<PatientDetails>?) {

        val arrayList = list?.let { ArrayList<PatientDetails>(it) }

        confirmedPatientsList = ArrayList()
        pendingPatientsList = ArrayList()

        if (arrayList != null) {
            for(patient in arrayList){
                if(patient.isRequestPending){
                    pendingPatientsList.add(patient)
                }else{
                    confirmedPatientsList.add(patient)
                }
            }
        }
        Log.d("patienr_list_log","pending "+pendingPatientsList.size)
        Log.d("patienr_list_log","confirmed "+confirmedPatientsList.size)
        setRecyclerView()
    }

    private fun setRecyclerView() {

        if(IS_VIEWING_CONFIRMED){
            if(::confirmedPatientsList.isInitialized && !confirmedPatientsList.isNullOrEmpty()){
                binding.tvNoData.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    patientsAdapter = PatientAdapter(confirmedPatientsList, this@DoctorPatientListingFragment)
                    adapter = patientsAdapter
                    patientsAdapter.submitList(confirmedPatientsList)
                }
            }else{
                binding.tvNoData.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }

        }else{
            if(::pendingPatientsList.isInitialized && !pendingPatientsList.isNullOrEmpty()){
                binding.tvNoData.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    patientsAdapter = PatientAdapter(pendingPatientsList, this@DoctorPatientListingFragment)
                    adapter = patientsAdapter
                    patientsAdapter.submitList(pendingPatientsList)
                }
            }else{
                binding.tvNoData.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
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
                    var medicalRecordFragment = NovPatientDetails()
                    medicalRecordFragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        medicalRecordFragment, this, false
                    )
                }

            }
        }

    }

    private fun getPatientsFromServer() {

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
                    appointmentViewModel.callGetPatientList(Constants.ROLE_DOCTOR, getUserAuthToken(), searchQueryInput)
                }
            }
        }else {
            Toast.makeText(FourBaseCareApp.activityFromApp,getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show()
        }

    }

    private fun addDoctor(name : String, phoneNumber: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("add_patient","1")
            var addUserInput = AddUserByCCInput()
            addUserInput.name = name
            addUserInput.mobileNumber = phoneNumber
            profileViewModel.addUserByCC(true, getUserAuthToken(), addUserInput)
            Log.d("find_doctor","2")

        }
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
                    getPatientsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }

    private fun doConnectionResponse(patientId: String, isAccepted: Boolean) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.responseCOnnectionRequest(getUserAuthToken(),patientId, true,isAccepted)
            Log.d("delete_log", "API called")

        }
    }

    private val connectionResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    getPatientsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }


}