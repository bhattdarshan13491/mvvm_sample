package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.mvvm_implementors.LoginVMImplementor
import com.oncobuddy.app.view_models.LoginViewModel


class LoginFactoryProvider(private val LoginVMImplementor: LoginVMImplementor) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return LoginViewModel(LoginVMImplementor) as T
    }

}