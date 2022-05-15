package com.oncobuddy.app.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ItemFeedBinding
import com.oncobuddy.app.databinding.ItemVerticalVideoBinding
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.pagination.PaginationAdapterCallback


class VerticalVideosAdapter(private var youtubeVideosList: ArrayList<TrendingVideoDetails>,
                            private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var youtubeFilterList = ArrayList<TrendingVideoDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingVideoDetails>() {

        override fun areItemsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            var isItemSame = oldItem.post.id == newItem.post.id
            Log.d("pagination_log", "olditem id : "+oldItem.post.id)
            Log.d("pagination_log", "newitem id : "+newItem.post.id)
            Log.d("pagination_log", "Item same : "+isItemSame)
            return isItemSame
        }

        override fun areContentsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            //var isContentSame = oldItem.post.id == newItem.post.id
            var isContentSame = oldItem.equals(newItem)
            Log.d("pagination_log", "contents same : "+isContentSame)
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: ItemVerticalVideoBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.item_vertical_video, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShoppingItemVH -> {
                holder.bind(differ.currentList.get(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<TrendingVideoDetails>) {
        differ.submitList(list)
    }


    class ShoppingItemVH constructor(
        binding: ItemVerticalVideoBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: ItemVerticalVideoBinding = binding

        fun bind(item: TrendingVideoDetails, pos: Int) {

            mBinder.tvTitle.setText(item.post.title)
            mBinder.tvCOntent.setText(item.videoDesc)


            mBinder.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(""+item.postLikes))
            mBinder.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding(item.commentsCount.toString()))

            mBinder.tvDate.setText(CommonMethods.convertTimeSlotFormat1(item.post.publishedDate))

            mBinder.ivPost.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.ivPost)
            })

            mBinder.ivShare.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.ivShare)
            })

            mBinder.linLike.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.linLike)
            })

            mBinder.linCOmments.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.linCOmments)
            })

            if(item.isLiked){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.ic_like_filled)).into(mBinder.ivLike)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.like_img)).into(mBinder.ivLike)
            }

            if(item.content.split("v=").size>1){
                val url = "https://img.youtube.com/vi/" + item.content.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.placeholder)).into(mBinder.ivPost)

            }

        }

    }
    interface Interaction {
        fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view: View)
    }
}



