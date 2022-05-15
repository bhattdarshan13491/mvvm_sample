package com.oncobuddy.app.views.fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
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
import com.oncobuddy.app.databinding.NovFragmentWebviewBinding
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
import com.itextpdf.text.pdf.PdfFileSpecification.url

import android.webkit.WebViewClient
import com.itextpdf.text.xml.xmp.DublinCoreProperties.setTitle

import androidx.databinding.adapters.SeekBarBindingAdapter.setProgress

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.databinding.adapters.SeekBarBindingAdapter
import com.itextpdf.text.xml.xmp.DublinCoreProperties


class NovWebViewFragment : BaseFragment(){

    private lateinit var binding: NovFragmentWebviewBinding
    private var webUrl = ""
    private var title =""

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
            R.layout.nov_fragment_webview, container, false
        )
        setupClickListeners()
        setDataFromArguments()
    }



    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()) fragmentManager?.popBackStack()
        })

    }

    private fun setDataFromArguments() {
        binding.webView.settings.setJavaScriptEnabled(true)
        if (arguments != null) {
            if (arguments!!.containsKey("url")) {
                webUrl = arguments!!.getString("url").toString()
                title = arguments!!.getString("title").toString()

                binding.webView.getSettings().setJavaScriptEnabled(true)
                binding.webView.setWebViewClient(WebViewClient())
                binding.webView.loadUrl(webUrl)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH)
                }
                binding.webView.setWebChromeClient(object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView, progress: Int) {

                        if (progress >= 75){
                            Log.d("web_view_log", "progress done "+progress)
                            binding.layoutProgress.frProgress.visibility = View.GONE
                            binding.webView.visibility = View.VISIBLE
                        }else{
                            Log.d("web_view_log", "progress "+progress)
                            binding.layoutProgress.frProgress.visibility = View.VISIBLE
                            binding.webView.visibility = View.GONE

                        }
                    }
                })
                binding.tvTitle.setText(title)

            }
        }
    }


}