package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.oncobuddy.app.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.LayoutDoctorExpSpecializationBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import java.util.*


class DocExpAndSpecializationNovFragment : BaseFragment() {

    private lateinit var binding: LayoutDoctorExpSpecializationBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var yearsCount = 0
    private var selectedSPecialization = "nothing"

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_doctor_exp_specialization, container, false
        )
        setupTitle()
        setUserData()
        setupCLickListeners()
        setupVM()
        setupObservers()
        setBottomSectionVisibility()

    }

    private fun setUserData() {
        var userObject = getUserObject()

        if(userObject.profile.specialization != null){
            selectedSPecialization = userObject.profile.specialization
            binding.spSpecialization.setSelection(resources.getStringArray(R.array.specialization).indexOf(selectedSPecialization))
            binding.edYearsOfExperience.setText(userObject.profile.experience)
            yearsCount = userObject.profile.experience.toInt()
        }

    }

    private fun setupTitle() {

        if (Constants.IS_ACC_SETUP_MODE) {
            binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.VISIBLE
            binding.layoutAcSetup.tvCurrentStep.setText("Experience and Specialization")
            binding.layoutAcSetup.tvNextStep.setText("Next : Education Background")
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_2_of_6).into(binding.layoutAcSetup.ivStep)
        } else {
            binding.layoutHeader.tvTitle.setText("Specialization and Experience")
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
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun setupCLickListeners() {

        binding.edYearsOfExperience.addTextChangedListener(object : TextWatcher {
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
                        if(!s.isNullOrEmpty()) yearsCount = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.layoutAcSetup.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        binding.ivDropDownSP.setOnClickListener(View.OnClickListener {
            binding.spSpecialization.performClick()
        })

        binding.spSpecialization.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if(position == 0){
                    selectedSPecialization = "nothing"
                }else{
                    selectedSPecialization = resources.getStringArray(R.array.specialization).get(position)
                }
            }

        }


        binding.relContinue.setOnClickListener {
            sendDataTOServer()
        }

        binding.tvSave.setOnClickListener {
            sendDataTOServer()
        }

        binding.ivArrowUp.setOnClickListener(View.OnClickListener {
                    yearsCount+=1
                    binding.edYearsOfExperience.setText(CommonMethods.getStringWithOnePadding(yearsCount.toString()))
        })

        binding.ivArrowDown.setOnClickListener(View.OnClickListener {

            if(yearsCount > 0){
                yearsCount -= 1
                binding.edYearsOfExperience.setText(CommonMethods.getStringWithOnePadding(yearsCount.toString()))
            }

        })
    }

    private fun sendDataTOServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            if(selectedSPecialization.equals("nothing")){
                showToast(FourBaseCareApp.activityFromApp, "Please select specialization")
            }else{
                var doctorRegistrationInput = DoctorRegistrationInput()

                if (!selectedSPecialization.equals("nothing")) doctorRegistrationInput.specialization = selectedSPecialization

                if (yearsCount > 0) doctorRegistrationInput.experience = getTrimmedText(binding.edYearsOfExperience)

                profileViewModel.updateProfile(
                    getUserAuthToken(),
                    null,
                    doctorRegistrationInput,
                    Constants.ROLE_DOCTOR
                )
            }


        }

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
       // doctorSetupChangeListener!!.doctorSetupChange(this.javaClass.simpleName)
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(context!!, message)
    }

    private val updateProfileObserver = androidx.lifecycle.Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                context!!,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            if(!selectedSPecialization.equals("nothing"))userObj.profile.specialization = selectedSPecialization
            if(yearsCount > 0)userObj.profile.experience = binding.edYearsOfExperience.text.toString()


            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            //fragmentManager!!.popBackStack()
            //gotoNextFragment()
            decideRedirection()

        } else {
            CommonMethods.showToast(
                context!!,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private fun decideRedirection() {
        if (Constants.IS_ACC_SETUP_MODE) {
            changeProfileCOmpletionLevel(3)
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                EduBackgroundNovFragment(), this, false
            )
        } else {
            //showToast(FourBaseCareApp.activityFromApp, "Data updated successfully!")
            fragmentManager?.popBackStack()
        }
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
                    if (heightDiff > 300) {
                        showHideBottomBar(true)
                    } else {
                        showHideBottomBar(false)
                    }
                }
            })
    }

    fun showHideBottomBar(shouldHide : Boolean){
        if(shouldHide){
            CommonMethods.showLog("button_height", "tvsave gone")
                binding.relContinue.visibility = View.GONE
                binding.tvSave.visibility = View.GONE

        }else{
            if(!Constants.IS_ACC_SETUP_MODE){
                binding.relContinue.visibility = View.GONE
                binding.tvSave.visibility = View.VISIBLE
                CommonMethods.showLog("button_height", "tvsave VIsible")
            }else{
                CommonMethods.showLog("button_height", "tvsave gone")
                binding.relContinue.visibility = View.VISIBLE
            }
        }
    }
}