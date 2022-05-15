package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAllListBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.PostQuestionDto
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.VideoViewerActivity
import com.oncobuddy.app.views.adapters.LiveForumsListingAdapter
import com.oncobuddy.app.views.adapters.LiveVideosListingAdapter
import com.oncobuddy.app.views.adapters.MyQuestionsListingAdapter
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * All videos listing fragment
 *
 * @constructor Create empty All videos listing fragment
 */

class AllVideosListingFragment : BaseFragment(),
    LiveVideosListingAdapter.Interaction,
    LiveForumsListingAdapter.Interaction,
    MyQuestionsListingAdapter.Interaction{

    private lateinit var binding : FragmentAllListBinding
    private lateinit var liveVideosListingAdapter: LiveVideosListingAdapter
    private lateinit var liveBlogssListingAdapter: LiveForumsListingAdapter
    private lateinit var myQuestionsListingAdapter: MyQuestionsListingAdapter
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var myLayoutManager: LinearLayoutManager
    private var pageNo = 0
    private var query = ""
    private var isAPiLoading = false
    private var source = Constants.VIDEO_LIST
    private var selectTab = 1
    private val FIRST_TAB = 1
    private val SECOND_TAB = 2
    private val THIRD_TAB = 3
    private lateinit var askQuestionDalogue: Dialog
    private lateinit var doctorDetailsDialogue: Dialog
    private var ivQuestion: ImageView? = null
    private var mCurrentPhotoPath: String? = null
    private lateinit var  FILE_PATH: String
    private var FILE_URL = ""
    private var cardAddImage: CardView? = null
    private var cardImage: CardView? = null
    private lateinit var selectedDoctor: Doctor
    private var IS_IMAGE_MODE =  false
    private var videosList = ArrayList<TrendingVideoDetails>()
    private var questionsList = ArrayList<TrendingVideoDetails>()
    private var myQuestionsList = ArrayList<TrendingVideoDetails>()
    private var blogsList = ArrayList<TrendingVideoDetails>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        init(inflater, container)
        logScreenViewEventMP("Oncohub screen")
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_all_list, container, false
        )

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d("api_call_log","Hidden "+hidden)
        if(!hidden){
            setTitleText()
            if(Constants.IS_LIST_UPDATED){
                Log.d("api_call_log","1")
                getDataFromServer(query,pageNo)
            }
        }
    }

    private fun setTitleText() {
        if(source.equals(Constants.VIDEO_LIST)){
            //binding.tvTitle.setText("All Videos")
            binding.edSearchH.setHint("Search your video")
        }else if(source.equals(Constants.BLOG_LIST)){
            //binding.tvTitle.setText("All Blogs")
            binding.edSearchH.setHint("Search your blog")
        }else{
            //binding.tvTitle.setText("All Questions")
            binding.edSearchH.setHint("Search your discussion")
        }

    }

    override fun onResume() {
        super.onResume()
        Log.d("visibility_log", "on resume")
        if(arguments != null){
            if(arguments!!.containsKey(Constants.SOURCE)){
                source = arguments!!.getString(Constants.SOURCE).toString()
            }
            if(arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
                if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)) binding.ivBack.visibility = View.GONE
            }
        }
        setTitleText()
        setClickListeners()
        setupVM()
        setupObservers()
        //getDataFromServer(query,pageNo)
        Log.d("visibility_log", "Resume Tab "+selectTab)
        Log.d("visibility_log", "Resume soucre "+source)

        if(!IS_IMAGE_MODE){
            if(source.equals(Constants.VIDEO_LIST)){
                selectTab = FIRST_TAB
            }else if(source.equals(Constants.BLOG_LIST)){
                selectTab = SECOND_TAB
            }else{
                selectTab = THIRD_TAB
            }

            if(!getTrimmedText(binding.edSearchH).isNullOrEmpty()){
                query = ""
                binding.edSearchH.setText("")
            }
            setSelectedTab(selectTab)
            IS_IMAGE_MODE = false

        }
        setBottomSectionVisibility()

    }

    override fun onPause() {
        super.onPause()
        Log.d("visibility_log", "on pause")
    }

    private fun setupObservers() {
        forumsViewModel.getMyQuestionResponseData.observe(this, questionListResponseObserver)
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        forumsViewModel.getVideosResponseData.observe(this, videosListResponseObserver)
        forumsViewModel.likeCommentResponseData.observe(this, likeCOmmentResponseObserver)
        forumsViewModel.addQuestionResponseData.observe(this, addQuestionResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
    }
    private val fileUploadResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData

        FILE_URL = responseObserver.message
        binding.executePendingBindings()
        binding.invalidateAll()
        Log.d("insert_img_log","question pic set "+FILE_URL)
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

    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                   // showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
                    var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                    var bundle = Bundle()
                    bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                    addOrEditAppointmentFragment.arguments = bundle
                    CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, addOrEditAppointmentFragment, this, false)
                }else{
                    showToast(FourBaseCareApp.activityFromApp, "Doctor not found!")
                }
            }
        }

    private fun showDoctorDetailsDialogue(doctorDetails: com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails) {

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
        
        if(!doctorDetails.dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(doctorDetails.dpLink).placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        tvName.setText(doctorDetails.firstName)
        tvDesignation.setText(doctorDetails.designation)
        tvSpecialization.setText(doctorDetails.specialization)
        //tvExperience.setText("05 years")
        tvExperience.setText(CommonMethods.getStringWithOnePadding(doctorDetails.experience)+" years")
        tvHospitalName.setText(doctorDetails.hospital)

        if(doctorDetails.isVerified){
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(ivVerified)
        }else{
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(ivVerified)
        }

        tvAddDoctor.setOnClickListener(View.OnClickListener {
            Log.d("details_log","6")
            doctorDetailsDialogue.dismiss()
            selectedDoctor = Doctor()
            selectedDoctor.firstName = doctorDetails.firstName
            selectedDoctor.doctorId = doctorDetails.doctorId
            selectedDoctor.displayPicUrl = doctorDetails.dpLink
            selectedDoctor.designation = doctorDetails.designation
            selectedDoctor.verified = doctorDetails.isVerified
            selectedDoctor.consultationFee = doctorDetails.consultationFee.toDouble()
            assignDoctor(""+doctorDetails.doctorId)
        })

        Log.d("details_log","5")
        doctorDetailsDialogue.show()
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

    private val questionListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("list_log","response came")
        if(!responseObserver.isNullOrEmpty()){
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            Log.d("q_list_log","my questions list size "+arrayList.size)
            binding.recyclerView.apply {
                   layoutManager = LinearLayoutManager(activity)
                   myLayoutManager = layoutManager as LinearLayoutManager
                   myQuestionsListingAdapter = MyQuestionsListingAdapter(arrayList, this@AllVideosListingFragment)
                   adapter = myQuestionsListingAdapter
                   myQuestionsList.clear()
                   myQuestionsList.addAll(arrayList.asReversed())
                   myQuestionsListingAdapter.submitList(myQuestionsList)

            }
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val addQuestionResponseObserver = androidx.lifecycle.Observer<BaseResponse>{baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Question posted successfully!")

            if(source.equals(Constants.QUESTION_LIST)){
                Log.d("api_call_log","2")
                getDataFromServer("" , 0 ) }

            //fragmentManager?.popBackStack()
        }

    }

    private val likeCOmmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer(query, pageNo)
        }

    }



    private fun setupTrendingVideosRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            myLayoutManager = layoutManager as LinearLayoutManager
            if(source.equals(Constants.VIDEO_LIST)){
                liveVideosListingAdapter = LiveVideosListingAdapter(list, this@AllVideosListingFragment)
                adapter = liveVideosListingAdapter
                liveVideosListingAdapter.submitList(videosList)
            }else if(source.equals(Constants.BLOG_LIST)){
                liveBlogssListingAdapter = LiveForumsListingAdapter(list, this@AllVideosListingFragment)
                adapter = liveBlogssListingAdapter
                liveBlogssListingAdapter.submitList(blogsList)
            }else{
                myQuestionsListingAdapter = MyQuestionsListingAdapter(list, this@AllVideosListingFragment)
                adapter = myQuestionsListingAdapter
                myQuestionsListingAdapter.submitList(questionsList)
                Log.d("q_list_log","all questions list size "+list.size)
            }
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
    }

    private fun getDataFromServer(searchKey: String, pageNo : Int) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("api_call_log","called")
            var searchQueryInput = SearchQueryInput()
            searchQueryInput.pageSize = 500
            searchQueryInput.reqPageNo = pageNo
            searchQueryInput.searchKey = searchKey
            searchQueryInput.sortBy = "id"
            searchQueryInput.sortOrder = "Asc"

            Timer().schedule(Constants.FUNCTION_DELAY) {
               if(source.equals(Constants.VIDEO_LIST)){
                   forumsViewModel.callGetAllVideos(Constants.VIDEO_LIST,getUserAuthToken(), searchQueryInput)
               }else if(source.equals(Constants.BLOG_LIST)){
                   forumsViewModel.callGetAllVideos(Constants.BLOG_LIST,getUserAuthToken(), searchQueryInput)
               }else{
                   Log.d("api_call_log","came here")
                   if (binding.switchMyQuestion.isChecked) {
                       Log.d("api_call_log","my que called")
                       forumsViewModel.callGetMyQuestions(getUserAuthToken())
                   }
                   else {
                       Log.d("api_call_log","All que called")
                       forumsViewModel.callGetAllVideos(Constants.QUESTION_LIST,getUserAuthToken(), searchQueryInput)
                   }
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

    private fun setClickListeners() {

        binding.ivTitleSearch.setOnClickListener(View.OnClickListener {
                  binding.relSearchContainer.visibility = View.VISIBLE
                  binding.linTitleContainer.visibility = View.GONE
        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            binding.relSearchContainer.visibility = View.GONE
            binding.linTitleContainer.visibility = View.VISIBLE
            if(!query.isNullOrEmpty()){
                query = ""
                binding.edSearchH.setText("")
                getDataFromServer(query,0)
            }

        })

        binding.switchMyQuestion.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            run {
                if (isChecked) forumsViewModel.callGetMyQuestions(getUserAuthToken())
                else getDataFromServer(query,pageNo)
            }

        })

        binding.ivAddQuestion.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())
                showAddQuestionDialogue()
            //showBottomSheetFragment()
        })

/*
        binding.ivSearch.setOnClickListener(View.OnClickListener {
            binding.linTitleContainer.visibility = View.GONE
            binding.relSearchContainer.visibility = View.VISIBLE

        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            binding.linTitleContainer.visibility = View.VISIBLE
            binding.relSearchContainer.visibility = View.GONE
        })
*/

        binding.tvFirstTab.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                source = Constants.VIDEO_LIST
                setSelectedTab(FIRST_TAB)
                binding.switchMyQuestion.isSelected = false
            }
        })

        binding.tvSecondTab.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                source = Constants.BLOG_LIST
                setSelectedTab(SECOND_TAB)
                binding.switchMyQuestion.isSelected = false
            }
        })

        binding.tvThirdTab.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                source = Constants.QUESTION_LIST
                setSelectedTab(THIRD_TAB)
            }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.edSearchH.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                query = s.toString()
                getDataFromServer(query, pageNo)

            }

            override fun afterTextChanged(s: Editable) {}
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


    override fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view: View) {
        val intent = Intent(FourBaseCareApp.activityFromApp, VideoViewerActivity::class.java)
        intent.putExtra(Constants.YOUTUBE_URL, item.content)
        intent.putExtra(Constants.VIDEO_OBJ, item)
        intent.putExtra(Constants.SOURCE, "onco_hub")
        startActivityForResult(intent, Constants.ACTIVITY_RESULT)
        FourBaseCareApp.activityFromApp.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        isAPiLoading = isLoading
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val videosListResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->
        Log.d("list_log","response came")
        if(!responseObserver.isNullOrEmpty()){
            showHideNoData(false)
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            if(selectTab == FIRST_TAB){
                videosList.addAll(arrayList.asReversed())
                setupTrendingVideosRecyclerView(videosList)
            }else if(selectTab == SECOND_TAB){
                blogsList.addAll(arrayList.asReversed())
                setupTrendingVideosRecyclerView(blogsList)
            }else{
                questionsList.addAll(arrayList.asReversed())
                setupTrendingVideosRecyclerView(questionsList)
            }
        }
        else{
           showHideNoData(true)
        }
        binding.executePendingBindings()
        binding.invalidateAll()
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

            else if(view.id == R.id.linCOntent){
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

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if(isVisibleToUser){
            Log.d("visibility_log","true")
            query = ""
        }else{
            Log.d("visibility_log","false")
            query = ""
            //binding.edSearchH.setText("")
        }
    }

    private fun setBottomSectionVisibility() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden")
                        binding.ivAddQuestion.visibility = View.GONE
                        /*val parentFrag: HomeBottomNavFragment =
                            this@AllVideosListingFragment.getParentFragment() as HomeBottomNavFragment
                        parentFrag.showHideBottomBar(true)*/

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        binding.ivAddQuestion.visibility = View.VISIBLE
                        /*val parentFrag: HomeBottomNavFragment = this@AllVideosListingFragment.getParentFragment() as HomeBottomNavFragment
                        parentFrag.showHideBottomBar(false)*/
                    }
                }
            })
    }

    private fun likePost(trendingBlogsDetails:TrendingVideoDetails){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), ""+trendingBlogsDetails.post.id)
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
                    "Hi! can you answer this question? " + item.content + "\n\n You can ask or answer questions like this onOnco buddy app. Download link : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }else if(view.id == R.id.relImage){
                Log.d("details_log","0 id "+item.post.author.userId)
                Log.d("details_log","0 role "+item.post.author.role)
                if(checkIFPatient() && item.post.author.role != null && item.post.author.role.equals(Constants.ROLE_DOCTOR) && !item.post.anonymous){
                    Log.d("details_log","APi called")
                    getDoctorDetails(""+item.post.author.userId)
                }

            }else if(view.id == R.id.relAnsImage){
                Log.d("details_log","0 c_id "+item.comments.get(0).comment.author.userId)
                Log.d("details_log","0")
                Log.d("details_log","patient "+checkIFPatient())
                Log.d("details_log","role "+item.comments.get(0).comment.author.role)
                if(checkIFPatient() && item.comments.get(0).comment.author.role != null
                    && item.comments.get(0).comment.author.role.equals(Constants.ROLE_DOCTOR) && !item.comments.get(0).comment.isAnonymous){
                    Log.d("details_log","condition true")
                    getDoctorDetails(""+item.comments.get(0).comment.author.userId)
                }else{
                    Log.d("details_log","condition false")
                }

            }
        }

    }

    private fun getDoctorDetails(doctorId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun likeComment(item: CommentItem){


        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("like_log","1")
            forumsViewModel.likeOrUnlikeComment(
                getUserAuthToken(), ""+item.comment.id)
        }

    }

    private fun setSelectedTab(activeTab: Int) {

        selectTab = activeTab
        Log.d("edit_log", "selected tab " + activeTab)

        binding.tvFirstTab.setBackgroundColor(
            if (selectTab == FIRST_TAB) getResourceColor(R.color.bg_blue) else getResourceColor(R.color.white)
        )

        binding.tvSecondTab.setBackgroundColor(
            if (selectTab == SECOND_TAB) getResourceColor(R.color.bg_blue) else getResourceColor(
                R.color.white
            )
        )

        binding.tvThirdTab.setBackgroundColor(
            if (selectTab == THIRD_TAB) getResourceColor(R.color.bg_blue) else getResourceColor(
                R.color.white
            )
        )


        binding.tvFirstTab.setTextColor(
            if (selectTab == FIRST_TAB) getResourceColor(R.color.white) else getResourceColor(
                R.color.bg_blue
            )
        )


        binding.tvSecondTab.setTextColor(
            if (selectTab == SECOND_TAB) getResourceColor(R.color.white) else getResourceColor(
                R.color.bg_blue
            )
        )

        binding.tvThirdTab.setTextColor(
            if (selectTab == THIRD_TAB) getResourceColor(R.color.white) else getResourceColor(
                R.color.bg_blue
            )
        )

        //show switch only when buddytalks is on
        if (selectTab == THIRD_TAB) binding.linMyQuestion.visibility = View.VISIBLE
        else binding.linMyQuestion.visibility = View.GONE

        setTitleText()
        if (!getTrimmedText(binding.edSearchH).isNullOrEmpty()) {
            query = ""
            binding.edSearchH.setText("")
        }
        pageNo = 0
        Log.d("api_call_log", "0")
        if (selectTab == FIRST_TAB) {
            if (videosList.isNullOrEmpty()) {
                getDataFromServer(query, 0)
            } else {
                setupTrendingVideosRecyclerView(videosList)
            }
        } else if (selectTab == SECOND_TAB) {
            if (blogsList.isNullOrEmpty()) {
                getDataFromServer(query, 0)
            } else {
                setupTrendingVideosRecyclerView(blogsList)
            }
        } else {
            if(binding.switchMyQuestion.isChecked){
              if (myQuestionsList.isNullOrEmpty()) {
                 forumsViewModel.callGetMyQuestions(getUserAuthToken())
            } else {
                setupTrendingVideosRecyclerView(myQuestionsList)

            }
            }
            else{
            if (questionsList.isNullOrEmpty()) {
                getDataFromServer(query, 0)
            } else {
                setupTrendingVideosRecyclerView(questionsList)

            }
            }



        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  Constants.ACTIVITY_RESULT){
            if (resultCode == Activity.RESULT_OK) {
                Log.d("activity_result","got result here in listing")
                query = ""
                getDataFromServer(query, pageNo)
            }
        }else if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_GALLERY_IMAGE){
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
                Log.d("visibility_log","Activity resume came")
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

                    FILE_PATH = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, resultUri)
                    uploadFileToS3()
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error: java.lang.Exception = result.getError()
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                }
            }

        }
    }
    private fun uploadFileToS3(){
        if(checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            Log.d("insert_img_log","File path1.2 "+FILE_PATH)

            val body: MultipartBody.Part
            val file  = File(FILE_PATH)

            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(),body)
            Log.d("insert_img_log","2 "+FILE_PATH)

        }
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
            IS_IMAGE_MODE = true
            if(isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        ivRefresh.setOnClickListener(View.OnClickListener {
            IS_IMAGE_MODE = true
            if(isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        ivRemoveImage.setOnClickListener(View.OnClickListener {
            FILE_URL = ""
            cardImage?.visibility = View.GONE
            cardAddImage?.visibility = View.VISIBLE
        })

        btnYes.setOnClickListener(View.OnClickListener {
            if(CommonMethods.getTrimmedText(edQuestion).isNullOrEmpty()){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp,"Please enter your question!")
            }else{
                addQuestion(getTrimmedText(edQuestion), getTrimmedText(edQuestionTitle), switchAnonymous.isChecked)
                askQuestionDalogue.dismiss()
            }


            /* addGuestToAppointment(
                 getTrimmedText(edName),
                 getTrimmedText(edMobile),
                 getTrimmedText(edEmail)
             )*/
        })

        val btnNo: TextView = askQuestionDalogue.findViewById(R.id.btnNo)

        btnNo.setOnClickListener(View.OnClickListener {
            askQuestionDalogue.dismiss()
        })
        askQuestionDalogue.show()
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
        IS_IMAGE_MODE = true
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
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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


    private fun addQuestion(que: String, queTitle: String, isAnonymous: Boolean){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            var addQuestionInput  =  AddQuestionInput()
            var postQuestionDto = PostQuestionDto()
            postQuestionDto.question = que
            postQuestionDto.title = queTitle
            if(!FILE_URL.isNullOrEmpty()) postQuestionDto.attachmentLink = FILE_URL
            addQuestionInput.postQuestionDto = postQuestionDto
            addQuestionInput.isAnonymous = isAnonymous

            addQuestionInput.postType = "QUESTION"

            forumsViewModel.callAddQuestion("",
                getUserAuthToken(), addQuestionInput)
        }

    }



}