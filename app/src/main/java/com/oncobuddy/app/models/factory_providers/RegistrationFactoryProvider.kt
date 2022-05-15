
package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.mvvm_implementors.RegistrationVMImplementor
import com.oncobuddy.app.view_models.RegistrationViewModel


class RegistrationFactoryProvider(private val registrationVMImplementor: RegistrationVMImplementor) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return RegistrationViewModel(registrationVMImplementor) as T
    }

}