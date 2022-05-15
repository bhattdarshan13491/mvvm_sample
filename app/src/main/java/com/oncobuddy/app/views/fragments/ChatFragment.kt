package com.oncobuddy.app.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentNovChatBinding
import com.oncobuddy.app.models.injectors.ChatVMInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.chats.MessageInput
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatListResponse
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatMessage
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.views.adapters.ChatAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Chat fragment
 * SHows one to one chat between users
 * @constructor Create empty Chat fragment
 */

class ChatFragment : BaseFragment(), ChatAdapter.Interaction{

    private lateinit var binding : FragmentNovChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<ChatMessage>
    private lateinit var chatViewModel: ChatsViewModel
    private lateinit var receiverId : String
    private lateinit var receiverPhotoUrl : String
    private lateinit var receiverName : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nov_chat, container, false
        )
        receiverId = arguments?.getString(Constants.CHAT_GROUP_ID).toString()
        receiverPhotoUrl = arguments?.getString(Constants.CHAT_URL).toString()
        receiverName = arguments?.getString(Constants.CHAT_NAME).toString()
        Log.d("chat_list","receiverid "+receiverId)

        binding.tvUserName.setText(receiverName)
        if(::receiverPhotoUrl.isInitialized && !receiverPhotoUrl.isNullOrEmpty()){
            Glide.with(FourBaseCareApp.activityFromApp).load(receiverPhotoUrl).placeholder(R.drawable.ic_user_image).transform(
                CircleCrop()
            ).into(binding.ivProfileImage)
        }else{
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_user_image).placeholder(R.drawable.ic_user_image).transform(
                CircleCrop()
            ).into(binding.ivProfileImage)
        }

        setupVM()
        setupObservers()
        setClickListeners()
        getCHatListingFromServer()
    }

    private fun getCHatListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            chatViewModel.callGetChatList(getUserAuthToken(), receiverId)
        }
    }

    private fun sendChatMessageToServer() {
        if(!isDoubleClick()){
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                if(getTrimmedText(binding.etMessage).isNullOrEmpty()){
                    showToast(FourBaseCareApp.activityFromApp, "Please enter your message")
                }else{
                    var messageInput = MessageInput()
                    messageInput.message = getTrimmedText(binding.etMessage)
                    messageInput.groupId = receiverId.toInt()
                    chatViewModel.callSendMessage(getUserAuthToken(), messageInput)
                }
            }
        }
    }

    private fun setupVM() {
        chatViewModel = ViewModelProvider(
            this,
            ChatVMInjection.provideViewModelFactory()
        ).get(ChatsViewModel::class.java)
    }

    private fun setupObservers() {
        chatViewModel.chatListResonseData.observe(this, chatListResponseObserver)
        chatViewModel.sendMessageResonseData.observe(this, sendMessageResponseObserver)
        chatViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        chatViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivSend.setOnClickListener(View.OnClickListener {
            sendChatMessageToServer()
        })

    }

    private fun setRecyclerView() {
        if(chatList != null && chatList.size > 0){
            showHideNoData(false)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                Log.d("chat_aadapter","Left "+getUserObject().dpLink)
                Log.d("chat_aadapter","Left "+receiverPhotoUrl)
                chatAdapter = ChatAdapter(chatList,this@ChatFragment, getUserIdd(),getUserObject().dpLink, receiverPhotoUrl)
                adapter = chatAdapter
                chatAdapter.submitList(chatList)
            }
            binding.recyclerView.smoothScrollToPosition(chatList.size - 1)
        }else{
            showHideNoData(true)
        }
    }

    private fun showHideNoData(shouldShowNoData: Boolean){
        if(shouldShowNoData){
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }




    override fun onChatMsgSelected(position: Int, item: YoutubeVideo, view: View) {
        //
    }
    // Setup Observers
    private val chatListResponseObserver = androidx.lifecycle.Observer<ChatListResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            chatList = ArrayList()
            chatList.addAll(responseObserver.payLoad)
            setRecyclerView()
        }
        binding.executePendingBindings()
        binding.invalidateAll()

    }

    private val sendMessageResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
            binding.etMessage.setText("")
            getCHatListingFromServer()
            Constants.IS_LIST_UPDATED = true
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("chat_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }





}