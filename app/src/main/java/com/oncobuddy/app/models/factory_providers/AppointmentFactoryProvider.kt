
package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.mvvm_implementors.AppointmentVMImplementor
import com.oncobuddy.app.view_models.AppointmentViewModel


class AppointmentFactoryProvider(private val appointmentVMImplementor: AppointmentVMImplementor)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return AppointmentViewModel(appointmentVMImplementor) as T
    }

}