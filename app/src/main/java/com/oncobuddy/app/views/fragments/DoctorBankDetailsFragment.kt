package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.doctor_update.AddressDto
import com.oncobuddy.app.models.pojo.doctor_update.AppUser
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import java.text.DecimalFormat

class DoctorBankDetailsFragment : BaseFragment() {

    private lateinit var binding: LayoutFeeBankDetailsBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var IS_DETAIL_ADDED = false


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
            R.layout.layout_fee_bank_details, container, false
        )

        setupClickListeners()
        setupText()
        setupVM()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.relContinue.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                validateAndSendData()

                //decideRedirection()
            }
        })
    }

    private fun setupText() {
        binding.layoutHeader.tvTitle.setText("Fees and Bank Details")

        if (getUserObject().profile.bankDetailsAdded != null) {
            if (getUserObject().profile.bankDetailsAdded.equals("true")) {
                IS_DETAIL_ADDED = true
                binding.tvStatusMessage.text =
                    "Your bank account details have been captured successfully. Please reach to the customer care if there is any issue or query."
            } else {
                IS_DETAIL_ADDED = false
                binding.tvStatusMessage.text =
                    "Your bank account details has not been added yet. Please go to consultation fees and time settings section and fill bank details."
            }

        }

        var strFees = "" + getUserObject().profile.consultationFee
        if (strFees.equals("null", true)) {
            binding.edFees.setText("")
        } else {
            val df = DecimalFormat("#.##")
            binding.edFees.setText("" + df.format(getUserObject().profile.consultationFee))
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val updateProfileObserver = androidx.lifecycle.Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            userObj.profile.consultationFee = getTrimmedText(binding.edFees).toDouble()
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            //fragmentManager!!.popBackStack()
            //decideRedirection()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun validateAndSendData() {
        if (getTrimmedText(binding.edFees).isNullOrEmpty()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter Fees")
        } else {

            //var appUser = AppUser()

            var doctorRegistrationINput = DoctorRegistrationInput()
            doctorRegistrationINput.consultationFee = getTrimmedText(binding.edFees).toDouble()


            profileViewModel.updateProfile(
                getUserAuthToken(),
                null,
                doctorRegistrationINput,
                Constants.ROLE_DOCTOR
            )
            //gotoNextFragment()
            Log.d("update_log", "1")
        }
    }


}