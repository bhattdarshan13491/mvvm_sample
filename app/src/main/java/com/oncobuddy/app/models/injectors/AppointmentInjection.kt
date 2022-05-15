package com.oncobuddy.app.models.injectors

import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.AppointmentVMRepository
import com.oncobuddy.app.models.factory_providers.AppointmentFactoryProvider
import com.oncobuddy.app.models.mvvm_implementors.AppointmentVMImplementor


object AppointmentInjection {

    private val APPOINTMENT_VM_IMPLEMENTOR: AppointmentVMImplementor = AppointmentVMRepository()

    private val appointmentViewModelFactory = AppointmentFactoryProvider(APPOINTMENT_VM_IMPLEMENTOR)


    @JvmStatic
    fun provideViewModelFactory(): ViewModelProvider.Factory{
        return appointmentViewModelFactory
    }

}