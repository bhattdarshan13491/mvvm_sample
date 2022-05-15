
package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.mvvm_implementors.VerifyOTPVMImplementor
import com.oncobuddy.app.view_models.VerifyOTPViewModel


class VerifyOTPFactoryProvider(private val verifyOTPVMImplementor: VerifyOTPVMImplementor)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return VerifyOTPViewModel(verifyOTPVMImplementor) as T
    }

}