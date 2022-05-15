package com.oncobuddy.app.views.fragments


import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
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
import com.oncobuddy.app.databinding.FragmentDoctorsListBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.DoctorListingForAppointmentAdapter
import java.util.*
import kotlin.collections.ArrayList


class DoctorListingFragment : BaseFragment(), DoctorListingForAppointmentAdapter.Interaction{

    private lateinit var binding : FragmentDoctorsListBinding
    private lateinit var doctorListingAdapter: DoctorListingForAppointmentAdapter
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var responseConfirmationDialogue: Dialog
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var findDoctorInputDialogue: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_doctors_list, container, false
        )
        setClickListeners()
        setupVM()
        setupObservers()
        getDoctorListingFromServer()
        settitle()
    }

    private fun settitle() {
        binding.layoutHeader.tvTitle.setText("My Doctors")
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.connectionResonseData.observe(this, connectionResponseObserver)
        profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
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


    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            doctorList = ArrayList()
            doctorList.addAll(responseObserver.doctorList)
            setRecyclerView(doctorList)

        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val connectionResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    getDoctorsFromServer()
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }

    private fun getDoctorsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            /*Timer().schedule(Constants.FUNCTION_DELAY) {
                getDoctorListingFromServer()

            }*/
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getDoctorListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetMyDoctorListing(getUserObject().role,false,
                getUserAuthToken(), "" + getUserObject().userIdd
            )
        }
    }


    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
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

    private fun setClickListeners() {

        binding.layoutHeader.ivSearch.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
                binding.layoutHeader.linSearchConteiner.visibility = View.VISIBLE
            }
        })

        binding.layoutHeader.ivClose.setOnClickListener(View.OnClickListener {
            binding.layoutHeader.edSearch.setText("")
        })

        binding.layoutHeader.ivBackResults.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
                binding.layoutHeader.linSearchConteiner.visibility = View.GONE
            }
        })

        binding.linAddDoctor.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, SearchDoctorFragment(), this, false)
            }
        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.layoutHeader.edSearch.addTextChangedListener(object : TextWatcher {
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
                doctorListingAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
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

        if(arrayList != null && arrayList.size > 0){
            showHideNoData(false)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                doctorListingAdapter = DoctorListingForAppointmentAdapter(arrayList!!, this@DoctorListingFragment)
                adapter = doctorListingAdapter
                doctorListingAdapter.submitList(arrayList)
            }
        }else{
            showHideNoData(true)
        }
    }

    override fun onItemSelected(position: Int, item: Doctor, view: View) {
        if(view.id == R.id.ivYes){
            Log.d("doctor_response_log","Yes")
            showResponseConfirmDialogue(""+item.doctorId,true)
        }else if(view.id == R.id.ivNo){
            Log.d("doctor_response_log","No")
            showResponseConfirmDialogue(""+item.doctorId,false)
        }else{
            if(item.verified  != null && !item.verified){
                showToast(FourBaseCareApp.activityFromApp, "You cannot book appointment with unverified doctor")
            }else if(item.isRequestPending != null && item.isRequestPending){
                showToast(FourBaseCareApp.activityFromApp, "You cannot book appointment with the doctor as connection request is pending")
            }else{
                var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                var bundle = Bundle()
                bundle.putParcelable(Constants.DOCTOR_DATA, item)
                addOrEditAppointmentFragment.arguments = bundle
                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, addOrEditAppointmentFragment, this, false)
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
            profileViewModel.responseCOnnectionRequest(getUserAuthToken(),patientId, false,isAccepted)
            Log.d("connection_log", "API called")
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!isHidden){
            settitle()
        }
    }




}