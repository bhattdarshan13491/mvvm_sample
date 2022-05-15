package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAddCgBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel

/**
 * Add care taker fragment
 * Takes basic details as an input and handles the addition of the care taker on the server
 * @constructor Create empty Add care taker fragment
 */

class AddCareTakerFragment : BaseFragment() {

    private lateinit var binding: FragmentAddCgBinding
    private lateinit var profileViewModel: ProfileViewModel

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
            R.layout.fragment_add_cg, container, false
        )

        binding.layoutHeader.tvTitle.text = "Add Care Giver"

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        binding.ivDropDOwnRelType.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())binding.spCGOneRelationShip.performClick()
        })


        binding.tvSignIn.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()&& checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidCTInput()) {
                val relArray: Array<String> = getResources().getStringArray(R.array.relationship_type)
                var addCareCompanionInput = AddCareCompanionInput()
                addCareCompanionInput.name = getTrimmedText(binding.etCGOneName)
                addCareCompanionInput.mobileNumber = getTrimmedText(binding.etMobileNumberCG1)
                addCareCompanionInput.relationship = CommonMethods.returnRelationSHipEnum(relArray.get(binding.spCGOneRelationShip.selectedItemPosition))
                profileViewModel.addCareCompanion(addCareCompanionInput, getUserObject().authToken, Constants.ROLE_PATIENT)
            }
        })

        setupVM()
        setupObservers()
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.addCareCompanionResponse.observe(this, addCareTakerObserver)
        profileViewModel.isViewLoading.observe(this,
                isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val addCareTakerObserver = androidx.lifecycle.Observer<AddCareTakerResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.isSuccess){
            showToast(FourBaseCareApp.activityFromApp, "Care giver added successfully!")
            binding.etCGOneName.setText("")
            binding.etMobileNumberCG1.setText("")
            binding.spCGOneRelationShip.setSelection(0)
            fragmentManager?.popBackStack()
        }else{
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while adding care giver")
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

    private fun isValidCTInput() : Boolean {
        Log.d("cg_log","0")
        if(getTrimmedText(binding.etCGOneName).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp,"Please enter care giver name")
            return  false
        }
        else if(getTrimmedText(binding.etMobileNumberCG1).isNullOrBlank()){
            showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_enter_mobile_number))
            return  false
        }else if(getTrimmedText(binding.etMobileNumberCG1).toString().trim().length < 10){
            showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_invalid_mobile_number))
            return  false
        }
        else if(getUserObject() !=null && !getUserObject().phoneNumber.isNullOrEmpty() &&
            getTrimmedText(binding.etMobileNumberCG1).equals(getUserObject().phoneNumber)){
            showToast(FourBaseCareApp.activityFromApp,"Your mobile number cannot be added as a care giver number")
            return  false
        }
        else if(binding.spCGOneRelationShip.selectedItemPosition == 0){
            showToast(FourBaseCareApp.activityFromApp,"Please select relationship type")
            return false
        }

        return true
    }


}