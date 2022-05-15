package com.oncobuddy.app.views.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityVideoViewBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.forums.comments.Comment
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.models.pojo.forums.comments.GetCommentsInput
import com.oncobuddy.app.models.pojo.forums.post_details.PostDetailsResponseNew
import com.oncobuddy.app.models.pojo.forums.trending_blogs.AddCommentInput
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.CommonMethods.shareApp
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.CommentsAdapter
import com.oncobuddy.app.views.fragments.AddOrEditAppointmentFragment
import com.oncobuddy.app.views.fragments.PatientHomeScreenNewFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Video viewer activity
 * Video viewer screen which takes youtube url from the arguments and  playes video
 * @constructor Create empty Video viewer activity
 */

class VideoViewerActivity : AppCompatActivity(), CommentsAdapter.Interaction{

    private lateinit var binding: ActivityVideoViewBinding
    private var youtubeId: String? = null
    private var youtubeUrl: String = ""
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var trendingVideoDetails: TrendingVideoDetails
    private var isLiked = false
    var index = 0
    private val MAX_CLICK_INTERVAL = 3000
    private var mLastClickTime: Long = 0
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var selectedDoctor: Doctor
    private var videoId =""
    private var EDIT_MODE = false
    private var commentId = ""
    private lateinit var deleteConfirmationDialogue: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_view)
        setupVM()
        setupObservers()
        setClickListeners()
        Log.d("video_comment","opened")
        if(intent != null){

            if(intent.hasExtra(Constants.SOURCE) && intent.getStringExtra(Constants.SOURCE).equals("notification")){
                intent.getStringExtra(""+Constants.VIDEO_ID)?.let { getPostDetailsFromServer(it) }
            }else{
                Log.d("video_comment","came here")
                youtubeUrl = intent.getStringExtra(Constants.YOUTUBE_URL).toString()
                trendingVideoDetails = intent.getParcelableExtra(Constants.VIDEO_OBJ)!!
                if(trendingVideoDetails.post == null){
                    videoId = intent.getStringExtra(Constants.VIDEO_ID)!!
                    Log.d("video_comment","video id "+videoId)
                }
                setVideoDetails()
                //getPostDetailsFromServer(""+videoId)
                getCommentsDataFromServer()
            }

        }



    }

    private fun setVideoDetails() {
        isLiked = trendingVideoDetails.isLiked
        Log.d("youtube_log", "" + youtubeUrl)
        Log.d("get_video_log","0 "+isLiked)


        if (!trendingVideoDetails.videoDesc.isNullOrEmpty()) {
            binding.tvDescription.setText(trendingVideoDetails.videoDesc)

            if (trendingVideoDetails.videoDesc.length > 150) {
                binding.tvDescription.text = Html.fromHtml(
                    trendingVideoDetails.videoDesc.substring(
                        0,
                        150
                    ) + "..." + "<font color='#35b1c4'> <u>View More</u></font>"
                )
            } else {
                binding.tvDescription.text = trendingVideoDetails.videoDesc
            }

            binding.tvDescription.setOnClickListener {
                if (binding.tvDescription.text.toString().endsWith("View More")) {
                    binding.tvDescription.text = trendingVideoDetails.videoDesc
                } else {
                    if (trendingVideoDetails.videoDesc.length > 150) {
                        binding.tvDescription.text = Html.fromHtml(
                            trendingVideoDetails.videoDesc.substring(
                                0,
                                150
                            ) + "..." + "<font color='#35b1c4'> <u>View More</u></font>"
                        )

                    } else binding.tvDescription.text = trendingVideoDetails.videoDesc
                }
            }
        }

        if (youtubeUrl.split("v=").size > 1) {
            val temp = youtubeUrl.split("v=").toTypedArray()
            youtubeId = temp[1]
            binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    //super.onReady(youTubePlayer)
                    youTubePlayer.loadOrCueVideo(lifecycle, "" + youtubeId, 0f)

                }
            })
        }

        /*getCommentsDataFromServer()
        //setCommentsListView()
        setLikeView()
*/

        setLikeView()
        binding.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(trendingVideoDetails.postLikes.toString()))

        if (trendingVideoDetails.postLikes > 0) {
            binding.tvLikesCOunt.setText(
                CommonMethods.getStringWithOnePadding(
                    trendingVideoDetails.postLikes.toString()
                )
            )
        } else {
            binding.tvLikesCOunt.setText("00")
        }

        Log.d("get_video_log","setting like count "+trendingVideoDetails.postLikes.toString())
    }

    private fun setLikeView(){
        Log.d("video_like","4")
        Log.d("get_video_log","0 "+isLiked)
        if(!isLiked){
            binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@VideoViewerActivity,R.drawable.like_img))
        }else{
            binding.ivLike.setImageDrawable(ContextCompat.getDrawable(this@VideoViewerActivity,R.drawable.ic_like_filled))
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }

    private fun setClickListeners() {
        binding.ivShare.setOnClickListener {
            if(!isDoubleClick()){
                Log.d("video_like","Sharing")
                shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great post\n\n" + youtubeUrl + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )
            }

        }


        binding.linLike.setOnClickListener(View.OnClickListener {
                  if(!isDoubleClick()){
                      likePost()
                  }
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                finishActivityWithResult()
            }
        })

        binding.ivReply.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                if(EDIT_MODE){
                    addCOmment(""+commentId)
                }else{
                    addCOmment(""+videoId)
                }
            }
        })
    }

    private fun finishActivityWithResult() {
        Log.d("activity_result", "set result in video viewer")
        val intent =
            Intent(this@VideoViewerActivity, PatientHomeScreenNewFragment::class.java)
        setResult(RESULT_OK, intent)
        finish()
    }

    fun isDoubleClick(): Boolean {
        if (SystemClock.elapsedRealtime() - mLastClickTime < MAX_CLICK_INTERVAL) {
            Log.d("Returned", "Returned")
            return true
        }
        Log.d("Success", "Success")
        mLastClickTime = SystemClock.elapsedRealtime()
        return false
    }

    private fun setCommentsListView() {
        var commentsList =  ArrayList<CommentItem>()
        var commentItem = CommentItem()
        var comment = Comment()
        comment.id = 1
        comment.authorUserName = "Darshan Bhatt"
        comment.content = "Really an interesting one!"
        commentItem.comment = comment
        commentsList.add(commentItem)


        commentItem = CommentItem()
        var comment2 = Comment()
        comment2.id = 2
        comment2.authorUserName = "Dinesh Gandhe"
        comment2.content = "Wow! Really an informative one!"
        commentItem.comment = comment2
        commentsList.add(commentItem)



        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoViewerActivity)
            commentsAdapter = CommentsAdapter(commentsList, this@VideoViewerActivity,getLoggedInUserIdd(), this@VideoViewerActivity)
            adapter = commentsAdapter
            commentsAdapter.submitList(commentsList)
        }
    }

    override fun onCommentItemSelected(position: Int, item: CommentItem, view: View) {
        if(view.id == R.id.linLikeCOntainer){
                 likeComment(item)
        }else if (view.id == R.id.relImage) {
            Log.d("details_log", "0")
            if (getUserObject().role.equals(Constants.ROLE_PATIENT) && item.comment.author.role != null && item.comment.author.role.equals(Constants.ROLE_DOCTOR) && !item.comment.anonymous) {
                getDoctorDetails("" + item.comment.author.userId)
            }
        }else if(view.id == R.id.ivMenu){
            val popupMenu = PopupMenu(this@VideoViewerActivity, view)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_discussion_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_edit ->{
                        EDIT_MODE = true
                        commentId = ""+item.comment.id
                        binding.edComment.setText(""+item.comment.content)
                        binding.edComment.requestFocus()
                    }
                    R.id.menu_delete -> {
                        showDeleteConfirmDialogue(item)
                    }
                }
                true
            }

            // Showing the popup menu
            popupMenu.show()
        }


    }

    private fun getDoctorDetails(doctorId: String) {
        if (checkInterNetConnection(this@VideoViewerActivity)) {
            Log.d("details_log","1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun setupVM() {
        forumsViewModel = ViewModelProvider(
            this@VideoViewerActivity,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)
        profileViewModel = ViewModelProvider(
            this@VideoViewerActivity,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }
    private fun likePost(){

        Log.d("video_like","-1")
        if (checkInterNetConnection(this@VideoViewerActivity)) {
            Log.d("video_like","0")
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), ""+videoId)

        }

    }

    private fun likeComment(item: CommentItem){

        if (checkInterNetConnection(this@VideoViewerActivity)) {
            forumsViewModel.likeOrUnlikeComment(
                getUserAuthToken(), ""+item.comment.id)
        }

    }

    private fun setupObservers() {
        forumsViewModel.commentDeleteResponseData.observe(this, deleteCOmmentResponseObserver)
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        forumsViewModel.getPostDetailsResponseData.observe(this, getVideoDetailsObserver)
        forumsViewModel.likeCommentResponseData.observe(this, likeCOmmentResponseObserver)
        forumsViewModel.getCommentsResponseData.observe(this, commentsListObserver)
        forumsViewModel.addCommentResponseData.observe(this, addCOmmentsResponseObserver)
        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val deleteCOmmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(this@VideoViewerActivity, ""+baseResponse.message)
            Constants.IS_LIST_UPDATED = true
            getCommentsDataFromServer()
        }

    }

    private val doctorDetailsResponseObserver = androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            showDoctorDetailsDialogue(responseObserver.payLoad)

        }else{
            CommonMethods.showToast(this@VideoViewerActivity, "Something went wrong while getting doctor details!")
        }

    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val likePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            Log.d("video_like","3")
            Constants.IS_LIST_UPDATED = true
            CommonMethods.showToast(this@VideoViewerActivity, ""+baseResponse.message)
            isLiked = !isLiked
            setLikeView()
            setLikeCount()
            Constants.IS_HOME_SCREEN_UPDATED = true
        }

    }

    private fun setLikeCount() {
        Log.d("like_count","0")
        var likeCount = 0
        if (trendingVideoDetails.postLikes != null) {
            likeCount = trendingVideoDetails.postLikes
            Log.d("like_count", "not null")
        }
        if (isLiked) {
            likeCount += 1
            Log.d("like_count","count + "+likeCount)
        } else {
            likeCount -= 1
            Log.d("like_count","count - "+likeCount)
        }

        trendingVideoDetails.postLikes = likeCount

        if (likeCount > 0) {
            binding.tvLikesCOunt.setText(
                CommonMethods.getStringWithOnePadding(
                    likeCount.toString()
                )
            )
        } else {
            binding.tvLikesCOunt.setText("00")
        }

    }
    private val assignDoctorResponseObserver =
        androidx.lifecycle.Observer<BaseResponse?>{ responseObserver ->
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver != null) {
                if(responseObserver.success){
                    //CommonMethods.showToast(this@VideoViewerActivity, responseObserver.message)
                    var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                    var bundle = Bundle()
                    bundle.putParcelable(Constants.DOCTOR_DATA, selectedDoctor)
                    addOrEditAppointmentFragment.arguments = bundle
                    CommonMethods.addNextFragment(this@VideoViewerActivity, addOrEditAppointmentFragment, null, false)
                }else{
                    CommonMethods.showToast(this@VideoViewerActivity, "Doctor not found!")
                }
            }
        }

    

    private val likeCOmmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(this@VideoViewerActivity, ""+baseResponse.message)
            getCommentsDataFromServer()
            Constants.IS_LIST_UPDATED = true
        }

    }

    private val getVideoDetailsObserver = androidx.lifecycle.Observer<PostDetailsResponseNew>{ postDetails ->

        if(postDetails.isSuccess){
            trendingVideoDetails = TrendingVideoDetails()
            trendingVideoDetails.videoDesc = postDetails.payLoad.description
            trendingVideoDetails.isLiked = false // As video has been posted, It will not be liked
            youtubeUrl = ""+postDetails.payLoad.videoLink
            Log.d("get_video_log","Video "+postDetails.payLoad.videoLink)
            Log.d("get_video_log","Video 2 "+youtubeUrl)
            setVideoDetails()
        //getDataFromServer()
        }

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(this@VideoViewerActivity, message)
    }

    private val addCOmmentsResponseObserver = androidx.lifecycle.Observer<BaseResponse>{baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(this@VideoViewerActivity, ""+baseResponse.message)
            getCommentsDataFromServer()
            Constants.IS_LIST_UPDATED = true
            //fragmentManager?.popBackStack()
        }

    }

    private val commentsListObserver = androidx.lifecycle.Observer<List<CommentItem>>{ responseObserver ->

        Log.d("list_log","response came")
        var commentsList =  ArrayList<CommentItem>()
        if(!responseObserver.isNullOrEmpty()) {
            commentsList.addAll(responseObserver.asReversed())
        }
            Log.d("comment_adap_log","Added "+commentsList.size)
            binding.tvCommentsCount.setText(""+CommonMethods.getStringWithOnePadding(""+commentsList.size))
           // binding.tvCommentsCount.setText(""+CommonMethods.getStringWithOnePadding(""+commentsList.size))
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this@VideoViewerActivity)
                commentsAdapter = CommentsAdapter(commentsList, this@VideoViewerActivity, getLoggedInUserIdd(),this@VideoViewerActivity)
                adapter = commentsAdapter
                commentsAdapter.submitList(commentsList)
            }

        binding.executePendingBindings()
        binding.invalidateAll()
        if(EDIT_MODE) EDIT_MODE = false
    }

    private fun getCommentsDataFromServer() {
        if (checkInterNetConnection(this@VideoViewerActivity)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                var getCommentsInput  =  GetCommentsInput()
                getCommentsInput.pageSize = 5000
                getCommentsInput.reqPageNo = 0
                getCommentsInput.sortBy = "publishedDateTime"
                getCommentsInput.sortOrder = "Desc"
                forumsViewModel.callGetComments(getUserAuthToken(),""+videoId,getCommentsInput)
            }
        } else {
            Toast.makeText(
                this@VideoViewerActivity,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getPostDetailsFromServer(postId : String) {
        if (checkInterNetConnection(this@VideoViewerActivity)) {
               forumsViewModel.getPostDetails(getUserAuthToken(), postId)
        } else {
            Toast.makeText(
                this@VideoViewerActivity,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addCOmment(id: String){

        if (checkInterNetConnection(this@VideoViewerActivity)) {
            Log.d("video_comment","0")
            if(CommonMethods.getTrimmedText(binding.edComment).isNullOrEmpty()){
                CommonMethods.showToast(this@VideoViewerActivity, "Please enter comment")
                return
            }
            var addCommentInput  =  AddCommentInput()
            addCommentInput.content = CommonMethods.getTrimmedText(binding.edComment)
            addCommentInput.isAnonymous = binding.switchAnonymous.isChecked
            addCommentInput.commentType = "COMMENT"
            addCommentInput.commentParentId = 0

            Log.d("video_comment","1")
            forumsViewModel.callAddCOmment(EDIT_MODE,
                getUserAuthToken(), ""+id, addCommentInput)

            Log.d("video_comment","2")
            binding.edComment.setText("")
            CommonMethods.hideKeyboard(this@VideoViewerActivity)
        }

    }

    // base activity methods
    fun showHideProgress(
        isShowProgress: Boolean,
        progressBar: FrameLayout
    ) {
        if (isShowProgress) {
            progressBar.visibility = View.VISIBLE
            progressBar.isEnabled = false
        } else {
            progressBar.visibility = View.GONE
            progressBar.isEnabled = true
        }
    }

    fun checkInterNetConnection(context: Context) : Boolean{

        val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        Toast.makeText(context,"Please check your internet connection", Toast.LENGTH_SHORT).show()
        return false
    }

    fun getUserAuthToken() : String {
        if(!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "").isNullOrEmpty()) {
            val gson = Gson()
            val json: String? =
                FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
            val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
            return userObj.authToken
        }else{
            return  ""
        }
    }

    fun getLoggedInUserIdd() : Int {
        if(!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "").isNullOrEmpty()) {
            val gson = Gson()
            val json: String? =
                FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
            val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
            return userObj.userIdd
        }else{
            return  -1
        }
    }

    override fun onBackPressed() {
        Log.d("activity_result", "on back press set result in video viewer")
        finishActivityWithResult()
    }

    private fun showDoctorDetailsDialogue(doctorDetails: com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetails) {

        doctorDetailsDialogue = Dialog(this@VideoViewerActivity)
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
            Glide.with(this@VideoViewerActivity).load(doctorDetails.dpLink).placeholder(R.drawable.ic_user_image).circleCrop().into(ivProfilePic)
        tvName.setText(doctorDetails.firstName)
        tvDesignation.setText(doctorDetails.designation)
        tvSpecialization.setText(doctorDetails.specialization)
        tvExperience.setText(CommonMethods.getStringWithOnePadding(doctorDetails.experience)+" years")
        tvHospitalName.setText(doctorDetails.hospital)

        if(doctorDetails.isVerified){
            Glide.with(this@VideoViewerActivity).load(R.drawable.ic_verified).into(ivVerified)
        }else{
            Glide.with(this@VideoViewerActivity).load(R.drawable.ic_not_verified).into(ivVerified)
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
        if (checkInterNetConnection(this@VideoViewerActivity)) {
            Log.d("assign_log","1")
            profileViewModel.assignDoctor(
                false,
                doctorId,
                getUserAuthToken()
            )
            Log.d("assign_log","2")

        }
    }

    fun getUserObject() : LoginDetails {
        val gson = Gson()
        val json: String? = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")
        val userObj: LoginDetails = gson.fromJson(json, LoginDetails::class.java)
        return userObj
    }

    private fun showDeleteConfirmDialogue(commentItem: CommentItem) {
        Log.d("show_delete","0")
        //deleteConfirmationDialogue = Dialog(this@VideoViewerActivity)
        deleteConfirmationDialogue = Dialog(this@VideoViewerActivity)
        deleteConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(deleteConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = deleteConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        deleteConfirmationDialogue.window?.setAttributes(lp)
        deleteConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = deleteConfirmationDialogue.findViewById(R.id.btnDelete)
        val tvMsg: TextView = deleteConfirmationDialogue.findViewById(R.id.tvMsg)

        tvMsg.setText(this@VideoViewerActivity.getString(R.string.are_you_sure_you_want_to_delete_this_comment))


        btnDelete.setOnClickListener(View.OnClickListener {
            //deleteRecordFromServer(obj)
            deleteComment(commentItem)
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
        Log.d("show_delete","1")
    }

    private fun deleteComment(item: CommentItem){

        if (checkInterNetConnection(this@VideoViewerActivity)) {
            forumsViewModel.deleteComment(
                getUserAuthToken(), ""+item.comment.id)
        }

    }
}