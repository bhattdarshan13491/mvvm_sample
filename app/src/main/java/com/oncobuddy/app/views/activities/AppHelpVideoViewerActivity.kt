package com.oncobuddy.app.views.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityAppHelpVideoViewBinding
import com.oncobuddy.app.utils.Constants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo
import java.util.*

/**
 * App help video viewer activity
 * Takes Youtube url from arguments and shows video with youtube video player
 * @constructor Create empty App help video viewer activity
 */
class AppHelpVideoViewerActivity : AppCompatActivity(){

    private lateinit var binding: ActivityAppHelpVideoViewBinding
    private var youtubeId: String? = null
    private var youtubeUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_help_video_view)
        initializeYoutubePlayer()
        setClickListeners()
    }

    private fun initializeYoutubePlayer() {
        youtubeUrl = intent.getStringExtra(Constants.YOUTUBE_URL).toString()
        val temp = youtubeUrl.split("v=").toTypedArray()
        youtubeId = temp[1]
        binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                //super.onReady(youTubePlayer)
                youTubePlayer.loadOrCueVideo(lifecycle, "" + youtubeId, 0f)

            }
        })
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.release()
    }

    private fun setClickListeners() {

        binding.ivBack.setOnClickListener(View.OnClickListener {
               finish()
        })


    }




}