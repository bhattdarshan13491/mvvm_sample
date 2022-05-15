package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.VerifyOTPVMRepository
import com.oncobuddy.app.models.factory_providers.VerifyOTPFactoryProvider
import com.oncobuddy.app.models.mvvm_implementors.VerifyOTPVMImplementor


object VerifyOtpInjection {

    private val OTP_VM_IMPLEMENTOR: VerifyOTPVMImplementor = VerifyOTPVMRepository()

    private val otpViewModelFactory = VerifyOTPFactoryProvider(OTP_VM_IMPLEMENTOR)


    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return otpViewModelFactory
    }

}