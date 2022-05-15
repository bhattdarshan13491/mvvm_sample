package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawVideoCategoryBinding
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.VideoCategories
import com.oncobuddy.app.models.pojo.records_list.Record


class VideoCategoriesAdapter(private var youtubeVideosList: ArrayList<VideoCategories>,
                             private val interaction: Interaction? = null, private val videosInterAction: LiveVideosListingAdapter.Interaction) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<VideoCategories>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoCategories>() {

        override fun areItemsTheSame(oldItem: VideoCategories, newItem: VideoCategories): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: VideoCategories, newItem: VideoCategories): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawVideoCategoryBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_video_category, parent, false
        )


        return ShoppingItemVH(
            mBinder,
            interaction,
            videosInterAction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShoppingItemVH -> {
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<VideoCategories>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawVideoCategoryBinding,
        private val interaction: Interaction?,
        private val videosInterAction: LiveVideosListingAdapter.Interaction
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawVideoCategoryBinding = binding

        fun bind(item: VideoCategories, pos : Int) {
            /*mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onVideoCategorySelected(pos, item, mBinder.root)
            })
            */

            mBinder.tvCategoryName.setText(item.name)
            if(!item.trendingVideoDetailsList.isNullOrEmpty())
                setRecyclerView(mBinder.rvVideos, item.trendingVideoDetailsList, videosInterAction)


            mBinder.tvViewAll.setOnClickListener(View.OnClickListener {
                interaction?.onVideoCategorySelected(pos, item, mBinder.tvViewAll)
            })
           /* mBinder.tvTitle.setText(item.title)

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onVideoSelected(pos, item, mBinder.root)
            })

            if(item.videoUrl.split("v=").size > 1) {
                val url = "https://img.youtube.com/vi/" + item.videoUrl.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).into(mBinder.ivThumbnail)

            }*/
        }
        fun setRecyclerView(recyclerView: RecyclerView, list: ArrayList<TrendingVideoDetails>, videosInteraction:LiveVideosListingAdapter.Interaction) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                var liveVideosListingAdapter =
                    LiveVideosListingAdapter(
                        list,
                        videosInteraction,
                        true
                    )
                adapter = liveVideosListingAdapter
                liveVideosListingAdapter.submitList(list)
            }
        }
    }

    interface Interaction {
        fun onVideoCategorySelected(position: Int, item: VideoCategories, view : View)
    }

    fun setRecyclerView(recyclerView: RecyclerView, list: ArrayList<TrendingVideoDetails>, videosInteraction:LiveVideosListingAdapter.Interaction) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            var liveVideosListingAdapter =
                LiveVideosListingAdapter(
                    list,
                    videosInteraction,
                    false
                )
            adapter = liveVideosListingAdapter
            liveVideosListingAdapter.submitList(list)
        }
    }


}
