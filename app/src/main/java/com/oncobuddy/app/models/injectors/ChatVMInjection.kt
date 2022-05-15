package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ChatVMImplementor
import com.oncobuddy.app.models.api_repositories.ChatVMRepository
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMRepository
import com.oncobuddy.app.models.factory_providers.ChatFactoryProvider
import com.oncobuddy.app.models.factory_providers.ProfileFactoryProvider


object ChatVMInjection {

    private val CHAT_VM_IMPLEMENTOR: ChatVMImplementor = ChatVMRepository()

    private val chatViewModelFactory = ChatFactoryProvider(CHAT_VM_IMPLEMENTOR)


    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return chatViewModelFactory
    }

}