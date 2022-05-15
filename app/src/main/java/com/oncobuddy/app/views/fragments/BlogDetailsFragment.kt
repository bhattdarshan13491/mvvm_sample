package com.oncobuddy.app.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentWebviewBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.models.pojo.forums.comments.GetCommentsInput
import com.oncobuddy.app.models.pojo.forums.post_details.PostDetailsResponseNew
import com.oncobuddy.app.models.pojo.forums.trending_blogs.AddCommentInput
import com.oncobuddy.app.models.pojo.forums.trending_videos.Post
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.CommentsAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Blog details fragment
 * Shows the article updated from the admin side and handles comments
 * @constructor Create empty Blog details fragment
 */

class BlogDetailsFragment : BaseFragment(),CommentsAdapter.Interaction {

    private lateinit var binding: FragmentWebviewBinding
    private lateinit var trendingBlogsDetails: TrendingVideoDetails
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private var isLiked = false
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var doctorDetailsDialogue: Dialog
    private lateinit var selectedDoctor: Doctor
    private var EDIT_MODE = false
    private var commentId = ""
    private lateinit var deleteConfirmationDialogue: Dialog


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
            R.layout.fragment_webview, container, false
        )
        Log.d("notification_click_lg", "blog 1")
        setupVM()
        setupObservers()
        setupClickListeners()
        setDataFromArguments()
        binding.layoutHeader.ivLikeNew.visibility = View.GONE
        binding.layoutHeader.ivShare.visibility = View.GONE
        binding.webView.isVerticalScrollBarEnabled = true
        binding.webView.isHorizontalScrollBarEnabled = true
    }



    private fun setupClickListeners() {
        binding.ivShare.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())
            CommonMethods.shareApp(
                FourBaseCareApp.activityFromApp,
                "Hi! check  this great article named " +
                        trendingBlogsDetails.post.title + "\n\nTo find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )


        })

        binding.ivLike.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) likePost()
        })



        binding.ivReply.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) {
                if(EDIT_MODE){
                    addCOmment(""+commentId)
                }else{
                    addCOmment(""+trendingBlogsDetails.post.id)
                }

            }
        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) fragmentManager?.popBackStack()
        })

    }

    private fun setDataFromArguments() {
        binding.webView.settings.setJavaScriptEnabled(true)
        if (arguments != null) {
            if (arguments!!.containsKey(Constants.BLOG_OBJ)) {
                trendingBlogsDetails = arguments!!.getParcelable(Constants.BLOG_OBJ)!!
                binding.layoutHeader.tvTitle.setText(trendingBlogsDetails.post.title)
                //binding.layoutHeader.tvSubTitle.setText(trendingBlogsDetails.authorName)
                Log.d("web_view_log", "content " + trendingBlogsDetails.post.author.name)
                binding.webView.loadData(trendingBlogsDetails.content, "text/html", "UTF-8")
                isLiked = trendingBlogsDetails.isLiked
                setLikeView()
                binding.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(trendingBlogsDetails.postLikes.toString()))
                getDataFromServer()
                binding.layoutHeader.ivBack.visibility = View.VISIBLE
                if(!trendingBlogsDetails.blogImageUrl.isNullOrEmpty()){
                    binding.ivBlogImage.visibility = View.VISIBLE
                    Glide.with(FourBaseCareApp.activityFromApp)
                        .load(trendingBlogsDetails.blogImageUrl)
                        .placeholder(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.place_holder_blog))
                        .into(binding.ivBlogImage)

                }else{
                 binding.ivBlogImage.visibility = View.GONE
                }

            }else if(arguments!!.containsKey(Constants.BLOG_ID)){
                Log.d("notification_click_lg", "blog 2")
                arguments!!.getString(""+Constants.BLOG_ID)?.let { getPostDetailsFromServer(it) }
                binding.layoutHeader.ivBack.visibility = View.GONE
            }
        }
    }

    private fun getPostDetailsFromServer(postId : String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.getPostDetails(getUserAuthToken(), postId)
        } else {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
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
    private fun likePost(){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), ""+trendingBlogsDetails.post.id)
        }

    }

    private fun setupObservers() {
        forumsViewModel.doctorDetailsResponseData.observe(this, doctorDetailsResponseObserver)
        forumsViewModel.getPostDetailsResponseData.observe(this, getBlogDetailsObserver)
        profileViewModel.assignDoctorResonseData.observe(this, assignDoctorResponseObserver)
        forumsViewModel.commentDeleteResponseData.observe(this, deleteCOmmentResponseObserver)
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        forumsViewModel.likeCommentResponseData.observe(this, likeCOmmentResponseObserver)
        forumsViewModel.addCommentResponseData.observe(this, addCOmmentsResponseObserver)
        forumsViewModel.getCommentsResponseData.observe(this, commentsListObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val getBlogDetailsObserver = androidx.lifecycle.Observer<PostDetailsResponseNew>{ postDetails ->

        if(postDetails.isSuccess){
            Log.d("notification_click_lg", "blog 2")
            trendingBlogsDetails = TrendingVideoDetails()
            var post = Post()
            post.id = postDetails.payLoad.post.id
            post.title = postDetails.payLoad.post.title
            post.author.name = postDetails.payLoad.post.author.name
            trendingBlogsDetails.post = post
            trendingBlogsDetails.videoDesc = postDetails.payLoad.description
            trendingBlogsDetails.isLiked = false // As video has been posted, It will not be liked
            //getDataFromServer()
            trendingBlogsDetails.content = postDetails.payLoad.content
            binding.layoutHeader.tvTitle.setText(trendingBlogsDetails.post.title)
            //binding.layoutHeader.tvSubTitle.setText(trendingBlogsDetails.authorName)
            Log.d("web_view_log", "content " + trendingBlogsDetails.post.author.name)
            binding.webView.loadData(trendingBlogsDetails.content, "text/html", "UTF-8")

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

    private val doctorDetailsResponseObserver = androidx.lifecycle.Observer<DoctorDetailsResponse> { responseObserver ->
        binding.executePendingBindings()
        binding.invalidateAll()

        Log.d("details_log","3")
        if (responseObserver.isSuccess) {
            Log.d("details_log","4")
            showDoctorDetailsDialogue(responseObserver.payLoad)
        }else{
            showToast(FourBaseCareApp.activityFromApp, "SOmething went wrong while getting doctor details!")
        }

    }

    private val deleteCOmmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer()
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

        tvAddDoctor.setText("Book an appointment")


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


    private val likeCOmmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer()
        }

    }

    private val addCOmmentsResponseObserver = androidx.lifecycle.Observer<BaseResponse>{baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer()
            //fragmentManager?.popBackStack()
        }

    }

    private val commentsListObserver = androidx.lifecycle.Observer<List<CommentItem>>{ responseObserver ->

        Log.d("comment_adap_log","response came")
        var commentsList =  ArrayList<CommentItem>()

        if(!responseObserver.isNullOrEmpty()) {
            commentsList.addAll(responseObserver.asReversed())
        }
            Log.d("comment_adap_log","Added "+commentsList.size)
            binding.tvCommentsCount.setText(""+CommonMethods.getStringWithOnePadding(""+commentsList.size))
            //binding.webView.visibility = View.GONE
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                commentsAdapter = CommentsAdapter(commentsList, this@BlogDetailsFragment,getUserIdd(), FourBaseCareApp.activityFromApp)
                adapter = commentsAdapter
                commentsAdapter.submitList(commentsList)
        }

        binding.executePendingBindings()
        binding.invalidateAll()
        if(EDIT_MODE) EDIT_MODE = false
    }

    private fun getDataFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                var getCommentsInput  =  GetCommentsInput()
                getCommentsInput.reqPageNo = 0
                getCommentsInput.sortBy = "id"
                getCommentsInput.sortOrder = "Asc"
                forumsViewModel.callGetComments(getUserAuthToken(),""+trendingBlogsDetails.post.id,getCommentsInput)
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addCOmment(id: String){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            if(getTrimmedText(binding.edComment).isNullOrEmpty()){
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Please enter comment")
                return
            }
            var addCommentInput  =  AddCommentInput()
            addCommentInput.content = getTrimmedText(binding.edComment)
            addCommentInput.isAnonymous = binding.checkAnonymous.isChecked
            addCommentInput.commentType = "COMMENT"
            addCommentInput.commentParentId = 0


            forumsViewModel.callAddCOmment(
                EDIT_MODE,getUserAuthToken(), ""+id, addCommentInput)

            binding.edComment.setText("")
            CommonMethods.hideKeyboard(FourBaseCareApp.activityFromApp)
        }

    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val likePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            isLiked = !isLiked

            setLikeCount()
            setLikeView()
            Constants.IS_HOME_SCREEN_UPDATED = true
        }

    }

    private fun setLikeCount() {
        Log.d("like_count","0")
        var likeCount = 0
        if (trendingBlogsDetails.postLikes != null) {
            likeCount = trendingBlogsDetails.postLikes
            Log.d("like_count", "not null")
        }
            if (isLiked) {
                likeCount += 1
                Log.d("like_count","count + "+likeCount)
            } else {
                likeCount -= 1
                Log.d("like_count","count - "+likeCount)
            }

              trendingBlogsDetails.postLikes = likeCount

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

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun setLikeView(){
        if(isLiked){
            binding.ivLike.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.ic_like_filled))
        }else{
            binding.ivLike.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.like_img))
        }
    }

    override fun onCommentItemSelected(position: Int, item: CommentItem, view: View) {
        if(view.id == R.id.linLikeCOntainer){
            Log.d("itemm_clicks_log", "0")
            likeComment(item)
        }else if (view.id == R.id.relImage) {
            Log.d("itemm_clicks_log", "1")
            if (checkIFPatient() && item.comment.author.role != null && item.comment.author.role.equals(Constants.ROLE_DOCTOR)) {
                getDoctorDetails("" + item.comment.author.userId)
            }else{
                Log.d("itemm_clicks_log", "issue")
            }
        }else if(view.id == R.id.ivMenu){
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
            popupMenu.menuInflater.inflate(R.menu.menu_discussion_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_edit ->{
                        popupMenu.dismiss()
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
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("details_log","1")
            forumsViewModel.callGetDoctorDetails(getUserAuthToken(), doctorId)
        }
    }

    private fun likeComment(item: CommentItem){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.likeOrUnlikeComment(
                getUserAuthToken(), ""+item.comment.id)
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

    private fun showDeleteConfirmDialogue(commentItem: CommentItem) {
        //deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
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

        tvMsg.setText(FourBaseCareApp.activityFromApp.getString(R.string.are_you_sure_you_want_to_delete_this_comment))


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
    }

    private fun deleteComment(item: CommentItem){

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.deleteComment(
                getUserAuthToken(), ""+item.comment.id)
        }

    }

}