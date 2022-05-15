package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ForumsVMImplementor
import com.oncobuddy.app.models.api_repositories.ForumsVMRepository
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMRepository
import com.oncobuddy.app.models.factory_providers.ForumsFactoryProvider
import com.oncobuddy.app.models.factory_providers.ProfileFactoryProvider


object ForumsInjection {

    private val FORUM_VM_IMPLEMENTOR: ForumsVMImplementor = ForumsVMRepository()

    private val forumsViewModelFactory = ForumsFactoryProvider(FORUM_VM_IMPLEMENTOR)


    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return forumsViewModelFactory
    }

}