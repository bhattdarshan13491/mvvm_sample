package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityNovOncoHubBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.OncoHub
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.VideoCategories
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.VideoViewerActivity
import com.oncobuddy.app.views.adapters.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class NovOncoFragment : BaseFragment(),OncoHubButtonsAdapter.Interaction,LiveVideosListingAdapter.Interaction,
    LiveForumsListingAdapter.Interaction, VideoCategoriesAdapter.Interaction, CommunityPostsAdapter.Interaction, ExpertQuestionsAdapter.Interaction {

    private lateinit var binding: ActivityNovOncoHubBinding
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var oncoHubAdapter: OncoHubButtonsAdapter
    private lateinit var videoCategoriesAdapter: VideoCategoriesAdapter
    private lateinit var liveVideosListingAdapter: LiveVideosListingAdapter
    private lateinit var liveBlogssListingAdapter: LiveForumsListingAdapter
    private lateinit var communityPostsAdapter: CommunityPostsAdapter
    private lateinit var expertQuestionsAdapter: ExpertQuestionsAdapter
    //private lateinit var novPostsAdapter: NovPostsAdapter
    private lateinit var deleteConfirmationDialogue: Dialog
    private lateinit var oncoHubTabsList: ArrayList<OncoHub>
    private var videosList = ArrayList<TrendingVideoDetails>()
    private var videoCategoriesList = ArrayList<VideoCategories>()
    private lateinit var qowObj: TrendingVideoDetails


    private var source = Constants.VIDEO_LIST

    private val COMMUNITY_TAB = 0
    private val EXPERTS_TAB = 1
    private val VIDEOS_TAB = 2
    private val BLOGS_TAB = 3
    private var SELECTED_POS = COMMUNITY_TAB

    private var pageNo = 0
    private var query = ""

    private var IS_QOW = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }


    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_nov_onco_hub, container, false)
        SELECTED_POS = 0
        if(arguments != null && arguments!!.containsKey(Constants.SOURCE)){
            source = arguments!!.getString(Constants.SOURCE).toString()

            if(source.equals(Constants.EXPERT_QUESTIONS_LIST)){
             SELECTED_POS = EXPERTS_TAB
            }else if(source.equals(Constants.QUESTION_LIST)){
                SELECTED_POS = COMMUNITY_TAB
            }else if(source.equals(Constants.BLOG_LIST)){
                SELECTED_POS = BLOGS_TAB
            }else if(source.equals(Constants.VIDEO_LIST)){
                SELECTED_POS = VIDEOS_TAB
            }
        }

        if (Constants.IS_DOCTOR_LOGGED_IN) setDrTabsListing()
        else setTabsListing()


        setupVM()
        setupObservers()
        setupClickListeners()
        getDataFromServer("", 0)

        //binding.recyclerView.addOnScrollListener(recyclerViewOnScrollListener)

    }

    private val recyclerViewOnScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("scroll_log", "0 y scroll "+dy+" dx "+dx)
                 if(dy>0){
                     Log.d("scroll_log", "y scroll "+dy)
                     Log.d("scroll_log", "x scroll "+dy)

                }

            }
        }

    private fun showHideSearchBar(shouldSHowSearch: Boolean) {
        if(shouldSHowSearch){
            binding.oncoActionBar.relTitleCOntainer.visibility = View.GONE
            binding.oncoActionBar.linSearchConteiner.visibility = View.VISIBLE
        }else{
            if(!binding.oncoActionBar.edSearch.text.isNullOrEmpty()){
                query = ""
                binding.oncoActionBar.edSearch.setText("")
            }
            binding.oncoActionBar.relTitleCOntainer.visibility = View.VISIBLE
            binding.oncoActionBar.linSearchConteiner.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {


        binding.oncoActionBar.ivMenu.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                     val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, binding.oncoActionBar.ivMenu)
                    popupMenu.menuInflater.inflate(R.menu.menu_oncohub_menu_options, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                        when(menuItem.itemId){
                            R.id.menu_saved_post ->{
                                popupMenu.dismiss()
                                CommonMethods.addNextFragment(
                                    FourBaseCareApp.activityFromApp,
                                    NovSavedPostsFragment(), this, false
                                )

                            }

                        }
                        true
                    }
                    popupMenu.show()
            }
        })

        binding.oncoActionBar.edSearch.addTextChangedListener(object : TextWatcher {
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
                if(s.length>3) {
                    query = s.toString()
                    getDataFromServer(query, pageNo)
                }else if(s.length == 0){
                    query = ""
                    pageNo = 0
                    getDataFromServer(query,pageNo)
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })


        binding.oncoActionBar.ivSearch.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideSearchBar(true)
            }
        })

        binding.oncoActionBar.ivClose.setOnClickListener(View.OnClickListener {
            binding.oncoActionBar.edSearch.setText("")
        })

        binding.oncoActionBar.ivBackResults.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                showHideSearchBar(false)
            }
        })

        binding.layoutPost.cardCOntainer.setOnClickListener(View.OnClickListener {

            if(!isDoubleClick()){
                var bundle = Bundle()

                if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                    Log.d("que_log",""+SELECTED_POS)
                    if (SELECTED_POS == COMMUNITY_TAB) {
                        Log.d("que_log","1")
                        bundle.putString(Constants.SOURCE, Constants.ONCO_DISCUSSIONS_LIST)
                    } else {
                        Log.d("que_log","2")
                        bundle.putString(Constants.SOURCE, Constants.COMMUNITY)
                    }
                }else{
                    if (SELECTED_POS == EXPERTS_TAB) {
                        bundle.putString(Constants.SOURCE, Constants.ASK_EXPERT)
                    } else {
                        bundle.putString(Constants.SOURCE, Constants.COMMUNITY)
                    }
                }


                var addPostFragment = AddPostFragment()
                addPostFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    addPostFragment, this, false
                )
            }
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

        binding.layoutQuestion.linLike.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (::qowObj.isInitialized) {
                    IS_QOW = true
                    likePost(qowObj)
                }

            }

        })

        binding.layoutQuestion.ivPost.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick() && ::qowObj.isInitialized){
                openFullScreenFragment(qowObj.questionAttachmentUrl)
            }
        })



        binding.layoutQuestion.ivShare.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick())
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! can you answer this question?\n\n" + qowObj.content + "\n\n You can ask or answer questions like this onOnco buddy app. Download link : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )


        })


    }

    /*private fun openFullScreenFragment(imgUrl: String) {
        var bundle = Bundle()
        bundle.putString(Constants.IMAGE_URL, imgUrl)
        var fullScreenFragment = FullScreenImageFragment()
        fullScreenFragment.arguments = bundle
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            fullScreenFragment, this, false
        )
    }*/

    private fun getDataFromServer(searchKey: String, pageNo: Int) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("api_call_log", "called")
            var searchQueryInput = SearchQueryInput()
            searchQueryInput.pageSize = 20
            searchQueryInput.reqPageNo = pageNo
            searchQueryInput.searchKey = searchKey
            searchQueryInput.sortBy = "id"
            searchQueryInput.sortOrder = "Desc"

            Log.d("expert_log", "API CALLED FOR " + SELECTED_POS)

            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (SELECTED_POS == COMMUNITY_TAB) {
                    if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                        forumsViewModel.callGetAllVideos(
                            Constants.ONCO_DISCUSSIONS_LIST,
                            getUserAuthToken(),
                            searchQueryInput
                        )
                    }else{
                        forumsViewModel.callGetAllVideos(
                            Constants.COMMUNITY_LIST,
                            getUserAuthToken(),
                            searchQueryInput
                        )
                    }
                    forumsViewModel.callQoW(getUserAuthToken())
                } else if (SELECTED_POS == EXPERTS_TAB) {
                    if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                        forumsViewModel.callGetAllVideos(
                            Constants.EXPERT_QUESTIONS_LIST,
                            getUserAuthToken(),
                            searchQueryInput
                        )
                        forumsViewModel.callQoW(getUserAuthToken())
                    }else{
                        forumsViewModel.callGetAllVideos(
                            Constants.EXPERT_QUESTIONS_LIST,
                            getUserAuthToken(),
                            searchQueryInput)
                    }


                } else if (SELECTED_POS == VIDEOS_TAB) {
                    if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                        forumsViewModel.callGetAllVideos(
                            Constants.COMMUNITY_LIST,
                            getUserAuthToken(),
                            searchQueryInput
                        )
                    }else{
                        forumsViewModel.callGetAllVideos(
                            Constants.VIDEO_LIST,
                            getUserAuthToken(),
                            searchQueryInput
                        )
                    }

                } else if (SELECTED_POS == BLOGS_TAB) {
                    forumsViewModel.callGetAllVideos(
                        Constants.BLOG_LIST,
                        getUserAuthToken(),
                        searchQueryInput
                    )
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

    private fun setupObservers() {
        forumsViewModel.getQoWResponseData.observe(this, qoWResponseObserver)
        forumsViewModel.getVideosResponseData.observe(this, videosListResponseObserver)
        forumsViewModel.likeCommentResponseData.observe(this, likeCOmmentResponseObserver)
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        forumsViewModel.savePostResponseData.observe(this, savePostResponseObserver)
        forumsViewModel.reportPostResponseData.observe(this, reportPostResponseObserver)
        forumsViewModel.postDeleteResponseData.observe(this, deletePostResponseObserver)
        /*forumsViewModel.addQuestionResponseData.observe(this, addQuestionResponseObserver)*/
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun likePost(trendingBlogsDetails: TrendingVideoDetails) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("expert_log", "2")
            forumsViewModel.likeOrUnlikePost(
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

    private fun savePost(trendingBlogsDetails: TrendingVideoDetails) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("expert_log", "2")
            var shouldSave = !trendingBlogsDetails.saved
            forumsViewModel.saveOrUnSavePost(shouldSave,
                getUserAuthToken(), "" + trendingBlogsDetails.post.id
            )
        }

    }

    private fun setupTrendingVideosRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        Log.d("exoert_log", "SELECTED POS " + SELECTED_POS)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            if (SELECTED_POS == COMMUNITY_TAB) {
                communityPostsAdapter = CommunityPostsAdapter(list, this@NovOncoFragment)
                adapter = communityPostsAdapter
                communityPostsAdapter.submitList(list)
            } else if (SELECTED_POS == EXPERTS_TAB) {
                expertQuestionsAdapter = ExpertQuestionsAdapter(list, this@NovOncoFragment)
                adapter = expertQuestionsAdapter
                expertQuestionsAdapter.submitList(list)
            } else if (SELECTED_POS == VIDEOS_TAB) {
               if(Constants.IS_DOCTOR_LOGGED_IN){
                   communityPostsAdapter = CommunityPostsAdapter(list, this@NovOncoFragment)
                   adapter = communityPostsAdapter
                   communityPostsAdapter.submitList(list)
               }else{
                   liveVideosListingAdapter = LiveVideosListingAdapter(list, this@NovOncoFragment, true)
                   adapter = liveVideosListingAdapter
                   liveVideosListingAdapter.submitList(videosList)
               }

            } else if (SELECTED_POS == BLOGS_TAB) {
                liveBlogssListingAdapter = LiveForumsListingAdapter(list, this@NovOncoFragment)
                adapter = liveBlogssListingAdapter
                liveBlogssListingAdapter.submitList(list)
            }/*else{
                myQuestionsListingAdapter = MyQuestionsListingAdapter(list, this@AllVideosListingFragment)
                adapter = myQuestionsListingAdapter
                myQuestionsListingAdapter.submitList(questionsList)
                Log.d("q_list_log","all questions list size "+list.size)
            }*/
        }

    }

    // observers

    private val likeCOmmentResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->

            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
                getDataFromServer(query, pageNo)
            }

        }

    private fun setCetegories(list: List<TrendingVideoDetails>) {
        videoCategoriesList = ArrayList()
        var categorieNamesList = ArrayList<String>()

        Log.d("categories_list", "0")
        Log.d("categories_list", "list size " + list.size)


        for (video in list) {
            if(video.post.categories != null){
                for(categoryObj in video.post.categories){
                if (!categorieNamesList.contains(categoryObj.name)) {
                    categorieNamesList.add(categoryObj.name)
                }
             }

            }else{
                if(categorieNamesList.contains("General")){
                    categorieNamesList.add("General")
                }
            }


        }

        Log.d("categories_list", "total categories found "+categorieNamesList.size)
        for (categoryName in categorieNamesList) {
            Log.d("categories_list", "category " + categoryName)
            var videosList = ArrayList<TrendingVideoDetails>()


            for (videoObj in list) {
                if(videoObj.post.categories != null){
                    for(categoryObj in videoObj.post.categories){
                    if (categoryObj.name.equals(categoryName)) {
                        videosList.add(videoObj)
                        Log.d("categories_list", "category obj " + videoObj.post.id)
                        Log.d("categories_list", "video id added " + videoObj.post.id)
                    }
                  }
                }
            }
            var videoCategory = VideoCategories()
            videoCategory.id = 0
            videoCategory.name = categoryName
            videoCategory.trendingVideoDetailsList = videosList
            videoCategoriesList.add(videoCategory)
            Log.d("categories_list", "category " + categoryName)
            Log.d(
                "categories_list",
                "categories Videos Added " + videoCategory.trendingVideoDetailsList.size
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            videoCategoriesAdapter = VideoCategoriesAdapter(
                videoCategoriesList,
                this@NovOncoFragment,
                this@NovOncoFragment
            )
            adapter = videoCategoriesAdapter
            videoCategoriesAdapter.submitList(videoCategoriesList)
        }


    }

    private val deletePostResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ baseResponse ->

        if(baseResponse.success){
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+baseResponse.message)
            getDataFromServer("", 0)
        }

    }

    private val likePostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->

            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
                if (IS_QOW) {
                    forumsViewModel.callQoW(getUserAuthToken())
                    IS_QOW = false
                } else {
                    Log.d("expert_log","getting refresh data")
                    getDataFromServer("", 0)

                }
                //isLiked = !isLiked
                //setLikeView()
                //Constants.IS_HOME_SCREEN_UPDATED = true
            }

        }

    private val savePostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->
            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
                Log.d("expert_log","getting refresh data")
                getDataFromServer("", 0)
            }
        }

    private val reportPostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->
            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
            }
        }

    private val qoWResponseObserver =
        androidx.lifecycle.Observer<List<TrendingVideoDetails>> { responseObserver ->
            Log.d("list_log", "response came")
            if (!responseObserver.isNullOrEmpty()) {
                qowObj = responseObserver.get(0)
                if(!Constants.IS_DOCTOR_LOGGED_IN)binding.layoutQuestion.cardQuestionContainer.visibility = View.VISIBLE
                binding.layoutQuestion.tvQuestion.setText(qowObj.content)
                binding.layoutQuestion.tvLikeCount.setText(CommonMethods.getStringWithOnePadding("" + qowObj.postLikes))
                binding.layoutQuestion.tvCommentsCount.setText(
                    CommonMethods.getStringWithOnePadding(
                        "" + qowObj.commentsCount
                    ) + " Answered"
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

    private val videosListResponseObserver =
        androidx.lifecycle.Observer<List<TrendingVideoDetails>> { responseObserver ->
            Log.d("expert_log", "response came")
            if (!responseObserver.isNullOrEmpty()) {
                showHideNoData(false)
                var arrayList = ArrayList<TrendingVideoDetails>()
                arrayList.addAll(responseObserver)
                if (SELECTED_POS == COMMUNITY_TAB) {
                    setupTrendingVideosRecyclerView(arrayList)
                } else if (SELECTED_POS == EXPERTS_TAB) {
                    setupTrendingVideosRecyclerView(arrayList)
                } else if (SELECTED_POS == BLOGS_TAB) {
                    setupTrendingVideosRecyclerView(arrayList)
                } else if (SELECTED_POS == VIDEOS_TAB) {
                    if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                        setupTrendingVideosRecyclerView(arrayList)
                    }else{
                        setCetegories(arrayList)
                    }

                }
                /*else{
                    questionsList.addAll(arrayList.asReversed())
                    setupTrendingVideosRecyclerView(questionsList)
                }*/
            } else {
                showHideNoData(true)
            }
            binding.executePendingBindings()
            binding.invalidateAll()
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


    private fun setTabsListing() {
        Log.d("selected_pos", "selected pos " + SELECTED_POS)

        oncoHubTabsList = ArrayList()
        oncoHubTabsList.add(OncoHub(0, "Community", R.drawable.community_selected_icon, true))
        oncoHubTabsList.add(OncoHub(1, "Ask Experts", R.drawable.ask_experts_icon, false))
        oncoHubTabsList.add(OncoHub(2, "Videos", R.drawable.video_icon, false))
        oncoHubTabsList.add(OncoHub(3, "Blogs", R.drawable.blog_icon, false))

        binding.oncoActionBar.rvTabs.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            oncoHubAdapter =
                OncoHubButtonsAdapter(oncoHubTabsList, this@NovOncoFragment, SELECTED_POS)
            adapter = oncoHubAdapter
            oncoHubAdapter.submitList(oncoHubTabsList)
        }

        if(SELECTED_POS > 2)
        binding.oncoActionBar.rvTabs.smoothScrollToPosition(0)

        if (SELECTED_POS == COMMUNITY_TAB) {
            binding.layoutPost.cardCOntainer.visibility = View.VISIBLE

            binding.layoutQuestion.cardQuestionContainer.visibility = View.VISIBLE
            binding.layoutPost.tvShare.text = "Share what\'s in your mind"
            if (!getUserObject().dpLink.isNullOrEmpty()) {
                Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink)
                    .placeholder(R.drawable.ic_profile_circular_white_bg)
                    .error(R.drawable.ic_profile_circular_white_bg).circleCrop()
                    .into(binding.layoutPost.ivProfilePic)

            }
            binding.layoutPost.tvProfileName.setText(getUserObject().firstName)
        } else if (SELECTED_POS == EXPERTS_TAB) {
            binding.layoutPost.cardCOntainer.visibility = View.VISIBLE
            binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
            binding.layoutPost.tvShare.text = "Ask your question to experts"
            if (!getUserObject().dpLink.isNullOrEmpty()) {
                Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink)
                    .placeholder(R.drawable.ic_profile_circular_white_bg)
                    .error(R.drawable.ic_profile_circular_white_bg).circleCrop()
                    .into(binding.layoutPost.ivProfilePic)
            }
            binding.layoutPost.tvProfileName.setText(getUserObject().firstName)
        } else if (SELECTED_POS == VIDEOS_TAB) {
            binding.layoutPost.cardCOntainer.visibility = View.GONE
            binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
        } else if (SELECTED_POS == BLOGS_TAB) {
            binding.layoutPost.cardCOntainer.visibility = View.GONE
            binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
        }

    }

    private fun setDrTabsListing() {
        Log.d("selected_pos", "selected pos " + SELECTED_POS)

        oncoHubTabsList = ArrayList()
        oncoHubTabsList.add(
            OncoHub(
                0,
                "Onco Club",
                R.drawable.ic_onco_discussions_selected,
                true
            )
        )
        oncoHubTabsList.add(OncoHub(1, "Patient Queries", R.drawable.ic_queries, false))
        oncoHubTabsList.add(OncoHub(2, "Community", R.drawable.community_selected_icon, false))

        binding.oncoActionBar.rvTabs.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            oncoHubAdapter =
                OncoHubButtonsAdapter(oncoHubTabsList, this@NovOncoFragment, SELECTED_POS)
            adapter = oncoHubAdapter
            oncoHubAdapter.submitList(oncoHubTabsList)
        }

        Log.d("selected_pos_log","selected pos "+SELECTED_POS)
        if (SELECTED_POS == COMMUNITY_TAB) {
            Log.d("selected_pos_log","came here")
            binding.layoutPost.cardCOntainer.visibility = View.VISIBLE

            if(getUserObject().role.equals(Constants.ROLE_DOCTOR)){
                binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
            }else{
                binding.layoutQuestion.cardQuestionContainer.visibility = View.VISIBLE
            }
            binding.layoutPost.tvShare.text = "Share what\'s in your mind"
            if (!getUserObject().dpLink.isNullOrEmpty()) {
                Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink)
                    .placeholder(R.drawable.ic_profile_circular_white_bg)
                    .error(R.drawable.ic_profile_circular_white_bg).circleCrop()
                    .into(binding.layoutPost.ivProfilePic)

            }
            binding.layoutPost.tvProfileName.setText(getUserObject().firstName)

        } else if (SELECTED_POS == EXPERTS_TAB) {
            Log.d("selected_pos_log","came in experts")
            binding.layoutPost.cardCOntainer.visibility = View.GONE
            binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
            if (!getUserObject().dpLink.isNullOrEmpty()) {
                Glide.with(FourBaseCareApp.activityFromApp).load(getUserObject().dpLink)
                    .placeholder(R.drawable.ic_profile_circular_white_bg)
                    .error(R.drawable.ic_profile_circular_white_bg).circleCrop()
                    .into(binding.layoutPost.ivProfilePic)
            }
            binding.layoutPost.tvProfileName.setText(getUserObject().firstName)
        } else if (SELECTED_POS == VIDEOS_TAB) {
            Log.d("selected_pos_log","videos tab")
            if(Constants.IS_DOCTOR_LOGGED_IN){
                binding.layoutPost.cardCOntainer.visibility = View.VISIBLE
                binding.layoutQuestion.cardQuestionContainer.visibility = View.VISIBLE
            }else{
                binding.layoutPost.cardCOntainer.visibility = View.GONE
                binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
            }

        } else if (SELECTED_POS == BLOGS_TAB) {
            Log.d("selected_pos_log","blogs tab")
            binding.layoutPost.cardCOntainer.visibility = View.GONE
            binding.layoutQuestion.cardQuestionContainer.visibility = View.GONE
        }
    }

    override fun onTabSelected(position: Int, item: OncoHub, view: View) {
        SELECTED_POS = position
        showHideSearchBar(false)
        if (Constants.IS_DOCTOR_LOGGED_IN) setDrTabsListing()
        else{
            setTabsListing()
      /*      item.isSelected = true
            oncoHubTabsList.set(position,item)
            oncoHubAdapter.notifyDataSetChanged()*/
        }
        getDataFromServer("", 0)
    }

    private fun showHideNoData(shouldShowNoData: Boolean) {
        if (shouldShowNoData) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onForumSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {

            if (view.id == R.id.ivLike) {
                likePost(trendingBlogsDetails = item)

            } else if (view.id == R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great article named " + item.post.title + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            } else if (view.id == R.id.relCOntent) {
                var bundle = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ, item)

                var blogDetailsFragment = BlogDetailsFragment()
                blogDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    blogDetailsFragment, this, false
                )
            }
        }

    }

    override fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view: View) {
        val intent = Intent(FourBaseCareApp.activityFromApp, VideoViewerActivity::class.java)
        intent.putExtra(Constants.YOUTUBE_URL, item.content)
        intent.putExtra(Constants.VIDEO_ID, ""+item.post.id)
        intent.putExtra(Constants.VIDEO_OBJ, item)
        intent.putExtra(Constants.SOURCE, "onco_hub")
        startActivityForResult(intent, Constants.ACTIVITY_RESULT)
        FourBaseCareApp.activityFromApp.overridePendingTransition(
            R.anim.anim_right_in,
            R.anim.anim_left_out
        )
    }

    override fun onVideoCategorySelected(position: Int, item: VideoCategories, view: View) {
        if(!isDoubleClick()){
            if(view.id == R.id.tvViewAll){
                Log.d("vertical_video","0")
                var bundle = Bundle()
                bundle.putString("category_name", item.name)
                bundle.putParcelableArrayList(Constants.VIDEO_LIST, item.trendingVideoDetailsList)
                var videoListingFragment = NovVideosListingFragment()
                videoListingFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    videoListingFragment, this, false
                )
                Log.d("vertical_video","SIze "+item.trendingVideoDetailsList)
            }
        }

    }

    override fun onCOmmunityPostSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {
            Log.d("community_log", "0 " + view.id)
            if (view.id == R.id.linLike) {
                Log.d("community_log", "1")
                likePost(trendingBlogsDetails = item)

            } else if (view.id == R.id.ivPost) {
                Log.d("community_log", "2 ")
                if (!item.questionAttachmentUrl.isNullOrEmpty() && item.questionAttachmentType.equals(
                        Constants.ATTACHEMENT_TYPE_IMAGE
                    )
                ) {
                    openFullScreenFragment(item.questionAttachmentUrl)
                }
            } else if (view.id == R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great post named " + item.content + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            } else if (view.id == R.id.linCOmments) {
                var bundle = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ, item)

                Log.d("anonymous_log","0 "+item.post.anonymous)
                var questionDetailsFragment = QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    questionDetailsFragment,
                    this,
                    false
                )
            } else if (view.id == R.id.ivSave) {
                 savePost(item)

            } else if (view.id == R.id.ivMenu) {

                if(getUserObject().userIdd == item.author.userId){
                    val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                    popupMenu.menuInflater.inflate(R.menu.menu_oncohub_options, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                        when(menuItem.itemId){
                            R.id.menu_edit ->{
                                popupMenu.dismiss()
                                var bundle = Bundle()
                                var addPostFragmemt = AddPostFragment()
                                bundle.putString(Constants.SOURCE, Constants.COMMUNITY)
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
                    Log.d("post_id","post id "+item.post.id)
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

    private fun deletePost(postId: String){
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            forumsViewModel.deletePost(getUserAuthToken(), ""+postId)
        }

    }

    override fun onExpertQuestionSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if (!isDoubleClick()) {
            Log.d("expert_log", "0 " + view.id)
            if (view.id == R.id.linLike) {
                Log.d("expert_log", "1")
                likePost(trendingBlogsDetails = item)

            }else if (view.id == R.id.ivSave) {
                Log.d("expert_log", "1")
                savePost(trendingBlogsDetails = item)

            }
            else if (view.id == R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check this great post\n\n"+"Subject :"+item.questionTitle+"\n\n" + item.content + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }else if(view.id == R.id.ivPost){
                Log.d("community_log", "2 ")
                if(!item.questionAttachmentUrl.isNullOrEmpty() && item.questionAttachmentType.equals(Constants.ATTACHEMENT_TYPE_IMAGE)){
                    openFullScreenFragment(item.questionAttachmentUrl)
                }
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

            else if (view.id == R.id.linCOmments) {
                var bundle = Bundle()

                bundle.putParcelable(Constants.BLOG_OBJ, item)
                Log.d("blog_obj","is anonymous "+item.post.anonymous)
                var questionDetailsFragment = QuestionDetailsFragment()
                questionDetailsFragment.arguments = bundle

                CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, questionDetailsFragment, this, false)
            }

            else if (view.id == R.id.ivMenu) {

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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d("api_call_log","Hidden "+hidden)
        if(!hidden){
            //setTitleText()
            if(Constants.IS_LIST_UPDATED){
                Log.d("api_call_log","1")
                getDataFromServer(query,pageNo)
            }
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
}
