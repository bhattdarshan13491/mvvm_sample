package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.RegisterVMRepository
import com.oncobuddy.app.models.factory_providers.RegistrationFactoryProvider
import com.oncobuddy.app.models.mvvm_implementors.LoginVMImplementor
import com.oncobuddy.app.models.mvvm_implementors.RegistrationVMImplementor


object Registrationnjection {

    private val REGISTRATION_VM_IMPLEMENTOR: RegistrationVMImplementor = RegisterVMRepository()

    private val regViewModelFactory = RegistrationFactoryProvider(REGISTRATION_VM_IMPLEMENTOR)


    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return regViewModelFactory
    }

}