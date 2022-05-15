package com.oncobuddy.app.views.activities

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityResetPwd2Binding
import com.oncobuddy.app.models.injectors.VerifyOtpInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseActivity
import com.oncobuddy.app.view_models.VerifyOTPViewModel

/**
 * Nov reset pwd activity
 * It is useful for the reset of password
 * @constructor Create empty Nov reset pwd activity
 */

class NovResetPwdActivity : BaseActivity() {
    private lateinit var binding : ActivityResetPwd2Binding
    private lateinit var verifyOTPViewModel: VerifyOTPViewModel
    private var shouldShowPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun init() {
        binding = DataBindingUtil.setContentView(this@NovResetPwdActivity, R.layout.activity_reset_pwd_2)
        setupVM()
        setupObservers()
        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.ivShowHidePwd.setOnClickListener(View.OnClickListener {
            showHidePassword(binding.etPassword, binding.ivShowHidePwd, shouldShowPassword)
            shouldShowPassword = !shouldShowPassword
        })

        binding.tvGetStarted.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(this@NovResetPwdActivity) && isvalidInput()) {
                callResetPasswordAPI()
            }
        })
    }

    private fun callResetPasswordAPI() {
        var forgotPasswordInput = ForgotPwdInput()
        forgotPasswordInput.newPassword = getTrimmedText(binding.etConfirmPassword)
        forgotPasswordInput.phoneNumber = "" + intent.getStringExtra("phone_number")
        forgotPasswordInput.otp = "" + intent.getStringExtra("otp")
        verifyOTPViewModel.callVerifyOTP(forgotPasswordInput)
    }

    private fun isvalidInput() : Boolean {
        if (getTrimmedText(binding.etPassword).isNullOrEmpty()) {
            showToast("Please enter password")
            return false
        } else if (getTrimmedText(binding.etConfirmPassword).isNullOrEmpty()) {
            showToast("Please confirm your password")
            return false
        }else if(!getTrimmedText(binding.etPassword).equals(getTrimmedText(binding.etConfirmPassword))){
            showToast("New password and confirm password are not matching")
            return false
        }else{
            return true
        }
    }

    //API stuff
    private fun setupVM() {
        verifyOTPViewModel = ViewModelProvider(
            this,
            VerifyOtpInjection.provideViewModelFactory()
        ).get(VerifyOTPViewModel::class.java)
    }

    private fun setupObservers() {
        verifyOTPViewModel.forGotPwdResponseData.observe(this, forgotPwdResponseObserver)
        verifyOTPViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        verifyOTPViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val forgotPwdResponseObserver = Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Password changed successfully! Please login with new password"
            )
            finish()
        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem creating password."
            )
        }
    }


    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("login_log", "is_loading is "+isLoading)
        showHideProgress(isLoading,binding.layoutProgress.frProgress)
    }
    private val errorMessageObserver = Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    ////
}