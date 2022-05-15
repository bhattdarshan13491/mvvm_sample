package com.oncobuddy.app.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.*
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.chats.MessageInput
import com.oncobuddy.app.models.pojo.chats.chat_groups.ChatGroupResponse
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatListResponse
import com.oncobuddy.app.models.pojo.doctor_update.DoctorAvailabilityReq

class ChatsViewModel(private val chatVMImplementor: ChatVMImplementor) : ViewModel() {


    private val _liveGroupListResponse = MutableLiveData<ChatGroupResponse>()
    val groupListResonseData: LiveData<ChatGroupResponse> = _liveGroupListResponse

    private val _liveSendMessageResponse = MutableLiveData<BaseResponse>()
    val sendMessageResonseData: LiveData<BaseResponse> = _liveSendMessageResponse

    private val _liveStartCHatResponse = MutableLiveData<BaseResponse>()
    val startCHatResponseData: LiveData<BaseResponse> = _liveStartCHatResponse

    private val _liveChatListResponse = MutableLiveData<ChatListResponse>()
    val chatListResonseData: LiveData<ChatListResponse> = _liveChatListResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError


    fun callGetChatGroupsList(token: String) {
        _isViewLoading.postValue(true)
        chatVMImplementor.getCHatGroups(
            token,
            object : ChatVMRepository.ChatGroupsApiCallBack<ChatGroupResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: ChatGroupResponse?) {
                    _isViewLoading.postValue(false)
                    _liveGroupListResponse.postValue(responseData)
                }

            })
    }

    fun callGetChatList(token: String, groupId: String) {
        _isViewLoading.postValue(true)
        chatVMImplementor.getCHatList(
            token, groupId,
            object : ChatVMRepository.ChatListApiCallBack<ChatListResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: ChatListResponse?) {
                    _isViewLoading.postValue(false)
                    _liveChatListResponse.postValue(responseData)
                }

            })
    }

    fun callSendMessage(token: String, messageInput: MessageInput) {
        _isViewLoading.postValue(true)
        chatVMImplementor.sendMessage(
            token, messageInput,
            object : ChatVMRepository.SendMessageApiCallBack<BaseResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveSendMessageResponse.postValue(responseData)
                }

            })
    }

    fun callStartChat(token: String, receiverId: String) {
        _isViewLoading.postValue(true)
        chatVMImplementor.startChat(
            token, receiverId,
            object : ChatVMRepository.StartCHatApiCallBack<BaseResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveStartCHatResponse.postValue(responseData)
                }

            })
    }

}