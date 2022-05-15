package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentPatientHomeScreenBinding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.models.pojo.aws_credentials.AwsCredentialsResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.PostQuestionDto
import com.oncobuddy.app.models.pojo.forums.shorts.ShortDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.CommentsItem
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.patient_profile.PatientDetails
import com.oncobuddy.app.models.pojo.patient_profile.PatientDetailsResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.VideoViewerActivity
import com.oncobuddy.app.views.adapters.*
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.gson.Gson
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.ProfileStatus
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetailsByCGResponse
import com.oncobuddy.app.view_models.RecordsViewModel

class PatientHomeScreenNewFragment : BaseFragment(),
    LiveVideosListingAdapter.Interaction,
    LiveForumsListingAdapter.Interaction,
    ToodayAppointmentsNewAdapter.Interaction,
    DoctorListingAdapter.Interaction,
    ExpertQuestionsAdapter.Interaction,
    MyQuestionsListingAdapter.Interaction{

    private lateinit var binding: FragmentPatientHomeScreenBinding
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var liveVideosListingAdapter: LiveVideosListingAdapter
    private lateinit var myQuestionsListingAdapter: MyQuestionsListingAdapter
    private lateinit var liveForumsListingAdapter: LiveForumsListingAdapter
    private lateinit var expertQuestionsAdapter: ExpertQuestionsAdapter
    private lateinit var askQuestionDalogue: Dialog
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var apppointmentsList: ArrayList<AppointmentDetails>
    private lateinit var todayAppointmentsAdapter: ToodayAppointmentsNewAdapter
    private lateinit var doctorListingAdapter: DoctorListingAdapter
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var selectBottomDoctorDialog: FragmentModalBottomSheet
    private lateinit var findDoctorInputDialogue: Dialog
    private lateinit var bottomCameraOrGalleryDIalogue : FragmentModalBottomSheet
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var askOverlayPermissionDialogue: Dialog
    private var mCurrentPhotoPath: String? = null
    private lateinit var  FILE_PATH: String
    private var FILE_URL = ""
    private var ivQuestion: ImageView? = null
    private var cardAddImage: CardView? = null
    private var cardImage: CardView? = null
    private lateinit var selectedDoctor: Doctor
    private lateinit var responseConfirmationDialogue: Dialog
    private var IS_QOW = false
    private lateinit var qowObj: TrendingVideoDetails
    private lateinit var recordsViewModel: RecordsViewModel
    private val ADD_DOCUMENT_PERMISSION = 103
    private var IS_SHOWING_MENU = false
    private lateinit var deleteConfirmationDialogue: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("crash_log","Patient Home opened")
        init(inflater, container)
        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        setBinding(inflater, container)
        showAddQuePerRole()
        setupTitleText()
        setupVM()
        setupObservers()
        setupClickListeners()
        getDataFromServer()

        if(getUserObject().profile != null &&
            getUserObject().profile.patientType != null &&
            getUserObject().profile.patientType.equals(Constants.PATIENT_TYPE_TEST_TAKER)){
            binding.linGeneticReportCOntainer.visibility = View.VISIBLE
        }else{
            binding.linGeneticReportCOntainer.visibility = View.GONE
        }

        /*if(checkIFPatient() && checkIfDeviceNeedsOverlayPermission()){
            showOverlayPermissionDialogue()
        }*/
        logScreenViewEventMP("Home screen")
        setProgressRecyclerView()

        //setupExpertQuestionsAdapter()

    }

    private fun showAddQuePerRole() {
        if (checkIFPatient()){
            binding.ivNotification.visibility = View.VISIBLE
        }else{
            binding.ivNotification.visibility = View.GONE
        }
    }
    private fun setBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_patient_home_screen, container, false
        )
    }

    private fun setupVM() {

        recordsViewModel = ViewModelProvider(
            this,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)

        appointmentViewModel = ViewModelProvider(this, AppointmentInjection.provideViewModelFactory()).get(AppointmentViewModel::class.java)

        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)

        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    // Gets all needed data from the server for setting short note and, latest blog, video and question of the week

    private fun getDataFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    forumsViewModel.callGetShorts(getUserAuthToken())
                    forumsViewModel.callGetTrendingVideos(getUserAuthToken())
                    forumsViewModel.callGetTrendingBlogs(getUserAuthToken())
                    forumsViewModel.callQoW(getUserAuthToken())
                    getExpertQuestions()
                    getTodayApptFromServer()
                    // Will be called only when one of aws credentials is missing
                    if(FourBaseCareApp.sharedPreferences.getString(Constants.PREF_AWS_ACCESS_KEY, "").isNullOrEmpty() ||
                        FourBaseCareApp.sharedPreferences.getString(Constants.PREF_AWS_SECRET_KEY, "").isNullOrEmpty()){

                        profileViewModel.getAwsCredentials(getUserAuthToken())
                    }

                    if(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
                        profileViewModel.callGetPatientDetailsByCG(getUserAuthToken(), "")
                    }

                    /*if(checkIFPatient()){
                        profileViewModel.callGetMyDoctorListing(
                            getUserAuthToken(), ""+getUserIdd()
                        )
                    }*/
                }
            }


        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getExpertQuestions() {
        var searchQueryInput = SearchQueryInput()
        searchQueryInput.pageSize = 5
        searchQueryInput.reqPageNo = 0
        searchQueryInput.searchKey = ""
        searchQueryInput.sortBy = "id"
        searchQueryInput.sortOrder = "Desc"
        forumsViewModel.callGetAllVideos(
            Constants.EXPERT_QUESTIONS_LIST,
            getUserAuthToken(),
            searchQueryInput
        )
    }


    private fun getTodayApptFromServer() {
        Log.d("today_apt_log","0")
        appointmentViewModel.callGetAppointment(false,true,getUserAuthToken(), getUserIdd().toString())
    }

    private fun setupObservers() {
        //appointmentViewModel.appointmentsList.observe(this, todayListResponseObserver)
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        recordsViewModel.viewReportResponseData.observe(this, responseObserver)
        profileViewModel.findDoctorResonseData.observe(this, findDoctorResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        profileViewModel.patientDetailsByCGData.observe(this, patientDetailsObserver)
        //profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        profileViewModel.alertContactResonseData.observe(this,alertCOntactsObserver)
        profileViewModel.awsCredentialsResponseData.observe(this,awsKeysResponseObserver)
        forumsViewModel.getShortsResponseData.observe(this, shortsListResponseObserver)
        forumsViewModel.getBlogsResponseData.observe(this, blogsListResponseObserver)
        forumsViewModel.getVideosResponseData.observe(this, videosListResponseObserver)
        forumsViewModel.getHomeVideosResponseData.observe(this, homeVideosListResponseObserver)
        //forumsViewModel.getMyQuestionResponseData.observe(this, questionListResponseObserver)
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        forumsViewModel.savePostResponseData.observe(this, savePostResponseObserver)
        forumsViewModel.reportPostResponseData.observe(this, reportPostResponseObserver)
        forumsViewModel.postDeleteResponseData.observe(this, deletePostResponseObserver)
        forumsViewModel.addQuestionResponseData.observe(this, addQuestionResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
        forumsViewModel.patientDetailsResponseData.observe(this, patientDetailsResponseObserver)
        profileViewModel.connectionResonseData.observe(this, connectionResponseObserver)
        forumsViewModel.getQoWResponseData.observe(this, qoWResponseObserver)
        recordsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        recordsViewModel.onMessageError.observe(this, errorMessageObserver)

        appointmentViewModel.appointmentsList.observe(this, todayListResponseObserver)
    }

    private val qoWResponseObserver =
        androidx.lifecycle.Observer<List<TrendingVideoDetails>> { responseObserver ->
            Log.d("list_log", "response came")
            if (!responseObserver.isNullOrEmpty()) {
                binding.relBuddyTalks.visibility = View.VISIBLE
                qowObj = responseObserver.get(0)
                binding.layoutQuestion.cardQuestionContainer.visibility = View.VISIBLE
                binding.layoutQuestion.tvQuestion.setText(qowObj.content)
                binding.layoutQuestion.tvLikeCount.setText(CommonMethods.getStringWithOnePadding("" + qowObj.postLikes))
                binding.layoutQuestion.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding(qowObj.commentsCount.toString()) + " Answered"
                )
                if (qowObj.isLiked) {
                    Glide.with(FourBaseCareApp.activityFromApp).load(
                        ContextCompat.getDrawable(
                            FourBaseCareApp.activityFromApp,
                            R.drawable.ic_like_filled
                        )
                    ).into(binding.layoutQuestion.ivLike)
                } else {
                    Glide.with(FourBaseCareApp.activityFromApp).load(
                        ContextCompat.getDrawable(
                            FourBaseCareApp.activityFromApp,
                            R.drawable.like_img
                        )
                    ).into(binding.layoutQuestion.ivLike)
                }

                if (qowObj.questionAttachmentUrl == null) {
                    binding.layoutQuestion.ivPost.visibility = View.GONE
                } else {
                    binding.layoutQuestion.ivPost.visibility = View.VISIBLE
                    Glide.with(FourBaseCareApp.activityFromApp).load(qowObj.questionAttachmentUrl)
                        .into(binding.layoutQuestion.ivPost)
                }
            } else {
                binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
            }
            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val doctorDetailsResponseObserver = androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            showDoctorDetailsDialogue(responseObserver.payLoad)
        }else{
            showToast(FourBaseCareApp.activityFromApp, "Something went wrong while getting doctor details!")
        }

    }

    private val patientDetailsResponseObserver = androidx.lifecycle.Observer<PatientDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            showProfileDetailsDialogue(responseObserver.payLoad)
        }else{
            showToast(FourBaseCareApp.activityFromApp, "SOmething went wrong while getting doctor details!")
        }

    }

    private val connectionResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    profileViewModel.callGetMyDoctorListing(getUserObject().role,false,
                        getUserAuthToken(), ""+getUserIdd()
                    )
                }else{
                    //showToast(FourBaseCareApp.activityFromApp, "Patient not found!")
                    showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
                }
            }
        }


    private val doctorListResponseObserver = androidx.lifecycle.Observer<DoctorsListingResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        Log.d("api_log", "doctor list Result " + responseObserver.isSuccess)

        if (responseObserver.doctorList == null) {
            Log.d("api_log", "doctor list null ")
        } else {
            Log.d("api_log", "doctor list size " + responseObserver.doctorList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()

        doctorList = ArrayList()
        if (responseObserver.isSuccess) {
            doctorList = ArrayList()
            doctorList.addAll(responseObserver.doctorList)

        }

    }

    private val findDoctorResponseObserver =
        androidx.lifecycle.Observer<FindDoctorResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.isSuccess){
                    //showToast(FourBaseCareApp.activityFromApp, "Doctor found! Assigning doctor to you!")
                    Log.d("assign_log","0 "+responseObserver.payLoad.id)
                    assignDoctor(""+responseObserver.payLoad.id)
                }else{
                    showNoDoctorFoundDialogue(""+responseObserver.message)
                    //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)
                }
            }else{
                showNoDoctorFoundDialogue(""+responseObserver?.message)
                //showToast(FourBaseCareApp.activityFromApp, ""+responseObserver?.message)
            }


        }

    private fun assignDoctor(doctorId: String) {
        Log.d("assign_log","0.1")
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("assign_log","1")
            profileViewModel.assignDoctor(
                false,
                doctorId,
                getUserAuthToken()
            )
            Log.d("assign_log","2")

        }
    }

    private val patientDetailsObserver = androidx.lifecycle.Observer<PatientDetailsByCGResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        if (responseObserver.isSuccess) {
            val gson = Gson()
            val patientObj = gson.toJson(responseObserver.payLoad)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_PATIENT_DETAILS_FOR_CG,patientObj)
            Log.d("patient_details_log","done "+responseObserver.payLoad)
        } else {
            Log.d("patient_details_log","err")
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }


    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    if(::selectedDoctor.isInitialized){
                        var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                        var bundle = Bundle()
                        bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                        addOrEditAppointmentFragment.arguments = bundle
                        CommonMethods.addNextFragment(
                            FourBaseCareApp.activityFromApp,
                            addOrEditAppointmentFragment, this, false
                        )
                    }else{
                        showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                        profileViewModel.callGetMyDoctorListing(getUserObject().role,false,
                            getUserAuthToken(), ""+getUserIdd()
                        )
                    }
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "Doctor not found!")
                }
            }
        }

    private val awsKeysResponseObserver = androidx.lifecycle.Observer<AwsCredentialsResponse?>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver != null && responseObserver.isSuccess) {
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_AWS_SECRET_KEY, responseObserver.payLoad.secret)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_AWS_ACCESS_KEY, responseObserver.payLoad.accessKey)
            Log.d("aws_credentials", "Data set")

        }

    }


    private val todayListResponseObserver = androidx.lifecycle.Observer<Response<AppointmentsListResponse?>>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if(responseObserver.isSuccessful){
            apppointmentsList = ArrayList()
            apppointmentsList = responseObserver.body()?.payLoad as ArrayList<AppointmentDetails>
            setTodayApptRecyclerView(apppointmentsList)

        }

    }

    private fun showHideNoData(shouldHide : Boolean){
        if(shouldHide){
            binding.tvNoData.visibility = View.VISIBLE
            binding.rvTodayAppointments.visibility = View.GONE
        }else{
            binding.tvNoData.visibility = View.GONE
            binding.rvTodayAppointments.visibility = View.VISIBLE
        }

    }

    private fun setTodayApptRecyclerView(list: ArrayList<AppointmentDetails>) {
        Log.d("today_list_log","Today listSize "+list.size)

        if(list.isNullOrEmpty()){
            showHideNoData(true)
            binding.linTodayAppointmentsContainer.visibility = View.GONE
        }else{
            binding.linTodayAppointmentsContainer.visibility = View.VISIBLE
            binding.rvTodayAppointments.apply {
                layoutManager = LinearLayoutManager(activity)
                (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
                todayAppointmentsAdapter =
                    ToodayAppointmentsNewAdapter(list, this@PatientHomeScreenNewFragment)
                adapter = todayAppointmentsAdapter
                list.sortByDescending { it.scheduledTime }
                todayAppointmentsAdapter.submitList(list.asReversed())
            }
        }


    }

    private val addQuestionResponseObserver = androidx.lifecycle.Observer<BaseResponse>{baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Question posted successfully!")
            getDataFromServer()
            //fragmentManager?.popBackStack()
        }

    }

    private val alertCOntactsObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "All contacts notified successfully!"
            )

        } else {
            showNoContactFoundDialogue("You do not have any emergency contacts! Please add atleast one contact from profile.")
        }


    }


    private val likePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer()
        }

    }

    private val savePostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->
            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
                Log.d("expert_log","getting refresh data")
                getExpertQuestions()
            }
        }

    private val reportPostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->
            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
            }
        }

    private val deletePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getExpertQuestions()
        }

    }

    private fun showPdfFile(link:String) {
        var bundle = Bundle()
        bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
        bundle.putString(Constants.SERVER_FILE_URL, link)
        bundle.putString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_REPORT)
        var fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
        fullScreenPDFViewFragment.arguments = bundle
        if (checkPermission(FourBaseCareApp.activityFromApp)) {
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                fullScreenPDFViewFragment, this, false

            )

        } else {
            Log.d("item_click_1", "double click captured")
        }
    }

    private fun setupClickListeners() {

        binding.tvView.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                    recordsViewModel.callGetGeneticReportPdf(
                        getUserAuthToken(),
                        getUserIdd().toString()
                    )
                }
            }
        })

        binding.tvCheckStatus.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp, GeneticReportFragment(), this, false
                )
            }
        })


        binding.fabmenu.setOnMenuButtonClickListener(View.OnClickListener {
            showHideMenu()

        })

        binding.layoutQuestion.ivPost.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick() && ::qowObj.isInitialized){
                if(!qowObj.questionAttachmentUrl.isNullOrEmpty() && qowObj.questionAttachmentType.equals("IMAGE")){
                    openFullScreenFragment(qowObj.questionAttachmentUrl)
                }
            }
        })

        binding.floatBookCOnsultation.setOnClickListener(View.OnClickListener {
                showHideMenu()
        })

        binding.floatAddPost.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideMenu()
                var bundle = Bundle()
                bundle.putString(Constants.SOURCE,Constants.COMMUNITY)
                var addPostFragment = AddPostFragment()
                addPostFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addPostFragment, this, false
                )
            }
        })

        binding.floatAskExperts.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideMenu()
                /*CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    AddPostFragment(), this, false
                )*/

                var bundle = Bundle()
                bundle.putString(Constants.SOURCE,Constants.ASK_EXPERT)
                var addPostFragment = AddPostFragment()
                addPostFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addPostFragment, this, false
                )
            }
        })

        binding.floatUploadReport.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideMenu()
                showBottomCameraOrGalleryDialogue()
            }
        })

        binding.floatBookCOnsultation.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideMenu()
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorListingFragment(), this, false
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

        binding.ivProfile.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    NovPatientProfileFragment(), this, false
                )
            }
        })

        binding.tvViewALlVideos.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString(Constants.SOURCE, Constants.VIDEO_LIST)
                var fragment = NovOncoFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }

        })

        binding.tvViewAllPosts.setOnClickListener(View.OnClickListener {

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

        binding.tvViewAllExpertQues.setOnClickListener(View.OnClickListener {

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

        binding.tvViewAllBlogs.setOnClickListener(View.OnClickListener {

            if(!isDoubleClick()){
                var bundle = Bundle()
                bundle.putString(Constants.SOURCE, Constants.BLOG_LIST)
                var fragment = NovOncoFragment()
                fragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fragment, this, false
                )
            }

        })

        binding.layoutQuestion.linLike.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                likePost(trendingBlogsDetails = qowObj)
            }
        })

        binding.layoutQuestion.ivShare.setOnClickListener(View.OnClickListener {
            CommonMethods.shareApp(
                FourBaseCareApp.activityFromApp,
                "Hi! can you answer this question? " + qowObj.content + "\n\n You can ask or answer questions like this on Onco buddy app. Download link : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
        })

        binding.layoutQuestion.linAnswer.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (::qowObj.isInitialized) {
                    var bundle = Bundle()
                    bundle.putParcelable(Constants.BLOG_OBJ, qowObj)
                    bundle.putBoolean("is_qow",true)
                    var questionDetailsFragment = QuestionDetailsFragment()
                    questionDetailsFragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        questionDetailsFragment, this, false
                    )
                }

            }
        })

    }

    private fun showHideMenu() {
        if (IS_SHOWING_MENU) {
            binding.frFloating.visibility = View.GONE
            binding.fabmenu.close(true)
        } else {
            binding.frFloating.visibility = View.VISIBLE
            binding.fabmenu.open(true)
        }
        IS_SHOWING_MENU = !IS_SHOWING_MENU
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                Log.d("permissions_log","Asked for permissions")
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }
    private fun showPermissionDeniedDialog() {
        android.app.AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle(getString(R.string.msg_permission_denied))
            .setMessage(getString(R.string.msg_permission_from_settings))
            .setPositiveButton(getString(R.string.title_app_settings),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        FourBaseCareApp.activityFromApp.getPackageName(),
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("permissions_log","came here "+requestCode)
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBottomCameraOrGalleryDialogue()
                    Constants.IS_FROM_HOME_SCREEN = true

                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
        }
    }

    private fun getTimeSlot(): String {
        val dt = Date()
        val hours = dt.hours
        val min = dt.minutes
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


    private fun setupTitleText() {

        //binding.tvProfileName.setText(""+getUserObject().firstName)
        binding.tvWelcome.setText("Hi Good "+getTimeSlot()+", "+getUserObject().firstName)

        if(getUserObject().role.equals(Constants.ROLE_CARE_COMPANION)){
            // navHeaderSubTitle.setText("Care Companion")
            //if(getUserObject().headline.isNullOrEmpty())navHeaderSubTitle.setText("Care Companion") else navHeaderSubTitle.setText(getUserObject().headline)
        }else{
            if(!getUserObject().headline.isNullOrEmpty() && !getUserObject().headline.equals("null")){
                //navHeaderSubTitle.setText(""+getUserObject().headline)
            }
        }



        if(!getUserObject().dpLink.isNullOrEmpty() && !FourBaseCareApp.activityFromApp.isDestroyed){
            Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink).
            placeholder(R.drawable.ic_user_image).error(R.drawable.ic_user_image).circleCrop().into(binding.ivProfile)
            Log.d("img_log","Set image")
        }else{
            Log.d("img_log","Is empty")
        }



    }

    private fun showLogoutDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.yes,
                    DialogInterface.OnClickListener { dialogInterface, which ->
                        doLogoutProcess()
                    }).setNegativeButton(R.string.no,
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        builder.show()
    }
    //observers
    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val blogsListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("list_log","response came")
        if(!responseObserver.isNullOrEmpty()){
            binding.linBlogsContainer.visibility = View.VISIBLE
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            setupForumsRecyclerView(arrayList)
            binding.executePendingBindings()
            binding.invalidateAll()

        }else{
            binding.linBlogsContainer.visibility = View.GONE
        //binding.linTrendingBlogsTitle.visibility = View.GONE
        }
    }

    private val shortsListResponseObserver = androidx.lifecycle.Observer<List<ShortDetails>>{ responseObserver ->

        Log.d("api_call_log","response came here")
        if(!responseObserver.isNullOrEmpty() && responseObserver.size>0){
            var arrayList = ArrayList<ShortDetails>()
            arrayList.add(responseObserver.asReversed().get(0))
            Log.d("api_call_log","list added "+arrayList.size)
            setupShortsRecyclerView(arrayList)
            binding.tvQuote.setText("\""+responseObserver.asReversed().get(0).content+"\"")

        }
        binding.executePendingBindings()
        binding.invalidateAll()

    }

    private val videosListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("list_log","response came")
        if(!responseObserver.isNullOrEmpty()){
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            setupTrendingVideosRecyclerView(arrayList)
        }
        else{
            binding.relTrendingVideosTitle.visibility = View.GONE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }


    private val homeVideosListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("home_trending_video","3")
        if(!responseObserver.isNullOrEmpty()){
            binding.linVideosContainer.visibility = View.VISIBLE
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
            setupVideosRecyclerView(subList)
            //setupExpertQuestionsAdapter()
            //binding.relTrendingVideosTitle.setText(CommonMethods.getStringWithOnePadding(""+arrayList.size))
        }
        else{
            Log.d("home_trending_video","NO videos")
            binding.linVideosContainer.visibility = View.GONE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }



    private val questionListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("list_log","question list response came")
        if(!responseObserver.isNullOrEmpty()){
            //binding.linQuestionsTitle.visibility = View.GONE
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            arrayList.sortByDescending { it.post.publishedDate }
           // binding.tvQuestionsCOunt.setText(CommonMethods.getStringWithOnePadding(""+arrayList.size))
        }
        else{
           // binding.linQuestionsTitle.visibility = View.GONE
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun setupForumsRecyclerView(list: ArrayList<TrendingVideoDetails>) {

        binding.rvForums.apply {
            //binding.linTrendingBlogsTitle.visibility = View.GONE
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            liveForumsListingAdapter = LiveForumsListingAdapter(list, this@PatientHomeScreenNewFragment, true)
            adapter = liveForumsListingAdapter
            liveForumsListingAdapter.submitList(list.asReversed())
        }
    }

    private fun setupShortsRecyclerView(list: ArrayList<ShortDetails>) {

    }


    private fun setupTrendingVideosRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        binding.rvAskExpert.apply {
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

            expertQuestionsAdapter = ExpertQuestionsAdapter(subList, this@PatientHomeScreenNewFragment)
            adapter = expertQuestionsAdapter
            Log.d("pagination_log", "Server list size "+subList.size)
            expertQuestionsAdapter.submitList(subList)
        }
    }

    private fun setupVideosRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        binding.rvLatestVideos.apply {
            Log.d("home_trending_video","4")
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            liveVideosListingAdapter = LiveVideosListingAdapter(list, this@PatientHomeScreenNewFragment)
            adapter = liveVideosListingAdapter
            Log.d("pagination_log", "Server list size "+list.size)
            liveVideosListingAdapter.submitList(list)
        }
    }

    override fun onForumSelected(position: Int, item: TrendingVideoDetails, view: View) {


        if(!isDoubleClick()){
            if(view.id === R.id.ivLike){
                likePost(trendingBlogsDetails = item)

            }else if(view.id === R.id.ivShare){
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great article named " + item.post.title + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }

            else if(view.id == R.id.relCOntent || view.id == R.id.linCOmments){
                var bundle  = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ,item)

                var blogDetailsFragment =  BlogDetailsFragment()
                blogDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    blogDetailsFragment, this, false
                )
            }
        }

    }

    override fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view: View) {

        /*if(view.id === R.id.ivLike){
            likePost(item)

        }else if(!isDoubleClick()){
            val intent = Intent(FourBaseCareApp.activityFromApp, VideoViewerActivity::class.java)
            intent.putExtra(Constants.YOUTUBE_URL, item.content)
            intent.putExtra(Constants.VIDEO_OBJ, item)
            startActivity(intent)
            FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        }*/


        val intent = Intent(FourBaseCareApp.activityFromApp, VideoViewerActivity::class.java)
        intent.putExtra(Constants.YOUTUBE_URL, item.content)
        intent.putExtra(Constants.VIDEO_ID, ""+item.post.id)
        intent.putExtra(Constants.VIDEO_OBJ, item)
        intent.putExtra(Constants.SOURCE, "home")
        startActivityForResult(intent, Constants.ACTIVITY_RESULT)
        FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        Log.d("activity_result","started activity for result")

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            Log.d("resume_log","Called")
            setupTitleText()
            if(Constants.IS_HOME_SCREEN_UPDATED){
                getDataFromServer()
                Constants.IS_HOME_SCREEN_UPDATED = false
            }
        }
    }

    private fun likePost(trendingBlogsDetails:TrendingVideoDetails){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), ""+trendingBlogsDetails.post.id)
        }

    }

    private fun likeComment(item: CommentsItem){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikeComment(
                getUserAuthToken(), ""+item.comment.id)
        }

    }

    override fun onQuestionItemSelected(position: Int, item: TrendingVideoDetails, view: View) {

        if(!isDoubleClick()){
            if(view.id == R.id.linCOntainer){
                var bundle  = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ,item)

                var questionDetailsFragment =  QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    questionDetailsFragment, this, false
                )
            }else if(view.id == R.id.ivLikes || view.id == R.id.tvLikesCOunt){
                Log.d("like_log","0")
                //likeComment(item.comments.get(0))

            }else if(view.id == R.id.ivShare || view.id == R.id.ivShare){
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! can you answer this question? " + item.content + "\n\n You can ask or answer questions like this on Onco buddy app. Download link : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }else if(view.id == R.id.relImage){
                Log.d("details_log","0.1")
                Log.d("details_log","patient "+checkIFPatient())
                Log.d("details_log","role "+item.post.author.role)

                /*if(item.post.author.role != null && !item.post.anonymous){
                    if(item.post.author.role.equals(Constants.ROLE_DOCTOR)){
                        Log.d("details_log","0.2")
                        getDoctorDetails(""+item.post.author.userId)
                    }else{
                        Log.d("details_log","0.3")
                        getPatientDetails(""+item.post.author.userId)
                    }

                }*/
                if(checkIFPatient() && item.post.author.role != null && item.post.author.role.equals(Constants.ROLE_DOCTOR) && !item.post.anonymous){
                    Log.d("details_log","APi called")
                    getDoctorDetails(""+item.post.author.userId)
                }

            }
            else if(view.id == R.id.relAnsImage){
                Log.d("details_log","0")
                Log.d("details_log","patient "+checkIFPatient())
                Log.d("details_log","role "+item.comments.get(0).comment.author.role)
                if(checkIFPatient() && item.comments.get(0).comment.author.role != null
                    && item.comments.get(0).comment.author.role.equals(Constants.ROLE_DOCTOR) && !item.comments.get(0).comment.isAnonymous){
                    getDoctorDetails(""+item.comments.get(0).comment.author.userId)
                    Log.d("details_log","condition true")
                }else{
                    Log.d("details_log","condition false")
                }

            }
        }





        /*if (!isDoubleClick()) {

            var bundle = Bundle()

            bundle.putParcelable(Constants.BLOG_OBJ, item)

            var questionDetailsFragment = QuestionDetailsFragment()
            questionDetailsFragment.arguments = bundle

            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                questionDetailsFragment, this, false
            )
        }
        */    /* if(!isDoubleClick()) {

            if(view.id == R.id.relImage){
                var doctorDetails = DoctorDetails()
                doctorDetails.id ="1"
                doctorDetails.name = "Dr. Akshay Verma"
                doctorDetails.designation = "Head of department"
                doctorDetails.mobileNo = "9333999920"
                doctorDetails.yearsofExp = "05"
                doctorDetails.specialization = "Oncology"
                doctorDetails.dpLink = "https://homepages.cae.wisc.edu/~ece533/images/fruits.png"
                showDoctorDetailsDialogue(doctorDetails)



            }else if(view.id == R.id.relAnsImage){
                  showDoctorDetailsDialogue(DoctorDetails())
            }

            else if(view.id == R.id.linComments){
                var bundle  = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ,item)

                var questionDetailsFragment =  QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    questionDetailsFragment, this, false
                )
            }
            else if(view.id === R.id.ivShare){
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! can you answer this question?\n\n" + item.content + "\n\nYou can ask or discuss questions like this onOnco buddy app.\n\nDownload link : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }
        }
*/



    }

    private fun showAddQuestionDialogue() {
        askQuestionDalogue = Dialog(FourBaseCareApp.activityFromApp)
        askQuestionDalogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        askQuestionDalogue.setContentView(R.layout.dialogue_add_question)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(askQuestionDalogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation


        val window: Window? = askQuestionDalogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        askQuestionDalogue.window?.attributes = lp
        //deleteConfirmationDialogue.window?.setGravity(Gravity.BOTTOM)
        askQuestionDalogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        cardAddImage = askQuestionDalogue.findViewById(R.id.cardAddImage)
        cardImage = askQuestionDalogue.findViewById(R.id.cardImage)
        ivQuestion = askQuestionDalogue.findViewById(R.id.ivQuestion)
        val ivRefresh: ImageView = askQuestionDalogue.findViewById(R.id.ivRefresh)
        val ivRemoveImage: ImageView = askQuestionDalogue.findViewById(R.id.ivRemoveImage)
        val btnYes: TextView = askQuestionDalogue.findViewById(R.id.btnYes)
        val switchAnonymous : Switch = askQuestionDalogue.findViewById(R.id.switchAnonymous)
        val edQuestion: EditText = askQuestionDalogue.findViewById(R.id.edQuestion)
        val edQuestionTitle: EditText = askQuestionDalogue.findViewById(R.id.edQuestionTitle)
        val tvCount: TextView = askQuestionDalogue.findViewById(R.id.tvCharecterCOunt)

        edQuestionTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                var counttext = charSequence.length.toString()
                tvCount.setText(counttext + "/80")
                if(charSequence.length>69){
                    tvCount.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.skip_to_login_red))
                }else{
                    tvCount.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.black))
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        cardAddImage?.setOnClickListener(View.OnClickListener {
            if(isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        ivRefresh.setOnClickListener(View.OnClickListener {
            if(isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        ivRemoveImage.setOnClickListener(View.OnClickListener {
            FILE_URL = ""
            cardImage?.visibility = View.GONE
            cardAddImage?.visibility = View.VISIBLE
        })

        btnYes.setOnClickListener(View.OnClickListener {
            if(CommonMethods.getTrimmedText(edQuestionTitle).isNullOrEmpty()){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp,"Please enter question title!")
            }
            else if(CommonMethods.getTrimmedText(edQuestion).isNullOrEmpty()){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp,"Please enter your question!")
            }else{
                addQuestion(getTrimmedText(edQuestion), getTrimmedText(edQuestionTitle), switchAnonymous.isChecked)
                askQuestionDalogue.dismiss()
            }
        })

        val btnNo: TextView = askQuestionDalogue.findViewById(R.id.btnNo)

        btnNo.setOnClickListener(View.OnClickListener {
            askQuestionDalogue.dismiss()
        })
        askQuestionDalogue.show()
    }

    private fun addQuestion(que: String, title:String, isAnonymous: Boolean){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            var addQuestionInput  =  AddQuestionInput()
            var postQuestionDto = PostQuestionDto()
            postQuestionDto.question = que
            postQuestionDto.title = title
            if(!FILE_URL.isNullOrEmpty()) postQuestionDto.attachmentLink = FILE_URL
            addQuestionInput.postQuestionDto = postQuestionDto
            addQuestionInput.isAnonymous = isAnonymous

            addQuestionInput.postType = "QUESTION"


            forumsViewModel.callAddQuestion("",
                getUserAuthToken(), addQuestionInput)
        }

    }

    private fun showSelectDoctorDialogue() {
        /*val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_emergency_contacts_list, null)

        selectBottomDoctorDialog = FragmentModalBottomSheet(myView)
        selectBottomDoctorDialog.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )
        val rvDoctors: RecyclerView = myView.findViewById(R.id.rvEmergencyCOntacts)
        val ivAddContact: ImageView = myView.findViewById(R.id.ivAddCOntact)
        val ivHeader : ImageView = myView.findViewById(R.id.ivHeader)
        val tvHeaderTitle : TextView = myView.findViewById(R.id.tvHeaderTitle)

        ivHeader.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_circular_blue_doctor))
        tvHeaderTitle.setText(getString(R.string.doctors))

        if(!::doctorList.isInitialized || doctorList == null){
            doctorList = ArrayList()
        }

        rvDoctors.apply {
            layoutManager = LinearLayoutManager(activity)
            doctorListingAdapter =
                DoctorListingAdapter(doctorList,this@PatientHomeScreenNewFragment,false,true)
            adapter = doctorListingAdapter
            doctorListingAdapter.submitList(doctorList)
        }

        ivAddContact.setOnClickListener(View.OnClickListener {
            selectBottomDoctorDialog.dismiss()
            showFindDoctorInputDialogue()
        })*/
    }



    private fun showFindDoctorInputDialogue() {


        findDoctorInputDialogue = Dialog(FourBaseCareApp.activityFromApp)
        findDoctorInputDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        findDoctorInputDialogue.setContentView(R.layout.dialogue_add_doctor_name)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(findDoctorInputDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = findDoctorInputDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        findDoctorInputDialogue.window?.attributes = lp
        findDoctorInputDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes: TextView = findDoctorInputDialogue.findViewById(R.id.btnYes)

        val btnCancel: TextView = findDoctorInputDialogue.findViewById(R.id.btnNo)

        val edMobile: EditText = findDoctorInputDialogue.findViewById(R.id.edMobileNumber)

        btnYes.setOnClickListener(View.OnClickListener {
            if(getTrimmedText(edMobile).isNullOrBlank()){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_mobile_number))
            } else if(getTrimmedText(edMobile).toString().trim().length < 10){
                showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_invalid_mobile_number))
            } else{
                findDoctorInputDialogue.dismiss()
                CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
                findDoctor(getTrimmedText(edMobile))
            }


        })

        btnCancel.setOnClickListener(View.OnClickListener {
            findDoctorInputDialogue.dismiss()

        })

        findDoctorInputDialogue.show()
    }

    private fun findDoctor(phoneNumber: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            profileViewModel.findDoctor(false,
                phoneNumber,
                getUserAuthToken()
            )
            Log.d("emergency_contact","2")

        }
    }

    private fun getDoctorDetails(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun getPatientDetails(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","4")
            forumsViewModel.callGetPatientDetails(false,getUserAuthToken(), doctorId)
        }
    }


    private fun showBottomCameraOrGalleryDialogue() {
        val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_camera_or_gallery, null)

        bottomCameraOrGalleryDIalogue = FragmentModalBottomSheet(myView)
        bottomCameraOrGalleryDIalogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linGallery: LinearLayout = myView.findViewById(R.id.linGallery)
        val linCamera: LinearLayout = myView.findViewById(R.id.linCamera)

        linGallery.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) uploadPDF()
            bottomCameraOrGalleryDIalogue.dismiss()
        })

    }

    private fun uploadPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 112)
    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        return try {
            val width = image.width / 2
            val height = image.height / 2
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: NullPointerException) {
            image
        }
    }

    private fun uploadFileToS3(){
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            Log.d("insert_img_log","File path1.2 "+FILE_PATH)

            val body: MultipartBody.Part
            val file  = File(FILE_PATH)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(),body)
            Log.d("insert_img_log","2 "+FILE_PATH)

        }
    }

    private val responseObserver = androidx.lifecycle.Observer<GeneticReportResponse> { responseObserver ->

        Log.d("list_log", "response came")

        if(responseObserver.isSuccess){
            showPdfFile(responseObserver.payLoad.link)
        }

    }

    private val todayApptResponseObserver = androidx.lifecycle.Observer<Response<AppointmentsListResponse?>>{ responseObserver ->
        if(responseObserver.isSuccessful){
            val arrayList = responseObserver.body()?.payLoad
            Log.d("today_list_log","total size "+arrayList?.size)
            //binding.tvTodayApptCount.text = ""+arrayList?.size
        }

        binding.executePendingBindings()
        binding.invalidateAll()

    }

    private val fileUploadResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData

        FILE_URL = responseObserver.message
        binding.executePendingBindings()
        binding.invalidateAll()
        Log.d("insert_img_log","question pic set "+FILE_URL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  Constants.ACTIVITY_RESULT){
            if (resultCode == Activity.RESULT_OK) {
                Log.d("activity_result","got result here")
                getDataFromServer()
            }
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_GALLERY_IMAGE){
            val uri = data!!.data
            mCurrentPhotoPath = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
            var imgBitmap: Bitmap?
            imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BitmapFactory.decodeFile(mCurrentPhotoPath)
            } else {
                val extras = data!!.extras
                extras!!["data"] as Bitmap?
            }
            try {
                if (imgBitmap != null) {
                    while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                        imgBitmap = getResizedBitmap(imgBitmap)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP

            Log.d("insert_img_log","0")
            //val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp,imgBitmap)//insertImage(FourBaseCareApp.activityFromApp.contentResolver, imgBitmap,"title","desc")
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(uri)
                .start(FourBaseCareApp.activityFromApp, this)


        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if(data != null){
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri: Uri = result.getUri()
                    Log.d("insert_img_log","cropped image")
                    if(ivQuestion == null){
                        Log.d("insert_img_log","Imageview null")
                    }
                    //val uri = data!!.data
                    ivQuestion?.let {
                        Glide.with(FourBaseCareApp.activityFromApp).load(resultUri).into(it)
                    }

                    cardImage?.visibility = View.VISIBLE
                    cardAddImage?.visibility = View.GONE
                    /* tvUploadImage?.setText("Change image")
                     tvRemoveImage?.visibility = View.VISIBLE
 */

                    FILE_PATH = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, resultUri)
                    uploadFileToS3()


                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error: java.lang.Exception = result.getError()
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                }
            }

        }
        else{
            if (resultCode == Activity.RESULT_OK) {
                val uri = data!!.data
                if(data != null && data!!.data != null){
                    try {
                        Log.d("extract_time","File scelected")
                        val fullPath1: String = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                        val f = File(fullPath1)
                        val size = f.length()/1024/1024

                        Log.d("file_path", "Full path $fullPath1")
                        if(size <= 10.0){
                            var bundle  = Bundle()
                            bundle.putString(Constants.PDF_PATH,fullPath1)
                            var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                            fullScreenPDFViewFragment.arguments = bundle

                            CommonMethods.addNextFragment(
                                FourBaseCareApp.activityFromApp,
                                fullScreenPDFViewFragment,this,false)

                        }else{
                            showToast(FourBaseCareApp.activityFromApp,"File size limit exceed. File with more than 10 MB cannot be uploaded!")
                        }


                    } catch (e: Exception) {
                        /*  Toast.makeText(
                              FourBaseCareApp.activityFromApp,
                              "There was an error getting data",
                              Toast.LENGTH_SHORT
                          ).show()*/

                    }
                }else{
                    Toast.makeText(FourBaseCareApp.activityFromApp,"Uri is null",Toast.LENGTH_SHORT)
                }
            }else{
                /*Toast.makeText(
                    FourBaseCareApp.activityFromApp,
                    "There was an error getting data",
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }


    }

    private fun showDoctorDetailsDialogue(doctorDetails: com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails, shouldBookAppointment: Boolean = false) {

        doctorDetailsDialogue = Dialog(FourBaseCareApp.activityFromApp)
        doctorDetailsDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        doctorDetailsDialogue.setContentView(R.layout.dialogue_doctor_details)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(doctorDetailsDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = doctorDetailsDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        doctorDetailsDialogue.window?.attributes = lp
        doctorDetailsDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val ivProfilePic: ImageView = doctorDetailsDialogue.findViewById(R.id.ivDoctorImage)
        val tvName: TextView = doctorDetailsDialogue.findViewById(R.id.tvDoctorName)
        val tvSpecialization: TextView = doctorDetailsDialogue.findViewById(R.id.tvSpecialization)
        val tvDesignation: TextView = doctorDetailsDialogue.findViewById(R.id.tvDesignation)
        val tvHospitalName: TextView = doctorDetailsDialogue.findViewById(R.id.tvHospitalName)
        val tvExperience: TextView = doctorDetailsDialogue.findViewById(R.id.tvExp)
        val tvAddDoctor: TextView = doctorDetailsDialogue.findViewById(R.id.tvAddDoctor)
        val ivVerified: ImageView = doctorDetailsDialogue.findViewById(R.id.ivVerified)
        val linExp: LinearLayout = doctorDetailsDialogue.findViewById(R.id.linExp)

        tvAddDoctor.setText("Book an appointment")


        if(!doctorDetails.dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(doctorDetails.dpLink).placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        tvName.setText(doctorDetails.firstName)
        tvDesignation.setText(doctorDetails.designation)
        tvSpecialization.setText(doctorDetails.specialization)
        //tvExperience.setText("05 years")
        if(doctorDetails.experience != null){
            tvExperience.setText(CommonMethods.getStringWithOnePadding(doctorDetails.experience)+" years")
        }else{
            linExp.visibility = View.GONE
        }
        tvHospitalName.setText(doctorDetails.hospital)
        if(doctorDetails.isVerified){
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(ivVerified)
        }else{
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(ivVerified)
        }
        tvAddDoctor.setOnClickListener(View.OnClickListener {
            Log.d("details_log","6")
            doctorDetailsDialogue.dismiss()
            doctorDetailsDialogue.hide()

            if(::selectBottomDoctorDialog.isInitialized) selectBottomDoctorDialog.dismiss()

            selectedDoctor = Doctor()
            selectedDoctor.firstName = doctorDetails.firstName
            selectedDoctor.doctorId = doctorDetails.doctorId
            selectedDoctor.displayPicUrl = doctorDetails.dpLink
            selectedDoctor.designation = doctorDetails.designation
            selectedDoctor.verified = doctorDetails.isVerified
            selectedDoctor.consultationFee = doctorDetails.consultationFee.toDouble()

            Log.d("selected_dr_log","0.0 id "+doctorDetails.doctorId)
            Log.d("selected_dr_log","0 id "+selectedDoctor.doctorId)
            Log.d("selected_dr_log","0 fees "+selectedDoctor.firstName)
            if(shouldBookAppointment){
                var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                var bundle = Bundle()
                bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                addOrEditAppointmentFragment.arguments = bundle
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addOrEditAppointmentFragment, this, false
                )
            }else{
                assignDoctor(""+doctorDetails.doctorId)
            }

        })

        Log.d("details_log","5")
        doctorDetailsDialogue.show()
    }

    private fun showProfileDetailsDialogue(patientDetails: PatientDetails) {

        doctorDetailsDialogue = Dialog(FourBaseCareApp.activityFromApp)
        doctorDetailsDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        doctorDetailsDialogue.setContentView(R.layout.dialogue_patient_details)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(doctorDetailsDialogue.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = doctorDetailsDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        doctorDetailsDialogue.window?.attributes = lp
        doctorDetailsDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val ivProfilePic: ImageView = doctorDetailsDialogue.findViewById(R.id.ivDoctorImage)
        val tvName: TextView = doctorDetailsDialogue.findViewById(R.id.tvDoctorName)
        val tvCancerType: TextView = doctorDetailsDialogue.findViewById(R.id.tvCancerType)
        val tvSubType: TextView = doctorDetailsDialogue.findViewById(R.id.tvSubType)
        val tvAddPatient: TextView = doctorDetailsDialogue.findViewById(R.id.tvAddPatient)


        if(!patientDetails.dpLink.isNullOrEmpty()){
            Glide.with(FourBaseCareApp.activityFromApp).load(patientDetails.dpLink).
            placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        }

        tvName.setText(patientDetails.firstName)
        if(patientDetails.cancerType != null)
            tvCancerType.setText(patientDetails.cancerType.name)
        if(patientDetails.cancerSubType != null)
            tvSubType.setText(patientDetails.cancerSubType.name)

        tvAddPatient.setOnClickListener(View.OnClickListener {
            Log.d("details_log","6")
            doctorDetailsDialogue.dismiss()
            //assignDoctor(""+doctorDetails.doctorId)
        })

        Log.d("details_log","5")
        doctorDetailsDialogue.show()
    }


    private fun showNoDoctorFoundDialogue(message: String) {
        android.app.AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle("No doctor found!")
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    dialogInterface.dismiss()
                }).setCancelable(false)

            .show()
    }

    private fun showNoContactFoundDialogue(message: String) {
        android.app.AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle("No emergency contacts found!")
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    dialogInterface.dismiss()
                })
            .setCancelable(false)
            .show()
    }

    fun handleScrollTOp(){
        //showToast(FourBaseCareApp.activityFromApp,"Called")
        /*if (!isDoubleClick()){
            binding.nestedScrollView.smoothScrollTo(0,0)
        }*/
    }

    private fun showOverlayPermissionDialogue() {
        askOverlayPermissionDialogue = Dialog(FourBaseCareApp.activityFromApp)
        askOverlayPermissionDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        askOverlayPermissionDialogue.setContentView(R.layout.dialogue_cancel_appointment)

        val ivLogo: ImageView = askOverlayPermissionDialogue.findViewById(R.id.ivLogo)
        ivLogo.setImageDrawable(FourBaseCareApp.activityFromApp.getDrawable(R.drawable.ic_cancel_alert))

        val tvTitleText: TextView = askOverlayPermissionDialogue.findViewById(R.id.tvTitleText)
        /*if(Constants.IS_FROM_ADD_APPOINTMENT){
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }else{
            tvTitleText.setText(getString(R.string.record_added_successfully))
        }*/
        tvTitleText.setText("For seamless calling experience, You need to enable overlay feature from your device")

        val btnNo: TextView = askOverlayPermissionDialogue.findViewById(R.id.btnNo)
        btnNo.visibility = View.GONE

        val linNo: LinearLayout = askOverlayPermissionDialogue.findViewById(R.id.linNo)
        linNo.visibility = View.GONE

        val btnYes: TextView = askOverlayPermissionDialogue.findViewById(R.id.btnYes)
        btnYes.setText(getString(R.string.goto_settings))



        btnYes.setOnClickListener(View.OnClickListener {
            askOverlayPermissionDialogue.dismiss()
            //fragmentManager?.popBackStack()
            requestOverLayPermission()
        })

        askOverlayPermissionDialogue.show()
    }

    override fun onTodayAppointmentSelected(position: Int, item: AppointmentDetails, view: View) {

        if(view.id == R.id.tvJoinRoom){
            if(askForCameraPermissions()) {
                var bundle  = Bundle()
                bundle.putParcelable(Constants.APPOINTMENT_DETAILS,item)
                var videoCallFragment  = VideoQueueByDoctorFragment()
                videoCallFragment.arguments = bundle

                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp,
                    videoCallFragment,this,false)
            }
        }

    }

    // permissions

    fun isCameraPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    fun askForCameraPermissions(): Boolean {
        if (!isCameraPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.RECORD_AUDIO)
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO),Constants.REQUEST_EXTERNAL_STORAGE)
            }
            return false
        }
        return true
    }

    override fun onItemSelected(position: Int, item: Doctor, view: View) {
        if(view.id == R.id.relImage){
            // dplink, name, specialization, designation, hospital name, experience,verified
            if(item.isRequestPending != null && item.isRequestPending){

            }else{
                var doctorDetails = com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails()
                doctorDetails.doctorId = item.doctorId
                doctorDetails.consultationFee = item.consultationFee.toInt()
                doctorDetails.firstName = item.firstName
                doctorDetails.dpLink = item.displayPicUrl
                doctorDetails.specialization = item.specialization
                doctorDetails.designation = item.designation
                doctorDetails.hospital = item.hospital
                doctorDetails.isVerified = item.verified
                //doctorDetails.experience = item.ex
                showDoctorDetailsDialogue(doctorDetails, true)

            }

        }else if(view.id == R.id.ivYes){
            Log.d("doctor_response_log","Yes")
            showResponseConfirmDialogue(""+item.doctorId,true)
        }else if(view.id == R.id.ivNo){
            Log.d("doctor_response_log","No")
            showResponseConfirmDialogue(""+item.doctorId,false)
        }
    }

    private fun showResponseConfirmDialogue(patientId: String,isAccepted: Boolean) {
        responseConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        responseConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(responseConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = responseConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        responseConfirmationDialogue.window?.setAttributes(lp)
        responseConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: Button = responseConfirmationDialogue.findViewById(R.id.btnDelete)
        val btnCancel: Button = responseConfirmationDialogue.findViewById(R.id.btnCancel)
        val tvMsg: TextView = responseConfirmationDialogue.findViewById(R.id.tvMsg)

        btnDelete.setText("Yes")
        btnCancel.setText("No")
        if(isAccepted){
            tvMsg.setText("Are you sure you want to accept connection with this doctor?")
        }else{
            tvMsg.setText("Are you sure you do not want to accept connection for this doctor?")
        }


        btnDelete.setOnClickListener(View.OnClickListener {
            doConnectionResponse(patientId,isAccepted)
            responseConfirmationDialogue.dismiss()
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            responseConfirmationDialogue.dismiss()
        })

        responseConfirmationDialogue.show()
    }

    private fun doConnectionResponse(patientId: String, isAccepted: Boolean) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            selectBottomDoctorDialog.dismiss()
            profileViewModel.responseCOnnectionRequest(getUserAuthToken(),patientId, false,isAccepted)
            Log.d("delete_log", "API called")

        }
    }

    private fun setProgressRecyclerView() {
        var profileStatusList = ArrayList<ProfileStatus>()

        for(i in 0..5){
            profileStatusList.add(ProfileStatus(i,false))
        }

        showHideNoData(false)

        var todayList = ArrayList<AppointmentDetails>()

        for(i in 0..3){
            todayList.add(AppointmentDetails())
        }

        binding.rvTodayAppointments.apply {
            layoutManager = LinearLayoutManager(activity)
            (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
            todayAppointmentsAdapter =
                ToodayAppointmentsNewAdapter(todayList, this@PatientHomeScreenNewFragment)
            adapter = todayAppointmentsAdapter
            todayList.sortByDescending { it.scheduledTime }
            todayAppointmentsAdapter.submitList(todayList.asReversed())
        }
    }

    override fun onExpertQuestionSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {
            Log.d("expert_log", "0 " + view.id)
            if (view.id === R.id.linLike) {
                Log.d("expert_log", "1")
                likePost(trendingBlogsDetails = item)

            }else if (view.id == R.id.ivSave) {
                Log.d("expert_log", "1")
                savePost(trendingBlogsDetails = item)

            }else if(view.id  == R.id.tvFileName){
                var bundle = Bundle()
                bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
                bundle.putString(Constants.SERVER_FILE_URL, item.questionAttachmentUrl)
                var fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
                fullScreenPDFViewFragment.arguments = bundle
                Log.d("item_click_1", "next fragment")
                Log.d("update_doc_log", "List FILE_URL ${item.questionAttachmentUrl}")
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    fullScreenPDFViewFragment, this, false
                )

            }
            else if (view.id === R.id.ivShare) {
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
            }else if (view.id == R.id.ivMenu) {

                if(getUserObject().userIdd == item.author.userId){
                    val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                    popupMenu.menuInflater.inflate(R.menu.menu_oncohub_options, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                        when(menuItem.itemId){
                            R.id.menu_edit ->{
                                popupMenu.dismiss()
                                var bundle = Bundle()
                                var addPostFragmemt = AddPostFragment()
                                bundle.putString(Constants.SOURCE, Constants.EXPERT_QUESTIONS_LIST)
                                bundle.putParcelable("add_post_data",item)
                                addPostFragmemt.arguments = bundle
                                CommonMethods.addNextFragment(
                                    FourBaseCareApp.activityFromApp,
                                    addPostFragmemt, this, false
                                )

                            }
                            R.id.menu_delete -> {
                                popupMenu.dismiss()
                                showDeleteConfirmDialogue(""+item.post.id)
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }else{
                    val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                    popupMenu.menuInflater.inflate(R.menu.menu_oncohub_report, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                        when(menuItem.itemId){
                            R.id.menu_report ->{
                                reportPost(item)
                                popupMenu.dismiss()
                            }

                        }
                        true
                    }
                    popupMenu.show()
                }

            }
        }
    }

    private fun savePost(trendingBlogsDetails: TrendingVideoDetails) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("expert_log", "2")
            var shouldSave = !trendingBlogsDetails.saved
            forumsViewModel.saveOrUnSavePost(shouldSave,
                getUserAuthToken(), "" + trendingBlogsDetails.post.id
            )
        }

    }

    private fun reportPost(trendingBlogsDetails: TrendingVideoDetails) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("expert_log", "2")
            forumsViewModel.reportPost(
                getUserAuthToken(), "" + trendingBlogsDetails.post.id
            )
        }

    }

    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    Log.d("permission_log","Coming here")
                    val alertBuilder = androidx.appcompat.app.AlertDialog.Builder(
                        context
                    )
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_necessary)
                    alertBuilder.setMessage(R.string.external_storage_permission_needed)
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            ),
                            ADD_DOCUMENT_PERMISSION
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                        ),
                        ADD_DOCUMENT_PERMISSION
                    )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun showDeleteConfirmDialogue(id: String) {
        //deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        deleteConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(deleteConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = deleteConfirmationDialogue.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        deleteConfirmationDialogue.window?.setAttributes(lp)
        deleteConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = deleteConfirmationDialogue.findViewById(R.id.btnDelete)
        val tvMsg: TextView = deleteConfirmationDialogue.findViewById(R.id.tvMsg)

        tvMsg.setText(FourBaseCareApp.activityFromApp.getString(R.string.are_you_sure_you_want_to_delete_this_post))


        btnDelete.setOnClickListener(View.OnClickListener {
            //deleteRecordFromServer(obj)
            deletePost(id)
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
    }

    private fun deletePost(postId: String){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.deletePost(getUserAuthToken(), ""+postId)
        }

    }


}