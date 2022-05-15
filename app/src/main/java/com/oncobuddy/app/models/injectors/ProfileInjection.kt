package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMRepository
import com.oncobuddy.app.models.factory_providers.ProfileFactoryProvider


object ProfileInjection {

    private val PROFILE_VM_IMPLEMENTOR: ProfileVMImplementor = ProfileVMRepository()

    private val profileViewModelFactory = ProfileFactoryProvider(PROFILE_VM_IMPLEMENTOR)


    @JvmStatic
    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return profileViewModelFactory
    }

}