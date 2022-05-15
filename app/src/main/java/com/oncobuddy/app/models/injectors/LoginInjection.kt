package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.LoginVMRepository
import com.oncobuddy.app.models.factory_providers.LoginFactoryProvider
import com.oncobuddy.app.models.mvvm_implementors.LoginVMImplementor


object LoginInjection {

    private val LOGIN_VM_IMPLEMENTOR: LoginVMImplementor = LoginVMRepository()

    private val loginViewModelFactory = LoginFactoryProvider(LOGIN_VM_IMPLEMENTOR)


    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return loginViewModelFactory
    }

}