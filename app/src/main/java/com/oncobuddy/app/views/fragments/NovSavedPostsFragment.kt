package com.oncobuddy.app.views.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentSavedPostsBinding
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.views.activities.VideoViewerActivity
import com.oncobuddy.app.views.adapters.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class NovSavedPostsFragment : BaseFragment(), SavedPostsAdapter.Interaction{

    private lateinit var binding : FragmentSavedPostsBinding
    private var videosList = ArrayList<TrendingVideoDetails>()
    private lateinit var savedPostsAdapter: SavedPostsAdapter
    private lateinit var forumsViewModel: ForumsViewModel

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
            R.layout.fragment_saved_posts, container, false
        )
        setClickListeners()
        setRecyclerView()
        setupVM()
        setupObservers()
        getDataFromServer()
    }

    private fun setupObservers() {
        forumsViewModel.getSavedPostsResponseData.observe(this, savedPostsResponseObserver)
        forumsViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        forumsViewModel.onMessageError.observe(this, errorMessageObserver)
    }


        private fun setupVM() {
        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)
    }


    private fun getDataFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    forumsViewModel.callGetSavedPosts(getUserAuthToken())
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
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun setRecyclerView() {

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            savedPostsAdapter = SavedPostsAdapter(videosList, this@NovSavedPostsFragment)
            adapter = savedPostsAdapter
            savedPostsAdapter.submitList(videosList)
        }
    }



    override fun onSavedPostSelected(position: Int, item: TrendingVideoDetails, view: View) {
        if(!isDoubleClick()) {
            if (view.id == R.id.ivPost) {
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
        }


    }

    private val savedPostsResponseObserver = androidx.lifecycle.Observer<List<TrendingVideoDetails>>{ responseObserver ->

        Log.d("saved_list_log","response came")
        if(!responseObserver.isNullOrEmpty()){
            showHideNoData(false)
            Log.d("saved_list_log","no data false")
            var arrayList = ArrayList<TrendingVideoDetails>()
            arrayList.addAll(responseObserver)
            setupRecyclerView(arrayList)
            binding.executePendingBindings()
            binding.invalidateAll()

        }else{
            //binding.linTrendingBlogsTitle.visibility = View.GONE
            showHideNoData(true)
        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("saved_list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun setupRecyclerView(list: ArrayList<TrendingVideoDetails>) {
        Log.d("saved_list_log","list size "+list.size)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            savedPostsAdapter = SavedPostsAdapter(list, this@NovSavedPostsFragment)
            adapter = savedPostsAdapter
            savedPostsAdapter.submitList(list)
        }
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
}