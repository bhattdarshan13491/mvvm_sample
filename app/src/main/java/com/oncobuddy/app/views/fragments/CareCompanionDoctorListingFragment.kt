package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.oncobuddy.app.models.pojo.care_giver_details.AddUserByCCInput
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.DoctorListingAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Care companion doctor listing fragment
 *
 * @constructor Create empty Care companion doctor listing fragment
 */

class CareCompanionDoctorListingFragment : BaseFragment(), DoctorListingAdapter.Interaction{

    private lateinit var binding : FragmentPatientsListBinding
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var profileViewModel : ProfileViewModel
    private lateinit var doctorListAdapter: DoctorListingAdapter
    private lateinit var findPatientInputDialogue: Dialog
    private lateinit var addPatientByCcInputDialogue: Dialog
    private lateinit var doctorList: ArrayList<Doctor>

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
        if(arguments!= null){
        if(arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
            if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)) binding.ivBack.visibility = View.GONE
        }
        }
        setupVM()
        getDoctorsFromServer()
        setupObservers()
        setClickListeners()
        setBottomSectionVisibility()
        binding.tvTitle.setText("Doctor Listing")
    }

    private fun getDoctorListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            appointmentViewModel.callGetDoctorListing(false,
                getUserAuthToken()
            )
        }
    }

    private fun verifyDoctor(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.verifyDoctorByCC(getUserAuthToken(), doctorId)
        }
    }

    private fun getDoctorsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                getDoctorListingFromServer()
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupObservers() {
        profileViewModel.addUserByCCResonseData.observe(viewLifecycleOwner,addDoctorResponseObserver)
        profileViewModel.verifyDoctorResponse.observe(viewLifecycleOwner, verifyDOctorResponseObserver)
        appointmentViewModel.doctorListResponse.observe(viewLifecycleOwner, doctorListResponseObserver)
        appointmentViewModel.isViewLoading.observe(viewLifecycleOwner, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(viewLifecycleOwner, errorMessageObserver)
        profileViewModel.isViewLoading.observe(viewLifecycleOwner,isViewLoadingObserver)
        profileViewModel.onMessageError.observe(viewLifecycleOwner, errorMessageObserver)
    }



    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver != null) {
            if(responseObserver.success && !responseObserver.doctorList.isNullOrEmpty()){
                showHideNoData(false)
                doctorList = ArrayList()
                doctorList.addAll(responseObserver.doctorList.asReversed())
                setRecyclerView(doctorList)
            }else{
                showHideNoData(true)
            }
        }else{
            showHideNoData(true)
        }
    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("assign_log_", "is_loading is "+isLoading)
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

    private val addDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            Log.d("add_doct_cc","here response "+responseObserver?.message)
            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, "Doctor added successfully!")
                    getDoctorListingFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
            binding.executePendingBindings()
            binding.invalidateAll()

        }

    private val verifyDOctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            Log.d("add_doct_cc","here response "+responseObserver?.message)
            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, "Doctor verified successfully!")
                    getDoctorListingFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
            binding.executePendingBindings()
            binding.invalidateAll()

        }

    private fun showFindDoctorInputDialogue() {

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
        edName.setHint("Please enter doctor name")
        val edMobile: EditText = findPatientInputDialogue.findViewById(R.id.edMobileNumber)

        val tvTitle : TextView = findPatientInputDialogue.findViewById(R.id.tvTitle)
        tvTitle.setText("Add Doctor")

        btnYes.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(edName).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_name))
            }
            else if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            } else if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_otp))
            }else{
                if(getTrimmedText(edMobile).isNullOrBlank()){
                    showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
                }else{
                    findPatientInputDialogue.dismiss()
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
            profileViewModel.addUserByCC(false, getUserAuthToken(), addUserInput)
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

                if(::doctorListAdapter.isInitialized) doctorListAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.floatAddPatient.setOnClickListener(View.OnClickListener {
                showFindDoctorInputDialogue()

        })

    }

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


    private fun setRecyclerView(list: List<Doctor>?) {

        val arrayList = list?.let { ArrayList<Doctor>(it) }


        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            doctorListAdapter = DoctorListingAdapter(arrayList!!, this@CareCompanionDoctorListingFragment, true)
            adapter = doctorListAdapter
            doctorListAdapter.submitList(arrayList)


        }
    }

  /*  override fun onPatientSelected(position: Int, item: PatientDetails, view: View) {
        //CommonMethods.showImplementationPending(FourBaseCareApp.activityFromApp)
        if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
            var bundle  = Bundle()
            bundle.putString(Constants.PATIENT_ID,""+item.id)
            var medicalRecordFragment = MedicalRecordsListingFragmentNew()
            medicalRecordFragment.arguments = bundle

            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                medicalRecordFragment, this, false
            )
        }
    }*/

    override fun onItemSelected(position: Int, item: Doctor, view: View) {
        if(view.id == R.id.tvVerify){
            verifyDoctor(""+item.doctorId)
        }

    }

    /*override fun onCancerTypeSelected(position: Int, item: PatientDetails, view: View) {
        TODO("Not yet implemented")
    }*/


}