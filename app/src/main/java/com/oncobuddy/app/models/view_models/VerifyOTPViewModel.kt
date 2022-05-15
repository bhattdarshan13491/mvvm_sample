package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.VerifyOTPVMRepository
import com.oncobuddy.app.models.mvvm_implementors.VerifyOTPVMImplementor
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput

class VerifyOTPViewModel(private val verifyOTPVMImplementor: VerifyOTPVMImplementor) : ViewModel() {

    private val _liveForgotPwdResponse = MutableLiveData<BaseResponse>()
    val forGotPwdResponseData: LiveData<BaseResponse> = _liveForgotPwdResponse

    private val _liveGenerateOtpResponse = MutableLiveData<BaseResponse>()
    val generateOtpResponseData: LiveData<BaseResponse> = _liveGenerateOtpResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError


    fun callVerifyOTP(forgotPwdInput: ForgotPwdInput) {

        Log.d("change_pwd_log","1")
        Log.d("change_pwd_log", "came here")
        _isViewLoading.postValue(true)
        verifyOTPVMImplementor.forgotPassword(
            forgotPwdInput,
            object : VerifyOTPVMRepository.ApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("change_pwd_log", "Success")
                    Log.d("change_pwd_log", "is_loading is 1")
                    Log.d("change_pwd_log","2")
                    _isViewLoading.postValue(false)
                    _liveForgotPwdResponse.postValue(responseData)
                }

                override fun onError(message: String?) {
                    Log.d("change_pwd_log", "Err")
                    Log.d("change_pwd_log","3")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun generateOTP(generateOTPInput: GenerateOTPInput) {

        Log.d("change_pwd_log", "came here")
        _isViewLoading.postValue(true)
        verifyOTPVMImplementor.generateOTP(
            generateOTPInput,
            object : VerifyOTPVMRepository.ApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("change_pwd_log", "Success")
                    Log.d("change_pwd_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveGenerateOtpResponse.postValue(responseData)
                }

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

}