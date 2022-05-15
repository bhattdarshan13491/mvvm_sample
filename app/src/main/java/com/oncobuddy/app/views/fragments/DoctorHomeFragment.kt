package com.oncobuddy.app.views.fragments

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.FragmentDoctorHomeScreenBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.ProfileStatus
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.profile.WalletBalanceResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.AskExpertsAdapter
import com.oncobuddy.app.views.adapters.CommunityPostsAdapter
import com.oncobuddy.app.views.adapters.ExpertQuestionsAdapter
import com.oncobuddy.app.views.adapters.ProgressTrackerAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.view.Gravity
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.R
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.view_models.AppointmentViewModel
import retrofit2.Response


class DoctorHomeFragment : BaseFragment(), ExpertQuestionsAdapter.Interaction, CommunityPostsAdapter.Interaction{

    private lateinit var binding: FragmentDoctorHomeScreenBinding
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var expertQuestionsAdapter: ExpertQuestionsAdapter
    private lateinit var oncoDiscussionsAdapter: CommunityPostsAdapter
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var walletBalance : Double = 0.0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_doctor_home_screen, container, false
        )
        setupClickListeners()
        setupProfileCOmpletionView()
        //changeProfileCOmpletionLevel(6)
        setProgressRecyclerView()
        //setupPatientQueriesAdapter()
        setupTitleText()
        setupVM()
        setupObservers()
        getDataFromServer("",0)
        getWalletBalanceFromServer()
        getTodayApptFromServer()
    }

    private fun getWalletBalanceFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getWalletBalance(
                getUserAuthToken()
            )
        }
    }

    private fun setupObservers() {
        forumsViewModel.getHomePatientQueriesResponseData.observe(this, patientQueriesResponseObserver)
        forumsViewModel.getHomeOncoDiscussionsResponseData.observe(this, oncoDIscussionsResponseObserver)
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        appointmentViewModel.appointmentsList.observe(this, responseObserver)

        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)

        profileViewModel.walletBalanceResponse.observe(this, walletBalanceResponse)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val likePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer("",0)
        }

    }

    private fun setupVM() {
        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)

        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

        appointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)

    }

    private fun getTodayApptFromServer() {
        appointmentViewModel.callGetAppointment(false,true,getUserAuthToken(), getUserIdd().toString())
    }

    private fun getDataFromServer(searchKey: String, pageNo: Int) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("api_call_log", "called")
            var searchQueryInput = SearchQueryInput()
            searchQueryInput.pageSize = 5
            searchQueryInput.reqPageNo = pageNo
            searchQueryInput.searchKey = searchKey
            searchQueryInput.sortBy = "id"
            searchQueryInput.sortOrder = "Desc"

            Timer().schedule(Constants.FUNCTION_DELAY) {
                forumsViewModel.callGetHomePatientQueries(
                    getUserAuthToken(),
                    searchQueryInput
                )

                forumsViewModel.callGetHomeOncoDiscussions(
                    getUserAuthToken(),
                    searchQueryInput
                )
            }
        }
    }



    private fun setupProfileCOmpletionView() {
        if (getUserObject().profile.profileCompletionLevel == 6) {
            binding.linProfileStatus.visibility = View.GONE
        } else {
            binding.linProfileStatus.visibility = View.VISIBLE
            setProgressRecyclerView()
        }
    }



    private fun setupTitleText() {

        binding.tvWelcome.setText("Good "+getTimeSlot()+", Dr. "+getUserObject().firstName)

        if(!getUserObject().numberOfPatientsForADoctor.isNullOrEmpty()){
            binding.tvNoOfPatients.setText(getUserObject().numberOfPatientsForADoctor)
        }

        if(!getUserObject().dpLink.isNullOrEmpty() && !FourBaseCareApp.activityFromApp.isDestroyed){
            Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink).
            placeholder(R.drawable.ic_user_image).error(R.drawable.ic_user_image).circleCrop().into(binding.ivProfile)
            Log.d("img_log","Set image")
        }else{
            Log.d("img_log","Is empty")
        }
    }

    private fun getTimeSlot(): String {
        val dt = Date()
        val hours = dt.hours
        Log.d("time_slot_lg",""+hours)
        if (hours >= 1 && hours <= 12) {
            return "morning"
        } else if (hours >= 12 && hours <= 16) {
            return "afternoon"
        } else if (hours >= 16 && hours <= 24) {
            return "evening"
        } else
            return "morning"
    }

    private fun setupClickListeners() {

        binding.relPatients.setOnClickListener(View.OnClickListener {
            val intent = Intent("action_open_patients")
            LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp).sendBroadcast(intent)
        })

        binding.relAppointments.setOnClickListener(View.OnClickListener {
            val intent = Intent("action_open_appointments")
            LocalBroadcastManager.getInstance(FourBaseCareApp.activityFromApp).sendBroadcast(intent)
        })

        binding.tvWithDraw.setOnClickListener(View.OnClickListener {
            Log.d("WALLET", "withdraw clicked")
            if(!isDoubleClick()) withdrawBalance()
        })

        binding.tvTransactionHistory.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DrTransactionHistoryFragment(), this, false
                )
            }
        })

        binding.ivChat.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    ChatListingFragment(), this, false
                )
            }
        })

        binding.ivSupport.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    ContactSupportFragment(), this, false
                )
            }
        })

        binding.ivNotification.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    NotificationFragment(), this, false
                )
            }
        })

        binding.tvCOmplete.setOnClickListener(View.OnClickListener {
            /*CommonMethods.changeActivity(
                FourBaseCareApp.activityFromApp,
                DoctorAccountSetupNovActivity::class.java, false, false
            )*/
            if(!isDoubleClick()){
                Constants.IS_ACC_SETUP_MODE = true
                val completionLevel = getUserObject().profile.profileCompletionLevel
                Log.d("completion_level","level "+completionLevel)

                var fragment = Fragment()

                if(completionLevel == 0){
                    fragment = DoctorPersonalInfoFragment()
                }else if(completionLevel == 1){
                    fragment = DocExpAndSpecializationNovFragment()
                }else if(completionLevel == 2){
                    fragment = EduBackgroundNovFragment()
                }else if(completionLevel == 3){
                    fragment = DocEditEstablishmentFragment()
                }else if(completionLevel == 4){
                    fragment = DocVerifyNovFragment()
                }else if(completionLevel == 5){
                    fragment = VirtualConsultNovFragment()
                }
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }
        })

        binding.ivProfile.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    NovDoctorProfileFragment(), this, false
                )
            }
        })

        binding.tvAllPatientQUeries.setOnClickListener(View.OnClickListener {

                if(!isDoubleClick()){
                    var bundle = Bundle()
                    bundle.putString(Constants.SOURCE, Constants.EXPERT_QUESTIONS_LIST)
                    var fragment = NovOncoFragment()
                    fragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        fragment, this, false
                    )
                }

        })

        binding.tvAllDiscussions.setOnClickListener(View.OnClickListener {
                if(!isDoubleClick()){
                    var bundle = Bundle()
                    bundle.putString(Constants.SOURCE, Constants.QUESTION_LIST)
                    var fragment = NovOncoFragment()
                    fragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        fragment, this, false
                    )
                }
        })

    }

    private fun setProgressRecyclerView() {
        var profileStatus = ArrayList<ProfileStatus>()

        Log.d("profile_completion_level", ""+getUserObject().profile.profileCompletionLevel)

        for (i in 0..5) {
            if(i < getUserObject().profile.profileCompletionLevel){
                profileStatus.add(ProfileStatus(i, false))
                Log.d("profile_completion_level", "pending false "+i)
            }else{
                profileStatus.add(ProfileStatus(i, true))
                Log.d("profile_completion_level", "pending true "+i)
            }

        }

        binding.rvProfileStatus.apply {
            layoutManager = LinearLayoutManager(
                FourBaseCareApp.activityFromApp,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            var progressTrackerAdapter = ProgressTrackerAdapter(profileStatus, getUserObject().profile.profileCompletionLevel)
            adapter = progressTrackerAdapter
            progressTrackerAdapter.submitList(profileStatus)
        }
    }



    private fun setupPatientQueriesAdapter() {
        var expertQuestions = ArrayList<YoutubeVideo>()
        for (i in 0..5) {
            expertQuestions.add(YoutubeVideo())
        }
        binding.rvPatientQueries.apply {
            layoutManager = LinearLayoutManager(activity)
            var experQuestionsAdapter = AskExpertsAdapter(expertQuestions, null)
            adapter = experQuestionsAdapter
            experQuestionsAdapter.submitList(expertQuestions)
        }
    }

    private fun withdrawBalance(){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.withdrawBalance(getUserAuthToken())
        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            Log.d("hidden_log","Called")
            setProgressRecyclerView()
        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        Log.d("appointment_log", "is_loading is " + isLoading)
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        Log.d("appointment_log", "Error " + message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val walletBalanceResponse = androidx.lifecycle.Observer<WalletBalanceResponse?> {
        if(it?.isSuccess == true){
            if(!it?.userWallet?.balance!!.equals("null")){
                val gson = Gson()
                walletBalance = gson.toJson(it?.userWallet?.balance).toDouble()/100
                val solution:Double = String.format("%.1f", walletBalance).toDouble()
                binding.tvWalletBalance.setText("\u20B9 "+solution)
                FourBaseCareApp.savePreferenceDataString(Constants.PREF_WALLET_BALANCE, "\u20B9 "+solution)
            }

        }
    }

    private val patientQueriesResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("home_trending_video","3")
        if(!responseObserver.isNullOrEmpty()){
            //binding.relTrendingVideosTitle.visibility = View.VISIBLE
            Log.d("home_trending_video","4")
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            var subList = ArrayList<TrendingVideoDetails>()
            var count  = 0
            for(obj in arrayList){
                if(count < 1){
                    subList.add(obj)
                    count++
                }
                else
                    break
            }

            //setupOncoDiscussionsRecyclerView(subList)
            setupPatientQueriesRecyclerView(subList)
            binding.linQueriesContainer.visibility = View.VISIBLE
        //setupExpertQuestionsAdapter()
            //binding.relTrendingVideosTitle.setText(CommonMethods.getStringWithOnePadding(""+arrayList.size))
        }
        else{
            Log.d("home_trending_video","NO videos")
            //binding.relTrendingVideosTitle.visibility = View.GONE
            binding.linQueriesContainer.visibility = View.GONE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val responseObserver = androidx.lifecycle.Observer<Response<AppointmentsListResponse?>>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if(responseObserver.isSuccessful){
            val arrayList = responseObserver.body()?.payLoad
            Log.d("list_log","total size "+arrayList?.size)
            binding.tvTodayApptCount.text = ""+arrayList?.size
        }

    }

    private val oncoDIscussionsResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("home_trending_video","3")
        if(!responseObserver.isNullOrEmpty()){
            //binding.relTrendingVideosTitle.visibility = View.VISIBLE
            Log.d("home_trending_video","4")
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)

            var subList = ArrayList<TrendingVideoDetails>()
            var count  = 0
            for(obj in arrayList){
                if(count < 1){
                    subList.add(obj)
                    count++
                }
                else
                    break
            }
            setupOncoDiscussionsRecyclerView(subList)
            binding.linDiscussionsContainer.visibility = View.VISIBLE
        }
        else{
            Log.d("home_trending_video","NO videos")
            binding.linDiscussionsContainer.visibility = View.GONE
        //binding.relTrendingVideosTitle.visibility = View.GONE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun setupPatientQueriesRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        binding.rvPatientQueries.apply {
            layoutManager = LinearLayoutManager(activity)
            //(layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL

            var count = 0
            var subList = ArrayList<TrendingVideoDetails>()
            for(obj in list){
                if(count < 1){
                    subList.add(obj)
                    count++
                }
                else
                    break
            }

            expertQuestionsAdapter = ExpertQuestionsAdapter(subList, this@DoctorHomeFragment)
            adapter = expertQuestionsAdapter
            Log.d("pagination_log", "Server list size "+subList.size)
            expertQuestionsAdapter.submitList(subList)
        }
    }

    private fun setupOncoDiscussionsRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        binding.rvOncoDiscussions.apply {
            layoutManager = LinearLayoutManager(activity)
            var count = 0
            var subList = ArrayList<TrendingVideoDetails>()
            for(obj in list){
                if(count<1){
                    subList.add(obj)
                    count++
                }
                else
                    break
            }

            oncoDiscussionsAdapter = CommunityPostsAdapter(subList, this@DoctorHomeFragment)
            adapter = oncoDiscussionsAdapter
            Log.d("pagination_log", "Server list size "+subList.size)
            oncoDiscussionsAdapter.submitList(subList)
        }
    }

    override fun onExpertQuestionSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {
            Log.d("expert_log", "0 " + view.id)
            if (view.id === R.id.linLike) {
                Log.d("expert_log", "1")
                likePost(trendingBlogsDetails = item)

            } else if (view.id === R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great post\n\n" + item.content + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }else if (view.id == R.id.linCOmments) {
                var bundle = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ, item)

                var questionDetailsFragment = QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, questionDetailsFragment, this, false)
            }
        }
    }

    override fun onCOmmunityPostSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {
            Log.d("expert_log", "0 " + view.id)
            if (view.id === R.id.linLike) {
                Log.d("expert_log", "1")
                likePost(trendingBlogsDetails = item)

            } else if (view.id === R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great post\n\n" + item.content + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }else if (view.id == R.id.linCOmments) {
                var bundle = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ, item)

                var questionDetailsFragment = QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, questionDetailsFragment, this, false)
            }
        }
    }

    private fun likePost(trendingBlogsDetails:TrendingVideoDetails){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), ""+trendingBlogsDetails.post.id)
        }

    }


}