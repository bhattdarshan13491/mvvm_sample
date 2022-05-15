
package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.RecrodsVMImplementor
import com.oncobuddy.app.view_models.*


class RecordsFactoryProvider(private val vmImplementor: RecrodsVMImplementor)
    : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return RecordsViewModel(vmImplementor) as T
    }

}