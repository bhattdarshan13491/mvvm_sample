package com.oncobuddy.app.models.mvvm_implementors

import com.oncobuddy.app.models.api_repositories.LoginVMRepository
import com.oncobuddy.app.models.mvvm_implementors.BaseImplementor
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse

/**
 * An interface to connect Login repository with login VM
 */

interface LoginVMImplementor : BaseImplementor {


    fun retriveLoginData(loginInput: LoginInput,
                         callback: LoginVMRepository.ApiCallBack<LoginResponse?>)


    fun getOTPForLogin(loginInput: com.oncobuddy.app.models.pojo.initial_login.LoginInput,
                       callback: LoginVMRepository.BaseResponseApiCallBack<BaseResponse?>, isFOrRegistration: Boolean)

    fun verifyOTP(verifyLoginOtpInput: VerifyLoginOtpInput, callback: LoginVMRepository.ApiCallBack<LoginResponse?>)

}