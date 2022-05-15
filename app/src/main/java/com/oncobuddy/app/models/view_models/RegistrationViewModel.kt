package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.RegisterVMRepository
import com.oncobuddy.app.models.mvvm_implementors.RegistrationVMImplementor
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.nov_signup.SignupResponse
import com.oncobuddy.app.models.pojo.registration_process.NovRegistration
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import retrofit2.Response

class RegistrationViewModel(private val registrationVMImplementor: RegistrationVMImplementor) : ViewModel() {

    private val _liveRegistrationResponse = MutableLiveData<Response<SignupResponse?>>()
    val loginResonseData: LiveData<Response<SignupResponse?>> = _liveRegistrationResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError


    fun doPatientRegistration(registrationInput: NovRegistration) {
        Log.d("sign_up_log", "came here")
        _isViewLoading.postValue(true)
        registrationVMImplementor.addPatient(
            registrationInput,
            object : RegisterVMRepository.ApiCallBack<SignupResponse?> {


                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: Response<SignupResponse?>) {
                    Log.d("sign_up_log", "Success")
                    Log.d("sign_up_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveRegistrationResponse.postValue(responseData)
                }
            })
    }

}