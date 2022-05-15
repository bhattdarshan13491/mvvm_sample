package com.oncobuddy.app.models.mvvm_implementors

import com.oncobuddy.app.models.api_repositories.RegisterVMRepository
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.nov_signup.SignupResponse
import com.oncobuddy.app.models.pojo.registration_process.NovRegistration
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput


interface RegistrationVMImplementor : BaseImplementor{


    fun addPatient(registrationInput: NovRegistration,
                   callback: RegisterVMRepository.ApiCallBack<SignupResponse?>)

}