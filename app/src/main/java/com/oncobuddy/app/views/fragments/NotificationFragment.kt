package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAppHelpVideosListBinding
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.databinding.FragmentNotificationListBinding
import com.oncobuddy.app.databinding.FragmentNovChatBinding
import com.oncobuddy.app.models.pojo.Notification
import com.oncobuddy.app.models.pojo.NotificationObj
import com.oncobuddy.app.models.pojo.NovChat
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.activities.AppHelpVideoViewerActivity
import com.oncobuddy.app.views.adapters.*
import java.util.*
import kotlin.collections.ArrayList


class NotificationFragment : BaseFragment(), NotificationsAdapter.Interaction{

    private lateinit var binding : FragmentNotificationListBinding
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var notificationList: ArrayList<Notification>
    //private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notification_list, container, false
        )
        //binding.tvTitle.setText("App guiding videos")
        //binding.edSearch.visibility = View.GONE

        setClickListeners()
        getDoctorListingFromServer()
        setRecyclerView()

        binding.layoutHeader.tvTitle.setText("Notifications")


    }

    private fun getDoctorListingFromServer() {
        /*if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetMyDoctorListing(
                getUserAuthToken(), ""+getUserId()
            )
        }*/
    }

    /*private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        Log.d("api_log", "doctor list Result " + responseObserver.isSuccess)
        if (responseObserver.doctorList == null) {
            Log.d("api_log", "doctor list null ")
        } else {
            Log.d("api_log", "doctor list size " + responseObserver.doctorList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.isSuccess) {
            doctorList = ArrayList()
            doctorList.addAll(responseObserver.doctorList)
            setRecyclerView(doctorList)

        }

    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }*/

    private fun setClickListeners() {
        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun setRecyclerView() {
        notificationList = ArrayList()

        for(i in 1..11){
            var notificationObj = Notification()
            notificationObj.id = i
            notificationObj.notification = "Notification "+i
            if(i ==  1  || i % 5 == 0) notificationObj.header = true
            else notificationObj.header = false
            notificationList.add(notificationObj)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            notificationsAdapter = NotificationsAdapter(notificationList, this@NotificationFragment)
            adapter = notificationsAdapter
            notificationsAdapter.submitList(notificationList)
        }
    }

    override fun onNotificationSelected(position: Int, item: Notification, view: View) {

    }
}