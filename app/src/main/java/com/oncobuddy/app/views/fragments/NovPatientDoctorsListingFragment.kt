package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
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
import com.oncobuddy.app.databinding.FragmentNovPatientDoctorsBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.NovDoctorDetailsActivity
import com.oncobuddy.app.views.adapters.DoctorListingForAppointmentAdapter
import java.util.*
import kotlin.collections.ArrayList


class NovPatientDoctorsListingFragment : BaseFragment(), DoctorListingForAppointmentAdapter.Interaction{

    private lateinit var binding : FragmentNovPatientDoctorsBinding
    private lateinit var doctorListingAdapter: DoctorListingForAppointmentAdapter
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var sentRequestList: ArrayList<Doctor>
    private lateinit var responseConfirmationDialogue: Dialog
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var findDoctorInputDialogue: Dialog
    private var IS_VIEWING_SENT = false
    private val CATEGORY_DOCTOR = 0
    private val CATEGORY_REQUEST = 1

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
            R.layout.fragment_nov_patient_doctors, container, false
        )
        setClickListeners()
        setupVM()
        setupObservers()
        getDoctorListingFromServer()
        Log.d("card_log","0")
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
                    getDoctorListingFromServer()
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "Doctor not found!")
                }
            }
        }


    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        doctorList = ArrayList()
        sentRequestList = ArrayList()
        if (responseObserver.isSuccess) {
            for(doctor in responseObserver.doctorList){
                if(doctor.isRequestPending){
                    sentRequestList.add(doctor)
                }else{
                    doctorList.add(doctor)
                }
            }
            //doctorList.addAll(responseObserver.doctorList)
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
            var source = ""
            if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                source = Constants.ROLE_PATIENT_CARE_GIVER
            }
            profileViewModel.callGetMyDoctorListing(source,false,
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

    private fun setClickListeners() {

        binding.ivAdd.setOnClickListener(View.OnClickListener {
            CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, SearchDoctorFragment(), this, false)
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.linMyDoctors.setOnClickListener(View.OnClickListener {
            setSelectionColor(CATEGORY_DOCTOR)
            setRecyclerView(doctorList)
        })

        binding.linSentRequest.setOnClickListener(View.OnClickListener {
            setSelectionColor(CATEGORY_REQUEST)
            setRecyclerView(sentRequestList)
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
                doctorListingAdapter = DoctorListingForAppointmentAdapter(arrayList!!, this@NovPatientDoctorsListingFragment, false, true)
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
        }else if(view.id == R.id.relImage){
            // com.oncobuddy.app.models.pojo.doctors.doctors_listing
            // com.oncobuddy.app.models.pojo.doctor_profile.doctor_details : name, specialization, image url, designation, years of exp, description, fees

            var doctorDetails = DoctorDetails()
            doctorDetails.firstName = item.firstName
            doctorDetails.dpLink = item.displayPicUrl
            doctorDetails.description = ""
            doctorDetails.consultationFee = item.consultationFee.toInt()
            doctorDetails.designation = item.designation
            doctorDetails.specialization = item.specialization
            doctorDetails.experience = "0"


            val intent = Intent(FourBaseCareApp.activityFromApp, NovDoctorDetailsActivity::class.java)
            intent.putExtra(Constants.DOCTOR_DATA, doctorDetails)
            startActivityForResult(intent, Constants.DOCTOR_DETAILS_RESULT)
            FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        }
        else{
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
            //settitle()
            getDoctorListingFromServer()
        }
    }



    private fun setSelectionColor(category : Int){
        if(category == CATEGORY_DOCTOR){
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))

        }else{
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

        if(category == CATEGORY_REQUEST){
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))
        }else{
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.gray))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

    }


}