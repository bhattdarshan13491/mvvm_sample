package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.views.activities.NovDoctorDetailsActivity
import com.oncobuddy.app.views.adapters.VerticalAlliedDoctorsAdapter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Vertical allied list fragment
 * Temporary setup to show all allied care doctors without category
 * @constructor Create empty Vertical allied list fragment
 */

class VerticalAlliedListFragment : BaseFragment(), VerticalAlliedDoctorsAdapter.Interaction{

    private lateinit var binding : FragmentChatListBinding
    private lateinit var chatListAdapter: VerticalAlliedDoctorsAdapter
    private lateinit var youtubeArrayList: ArrayList<YoutubeVideo>
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
            R.layout.fragment_chat_list, container, false
        )
        //binding.tvTitle.setText("App guiding videos")
        //binding.edSearch.visibility = View.GONE

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
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
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

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            chatListAdapter = VerticalAlliedDoctorsAdapter(youtubeArrayList, this@VerticalAlliedListFragment)
            adapter = chatListAdapter
            chatListAdapter.submitList(youtubeArrayList)
        }
    }

    /*override fun onVideoSelected(position: Int, item: YoutubeVideo, view: View) {
        val intent = Intent(FourBaseCareApp.activityFromApp, AppHelpVideoViewerActivity::class.java)
        intent.putExtra(Constants.YOUTUBE_URL, item.videoUrl)
        startActivity(intent)
        FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
    }*/

    override fun onDoctorSelected(position: Int, item: YoutubeVideo, view: View) {
        CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        if(!isDoubleClick()){
            val response = "{\"success\":true,\"message\":\"doctor details\",\"payLoad\":{\"doctorId\":4,\"firstName\":\"Darshan Bhatt Doctor\",\"lastName\":\"\",\"phoneNumber\":\"8780650576\",\"email\":\"doctor@gmail.com\",\"hospital\":\"Asian Hospital\",\"consultationFee\":100.0,\"specialization\":\"Onco\",\"designation\":\"HOD\",\"category\":{\"name\":\"Specialist\",\"id\":4},\"subCategory\":{\"name\":\"Oncologist\",\"id\":1},\"verified\":true,\"dpLink\":\"https://4care-dev.s3.ap-south-1.amazonaws.com/4/1628761625326-cropped892472330737371020.jpg\",\"experience\":\"1\",\"description\":null}}"
            val gson = Gson()
            val responseObserver: DoctorDetailsResponse = gson.fromJson(response, DoctorDetailsResponse::class.java)

            val intent = Intent(FourBaseCareApp.activityFromApp, NovDoctorDetailsActivity::class.java)
            intent.putExtra(Constants.DOCTOR_DATA, responseObserver.payLoad)
            startActivityForResult(intent, Constants.DOCTOR_DETAILS_RESULT)
            FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
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