package com.oncobuddy.app.models.factory_providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.models.api_repositories.ChatVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel


class ChatFactoryProvider(private val chatVMImplementor: ChatVMImplementor) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
              return ChatsViewModel(chatVMImplementor) as T
    }

}