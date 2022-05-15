package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ForumsVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel


class ForumsFactoryProvider(private val forumsVMImplementor: ForumsVMImplementor) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return ForumsViewModel(forumsVMImplementor) as T
    }

}