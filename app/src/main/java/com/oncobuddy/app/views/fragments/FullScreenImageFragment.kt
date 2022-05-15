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
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.*
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.AppHelpVideoViewerActivity
import com.oncobuddy.app.views.adapters.AlliedCategoriesAdapter
import com.oncobuddy.app.views.adapters.AppHelpVideosAdapter
import com.oncobuddy.app.views.adapters.ChatListAdapter
import com.oncobuddy.app.views.adapters.NovChatAdapter
import java.util.*
import kotlin.collections.ArrayList


class FullScreenImageFragment : BaseFragment(){

    private lateinit var binding : FragmentFullScreenImageBinding
    private var url = ""


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
            R.layout.fragment_full_screen_image, container, false
        )
        url = arguments?.getString(Constants.IMAGE_URL,"")!!

        if(!url.isNullOrEmpty()){
            Glide.with(FourBaseCareApp.activityFromApp).load(url).into(binding.ivImage)
        }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

    }



}