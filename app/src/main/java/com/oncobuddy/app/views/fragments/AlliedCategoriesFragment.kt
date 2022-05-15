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
import com.oncobuddy.app.databinding.FragmentAlliedCareBinding
import com.oncobuddy.app.databinding.FragmentAppHelpVideosListBinding
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.models.pojo.AlliedCategory
import com.oncobuddy.app.models.pojo.NovChat
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.activities.AppHelpVideoViewerActivity
import com.oncobuddy.app.views.adapters.AlliedCategoriesAdapter
import com.oncobuddy.app.views.adapters.AppHelpVideosAdapter
import com.oncobuddy.app.views.adapters.ChatListAdapter
import com.oncobuddy.app.views.adapters.NovChatAdapter
import java.util.*
import kotlin.collections.ArrayList


class AlliedCategoriesFragment : BaseFragment(), AlliedCategoriesAdapter.Interaction{

    private lateinit var binding : FragmentAlliedCareBinding
    private lateinit var alliedCategoriesAdapter: AlliedCategoriesAdapter
    private lateinit var youtubeArrayList: ArrayList<YoutubeVideo>
    private lateinit var alliedCategoryArrayList: ArrayList<AlliedCategory>
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
            R.layout.fragment_allied_care, container, false
        )
        setClickListeners()
        getDoctorListingFromServer()
        setRecyclerView()
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
  /*      binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })*/
    }




/*    private fun showHideNoData(shouldShowNoData: Boolean){
        if(shouldShowNoData){
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }*/


    private fun setRecyclerView() {
        youtubeArrayList = ArrayList()
        youtubeArrayList.add(YoutubeVideo(0,"How To Book An Appointment", "https://www.youtube.com/watch?v=3Ubl-lfrh5Q"))
        youtubeArrayList.add(YoutubeVideo(1,"How To Edit Records", "https://www.youtube.com/watch?v=jYw5kPCWGQA"))
        youtubeArrayList.add(YoutubeVideo(2,"How To Reschedule An Appointment", "https://www.youtube.com/watch?v=Rxa-TxvRZDQ"))
        youtubeArrayList.add(YoutubeVideo(3,"How to Upload Record", "https://www.youtube.com/watch?v=iiRhtsI92Lg"))


        alliedCategoryArrayList = ArrayList()
        for(i in 0..5){
            var alliedCategory = AlliedCategory()
            alliedCategory.categoryName = "Category "+i
            alliedCategory.doctorsList = youtubeArrayList
            alliedCategory.id = i
            alliedCategoryArrayList.add(alliedCategory)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            alliedCategoriesAdapter = AlliedCategoriesAdapter(alliedCategoryArrayList, this@AlliedCategoriesFragment)
            adapter = alliedCategoriesAdapter
            alliedCategoriesAdapter.submitList(alliedCategoryArrayList)
        }
    }

    /*override fun onVideoSelected(position: Int, item: YoutubeVideo, view: View) {
        val intent = Intent(FourBaseCareApp.activityFromApp, AppHelpVideoViewerActivity::class.java)
        intent.putExtra(Constants.YOUTUBE_URL, item.videoUrl)
        startActivity(intent)
        FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
    }*/

    /*override fun onChatSelected(position: Int, item: YoutubeVideo, view: View) {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        if(!isDoubleClick()){
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                ChatFragment(), this, false
            )
        }
    }*/



    override fun onCategorySelected(position: Int, item: AlliedCategory, view: View) {
        if(view.id == R.id.tvViewAll){
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                VerticalAlliedListFragment(), this, false
            )
        }
    }

/*
    override fun onItemSelected(position: Int, item: YoutubeVideo, view: View) {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        val intent = Intent(context, AddOrEditAppointmentFragment::class.java)
        intent.putExtra(Constants.DOCTOR_DATA, item)
        targetFragment!!.onActivityResult(Constants.FRAGMENT_SELECT_DOCTOR_RESULT, Activity.RESULT_OK, intent)
        fragmentManager!!.popBackStack()
    }
*/





}