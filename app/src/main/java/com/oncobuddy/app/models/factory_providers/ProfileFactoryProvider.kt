package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.view_models.ProfileViewModel


class ProfileFactoryProvider(private val profileVMImplementor: ProfileVMImplementor) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return ProfileViewModel(profileVMImplementor) as T
    }

}