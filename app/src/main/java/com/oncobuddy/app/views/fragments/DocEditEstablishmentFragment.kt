package com.oncobuddy.app.views.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.oncobuddy.app.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.FragmentAddEstablishmentBinding
import com.oncobuddy.app.databinding.LayoutDoctorExpSpecializationBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_certification.Certification
import com.oncobuddy.app.models.pojo.doctor_locations.Address
import com.oncobuddy.app.models.pojo.doctor_locations.Establishment
import com.oncobuddy.app.models.pojo.doctor_locations.EstablishmentInput
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel


class DocEditEstablishmentFragment : BaseFragment() {

    private lateinit var binding: FragmentAddEstablishmentBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var selectedEstTYpe = "nothing"
    private var IS_EDIT_MODE =  false
    private var id = "-1"
    private lateinit var establishment :Establishment


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_establishment, container, false
        )
        setupTitle()
        setupCLickListeners()

        if(arguments?.containsKey("establishment") == true){
            IS_EDIT_MODE = true
            establishment = arguments?.getParcelable("establishment")!!
            id = ""+establishment.id
            binding.etEstName.setText(establishment.establishmentName)
            binding.etDoorNo.setText(establishment.address.address1)
            binding.etStreetName.setText(establishment.address.address2)
            binding.etCity.setText(establishment.address.city)
            binding.etState.setText(establishment.address.state)
            binding.etPincode.setText(establishment.address.postalCode)
            selectedEstTYpe = establishment.doctorConsultationLocationType

            if(selectedEstTYpe.equals(Constants.OWN_CLINIC)){
                binding.spEstablishmentType.setSelection(1)
            }else if(selectedEstTYpe.equals(Constants.VISIT_HOSPITAL)){
                binding.spEstablishmentType.setSelection(2)
            }

        }else{
            IS_EDIT_MODE = false
        }

        setupVM()
        setupObservers()
    }
    private fun setupTitle() {

        if (Constants.IS_ACC_SETUP_MODE) {
            binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.VISIBLE
            binding.layoutAcSetup.tvCurrentStep.setText("Practice Information")
            binding.layoutAcSetup.tvNextStep.setText("Next : Document Verification")
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_4_of_6).into(binding.layoutAcSetup.ivStep)
        } else {
            binding.layoutHeader.tvTitle.setText("Practice Information")
            binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.GONE
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.addLocationRsonseData.observe(this, addEstablishmentObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun setupCLickListeners() {

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
        binding.layoutAcSetup.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        binding.relContinue.setOnClickListener {
            if(!isDoubleClick() && isValidInput()){
                sendDataTOServer()
            }

        }

        binding.ivDropDownEstablishment.setOnClickListener {
            binding.spEstablishmentType.performClick()
        }

        binding.spEstablishmentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    selectedEstTYpe = "nothing"
                }else if(position == 1){
                    selectedEstTYpe = Constants.OWN_CLINIC
                }else{
                    selectedEstTYpe = Constants.VISIT_HOSPITAL
                }

            }

        }




    }

    private fun sendDataTOServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            var establishmentInput = EstablishmentInput()
            establishmentInput.locationType = selectedEstTYpe
            establishmentInput.establishmentName = getTrimmedText(binding.etEstName)
            establishmentInput.address = Address()
            establishmentInput.address.address1 = getTrimmedText(binding.etDoorNo)
            establishmentInput.address.address2 = getTrimmedText(binding.etStreetName)
            establishmentInput.address.state = getTrimmedText(binding.etState)
            establishmentInput.address.city = getTrimmedText(binding.etCity)
            establishmentInput.address.postalCode = getTrimmedText(binding.etPincode)
            if(id.equals("-1"))
                profileViewModel.addOrEditEstablishment(getUserAuthToken(), null, establishmentInput)
            else
                profileViewModel.addOrEditEstablishment(getUserAuthToken(), id, establishmentInput)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        return binding.root
    }



    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(context!!, message)
    }


    private fun decideRedirection() {
        if (Constants.IS_ACC_SETUP_MODE) {
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                DocVerifyNovFragment(), this, false
            )
        } else {
            fragmentManager?.popBackStack()
        }
    }

    private val addEstablishmentObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
            Constants.IS_LIST_UPDATED = true
            decideRedirection()
        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while adding establishment")


        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun isValidInput() : Boolean {
        if(selectedEstTYpe.equals("nothing")){
            showToast(FourBaseCareApp.activityFromApp,"Please select establishment type")
            return  false
        }else if(getTrimmedText(binding.etEstName).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment number")
            return  false
        }else if(getTrimmedText(binding.etDoorNo).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment door no")
            return  false
        }else if(getTrimmedText(binding.etStreetName).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment street name")
            return  false
        }else if(getTrimmedText(binding.etCity).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment city")
            return  false
        }else if(getTrimmedText(binding.etState).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment State")
            return  false
        }else if(getTrimmedText(binding.etPincode).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter establishment pin code")
            return  false
        }

        return true
    }


}