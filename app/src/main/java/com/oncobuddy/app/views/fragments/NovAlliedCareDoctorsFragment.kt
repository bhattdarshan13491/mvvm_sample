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
import com.oncobuddy.app.databinding.FragmentAlliedDoctorsBinding
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


class NovAlliedCareDoctorsFragment : BaseFragment(), DoctorListingForAppointmentAdapter.Interaction{

    private lateinit var binding : FragmentAlliedDoctorsBinding
    private lateinit var doctorListingAdapter: DoctorListingForAppointmentAdapter
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var profileViewModel: ProfileViewModel

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
            R.layout.fragment_allied_doctors, container, false
        )
        setClickListeners()
        setupVM()
        setupObservers()
        getDoctorListingFromServer()
    }



    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }



    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        doctorList = ArrayList()
        if (responseObserver.isSuccess) {
            doctorList.addAll(responseObserver.doctorList)
            setRecyclerView(doctorList)

        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun getDoctorListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetMyDoctorListing("",true,
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
                doctorListingAdapter = DoctorListingForAppointmentAdapter(arrayList!!, this@NovAlliedCareDoctorsFragment, false, true)
                adapter = doctorListingAdapter
                doctorListingAdapter.submitList(arrayList)
            }


        }else{
            showHideNoData(true)
        }


    }

    override fun onItemSelected(position: Int, item: Doctor, view: View) {
          if(view.id == R.id.relImage){
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



    }

}