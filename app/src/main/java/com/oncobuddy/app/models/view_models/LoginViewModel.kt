package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.LoginVMRepository
import com.oncobuddy.app.models.mvvm_implementors.LoginVMImplementor
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import retrofit2.Response

class LoginViewModel(private val LoginVMImplementor: LoginVMImplementor) : ViewModel() {

    private val _liveLoginResponse = MutableLiveData<Response<LoginResponse?>>()
    val loginResonseData: LiveData<Response<LoginResponse?>> = _liveLoginResponse

    private val _liveGetOtpResponse = MutableLiveData<Response<BaseResponse?>>()
    val otpResonseData: LiveData<Response<BaseResponse?>> = _liveGetOtpResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError


    fun retiveLoginData(loginInput: LoginInput) {
        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        LoginVMImplementor.retriveLoginData(
            loginInput,
            object : LoginVMRepository.ApiCallBack<LoginResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")

                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: Response<LoginResponse?>) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveLoginResponse.postValue(responseData)
                }

            })
    }



    fun verifyOtp(verifyLoginOtpInput: VerifyLoginOtpInput) {
        _isViewLoading.postValue(true)
        LoginVMImplementor.verifyOTP(
            verifyLoginOtpInput,
            object : LoginVMRepository.ApiCallBack<LoginResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")

                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: Response<LoginResponse?>) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveLoginResponse.postValue(responseData)
                }

            })
    }

    fun getOtp(loginInput: com.oncobuddy.app.models.pojo.initial_login.LoginInput, isFOrRegistration: Boolean = false) {
        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        LoginVMImplementor.getOTPForLogin(
            loginInput,
            object : LoginVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")

                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: Response<BaseResponse?>) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveGetOtpResponse.postValue(responseData)
                }

            }, isFOrRegistration)
    }

}