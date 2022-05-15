package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAppHelpVideosListBinding
import com.oncobuddy.app.databinding.FragmentChatListBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.NovChat
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.AppHelpVideoViewerActivity
import com.oncobuddy.app.views.activities.VideoViewerActivity
import com.oncobuddy.app.views.adapters.*
import java.util.*
import kotlin.collections.ArrayList


class NovVideosListingFragment : BaseFragment(), VerticalVideosAdapter.Interaction{

    private lateinit var binding : FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private var videosList = ArrayList<TrendingVideoDetails>()
    private lateinit var liveVideosListingAdapter: VerticalVideosAdapter
    private lateinit var forumsViewModel: ForumsViewModel
    //private lateinit var profileViewModel: ProfileViewModel
    private var updatedItemPos = -1
    private lateinit var trendingVideoDetails: TrendingVideoDetails

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
        binding.toolbarTitle.setText(arguments?.getString("category_name"))
        binding.ivSearch.visibility = View.GONE
        //binding.edSearch.visibility = View.GONE

        setClickListeners()
        setRecyclerView()
        setupVM()
        setupObservers()
    }

    private fun setupObservers() {
        forumsViewModel.likePostResponseData.observe(this, likePostResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val likePostResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { baseResponse ->

            if (baseResponse.success) {
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, "" + baseResponse.message)
                if(updatedItemPos != -1){
                    Constants.IS_LIST_UPDATED = true
                    Log.d("community_log", "response "+updatedItemPos)

                    if(trendingVideoDetails.isLiked){
                        trendingVideoDetails.postLikes += + 1
                    }else{
                        if(trendingVideoDetails.isLiked){
                            trendingVideoDetails.postLikes -= 1
                        }
                    }


                    videosList.set(updatedItemPos, trendingVideoDetails)
                    liveVideosListingAdapter.notifyItemChanged(updatedItemPos)
                    liveVideosListingAdapter.notifyDataSetChanged()
                    updatedItemPos = -1
                }

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

    private fun setupVM() {
        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun setRecyclerView() {

        arguments!!.getParcelableArrayList<TrendingVideoDetails>(Constants.VIDEO_LIST)?.let { videosList.addAll(it) }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            liveVideosListingAdapter = VerticalVideosAdapter(videosList, this@NovVideosListingFragment)
            adapter = liveVideosListingAdapter
            liveVideosListingAdapter.submitList(videosList)
        }
    }


    private fun likePost(trendingBlogsDetails: TrendingVideoDetails) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("expert_log", "2")
            forumsViewModel.likeOrUnlikePost(
                getUserAuthToken(), "" + trendingBlogsDetails.post.id
            )
        }

    }

    override fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if(!isDoubleClick()) {
            if (view.id == R.id.ivPost || view.id == R.id.linCOmments) {
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
            }else if (view.id == R.id.linLike) {
                Log.d("community_log", "1 "+position)
                 updatedItemPos = position
                likePost(trendingBlogsDetails = item)
                trendingVideoDetails = item
                trendingVideoDetails.isLiked = !trendingVideoDetails.isLiked

            }else if (view.id == R.id.ivShare) {
                CommonMethods.shareApp(
                    FourBaseCareApp.activityFromApp,
                    "Hi! check  this great post\n\n" + item.content + "\n\n To find more contents like this, download Onco buddy app : https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                )

            }

        }


    }


}