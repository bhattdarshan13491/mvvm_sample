package com.oncobuddy.app.models.mvvm_implementors

import com.oncobuddy.app.models.api_repositories.VerifyOTPVMRepository
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput


interface VerifyOTPVMImplementor : BaseImplementor{

    fun generateOTP(generateOTPInput: GenerateOTPInput,
                       callback: VerifyOTPVMRepository.ApiCallBack<BaseResponse?>)

    fun forgotPassword(forgotPwdInput: ForgotPwdInput,
                  callback: VerifyOTPVMRepository.ApiCallBack<BaseResponse?>)

}