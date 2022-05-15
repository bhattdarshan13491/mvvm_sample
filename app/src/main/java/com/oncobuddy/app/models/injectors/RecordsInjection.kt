package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.*
import com.oncobuddy.app.models.factory_providers.*


object RecordsInjection {

    private val RECORDS_VM_IMPLEMENTOR: RecrodsVMImplementor = RecordsVMRepository()

    private val viewModelFactory = RecordsFactoryProvider(RECORDS_VM_IMPLEMENTOR)


    @JvmStatic
    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return viewModelFactory
    }

}