package com.oncobuddy.app.views.fragments

import android.app.DatePickerDialog
import com.oncobuddy.app.models.factory_providers.DoctorSetupChangeListener
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.Time
import android.util.Log
import android.view.View
import android.widget.DatePicker
import com.oncobuddy.app.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.FragmentDoctorPersonalInfoBinding

import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.doctor_update.AddressDto
import com.oncobuddy.app.models.pojo.doctor_update.AppUser
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import java.util.*

class DrUpdatePersonalInfoNovFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorPersonalInfoBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var doctorSetupChangeListener: DoctorSetupChangeListener? = null

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_doctor_personal_info, container, false
        )
        doctorSetupChangeListener = activity as DoctorSetupChangeListener?
        setupVM()
        setupObservers()

        setupCLickListeners()

    }

    private fun setupCLickListeners() {

        binding.relDob.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showToDatePickerDialogue()
            }
        })

        binding.relContinue.setOnClickListener {
            //validateAndSendData()
            gotoNextFragment()
        }
    }

    private fun validateAndSendData() {
        if (getTrimmedText(binding.edName).isNullOrEmpty()) {
            showToast(context!!, "Please enter name")
        } else if (binding.tvDob.text.isNullOrEmpty()) {
            showToast(context!!, "Please enter date of birth")
        } else {
            var addressDTO = AddressDto()
            addressDTO.address1 = "Address line 1"
            addressDTO.address2 = "Address line 2"
            addressDTO.city = "City"
            addressDTO.postalCode = "123456"
            addressDTO.state = "State"

            var appUser = AppUser()
            appUser.firstName = getTrimmedText(binding.edName)
            appUser.lastName = ""
            appUser.dateOfBirth = binding.tvDob.text.toString()
            appUser.description = getTrimmedText(binding.edAbout)

            var doctorRegistrationINput = DoctorRegistrationInput()
            doctorRegistrationINput.appUser = appUser

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

    private fun showToDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val yy = calendar[Calendar.YEAR]
        val mm = calendar[Calendar.MONTH]
        val dd = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            context?.let {
                DatePickerDialog(
                    it,
                    DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val chosenDate = Time()
                        chosenDate[dayOfMonth, monthOfYear] = year
                        val dtDob = chosenDate.toMillis(true)
                        var strDate = DateFormat.format("yyyy-MM-dd", dtDob)
                        //               binding.tvToVal.setText(strDate.toString())
                        binding.tvDob.text = strDate

                    }, yy, mm, dd
                )
            }

        if (datePicker != null) {
            datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }

    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        doctorSetupChangeListener!!.doctorSetupChange(this.javaClass.simpleName)
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
            userObj.firstName = getTrimmedText(binding.edName)
            userObj.dateOfBirth = binding.tvDob.text.toString()
            userObj.description = getTrimmedText(binding.edAbout)

            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            //fragmentManager!!.popBackStack()
            gotoNextFragment()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private fun gotoNextFragment() {
        /*CommonMethods.addNextFragment(
            activity as DoctorAccountSetupNovActivity,
            DocExpAndSpecializationNovFragment(), this, false
        )*/
        val docExpAndSpecializationNovFragment = DocExpAndSpecializationNovFragment()
        val fragmentManager = activity!!.supportFragmentManager
        val backStateName = docExpAndSpecializationNovFragment.javaClass.name
        fragmentManager.beginTransaction()
            .replace(R.id.frameLayout, docExpAndSpecializationNovFragment)
            .addToBackStack(backStateName)
            .commit()
    }
}