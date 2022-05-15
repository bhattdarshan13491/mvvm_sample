package com.oncobuddy.app.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.models.injectors.ChatVMInjection
import com.oncobuddy.app.models.pojo.chats.chat_groups.ChatGroupResponse
import com.oncobuddy.app.models.pojo.chats.chat_groups.GroupDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.views.adapters.ChatListAdapter
import java.util.*
import kotlin.collections.ArrayList


/**
 * Chat listing fragment
 * Handles chat listing for the user
 * @constructor Create empty Chat listing fragment
 */

class ChatListingFragment : BaseFragment(), ChatListAdapter.Interaction{

    private lateinit var binding : FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatGroupList: ArrayList<GroupDetails>
    private lateinit var chatViewModel: ChatsViewModel
    private var IS_FROM_APPOINTMENTS = false



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat_list, container, false
        )
        setupVM()
        setupObservers()
        setClickListeners()
        getCHatGroupListingFromServer()



    }

    private fun setupVM() {
        chatViewModel = ViewModelProvider(
            this,
            ChatVMInjection.provideViewModelFactory()
        ).get(ChatsViewModel::class.java)
    }

    private fun setupObservers() {
        chatViewModel.groupListResonseData.observe(this, chatGroupListResponseObserver)
        chatViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        chatViewModel.onMessageError.observe(this, errorMessageObserver)
    }


    private fun getCHatGroupListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            chatViewModel.callGetChatGroupsList(getUserAuthToken())
        }
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
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



    private fun setRecyclerView() {
        if(chatGroupList != null && chatGroupList.size > 0){
            showHideNoData(false)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                chatListAdapter = ChatListAdapter(this@ChatListingFragment)
                adapter = chatListAdapter
                chatListAdapter.submitList(chatGroupList)
            }
        }else{
            showHideNoData(true)
        }
    }

    override fun onChatSelected(position: Int, item: GroupDetails, view: View) {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        if(!isDoubleClick()){

            gotoChatScreen(item)
        }
    }

    private fun gotoChatScreen(item: GroupDetails) {
        var bundle = Bundle()
        bundle.putString(Constants.CHAT_GROUP_ID, "" + item.chatGroupId)
        bundle.putString(Constants.CHAT_NAME, "" + item.userDisplayName)
        bundle.putString(Constants.CHAT_URL, "" + item.userDisplayPic)
        Log.d("group_id","chat Group id "+item.chatGroupId)
        Log.d("group_id","chat Group name "+item.userDisplayName)
        Log.d("group_id","chat Group pic "+item.userDisplayPic)
        var chatFragment = ChatFragment()
        chatFragment.arguments = bundle
        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, chatFragment, this, false)
    }

    // Setup Observers
    private val chatGroupListResponseObserver = androidx.lifecycle.Observer<ChatGroupResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            chatGroupList = ArrayList()
            chatGroupList.addAll(responseObserver.payLoad)
            setRecyclerView()

            if(arguments != null){
                var groupDetails = GroupDetails()
                var doctorId =  arguments?.getInt("user_id")
                for(chat in chatGroupList){
                    if(chat.userAppUserId == doctorId){
                        groupDetails = chat
                    }
                }
                Log.d("group_id","doctor id "+arguments?.getInt("user_id"))
                Log.d("group_id","chat Group id "+groupDetails.chatGroupId)
                Log.d("group_id","chat Group name "+groupDetails.userDisplayName)
                Log.d("group_id","chat Group pic "+groupDetails.userDisplayPic)

                if(groupDetails.chatGroupId != 0) gotoChatScreen(groupDetails)
            }
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


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            if(Constants.IS_LIST_UPDATED){
             getCHatGroupListingFromServer()
             Constants.IS_LIST_UPDATED = false
            }

        }
    }


}