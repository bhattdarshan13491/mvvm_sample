package com.oncobuddy.app.views.adapters

import android.os.Build
import android.text.Html
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
import com.oncobuddy.app.databinding.RawForumBinding
import com.oncobuddy.app.databinding.RawLiveVideosBinding
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods


class LiveForumsListingAdapter(private var  trendingBlogsDetailsList: ArrayList<TrendingVideoDetails>,
                               private val interaction: Interaction? = null, private val shouldCutWidth: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingVideoDetails>() {

        override fun areItemsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            return oldItem.post.id == oldItem.post.id
        }

        override fun areContentsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawForumBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_forum, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,
            shouldCutWidth
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

    fun submitList(list: List<TrendingVideoDetails>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawForumBinding,
        private val interaction: Interaction?,
        private val shouldCutWidth: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawForumBinding = binding

        fun bind(item: TrendingVideoDetails, pos : Int) {


            mBinder.tvTitle.text = item.post.title
            mBinder.tvPost.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(item.content, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(item.content)
            }

           // mBinder.tvDescription.text = item.content

            mBinder.tvDate.setText(CommonMethods.convertTimeSlotFormat1(item.post.publishedDate))
            mBinder.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(item.postLikes.toString()))
            mBinder.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding(item.commentsCount.toString()))

            if(!item.blogImageUrl.isNullOrEmpty()){
                Glide.with(itemView.context)
                     .load(item.blogImageUrl)
                    .placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.place_holder_blog))
                    .into(mBinder.ivPost)

            }else{
                Glide.with(itemView.context)
                    .load(ContextCompat.getDrawable(itemView.context, R.drawable.place_holder_blog))
                    .into(mBinder.ivPost)
            }

            if(item.isLiked){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.ic_like_filled)).into(mBinder.ivLike)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.like_img)).into(mBinder.ivLike)
            }

            mBinder.linCOmments.setOnClickListener(View.OnClickListener {
                interaction?.onForumSelected(pos, item, mBinder.linCOmments)
            })

            mBinder.ivLike.setOnClickListener(View.OnClickListener {
                interaction?.onForumSelected(pos, item, mBinder.ivLike)
            })

            mBinder.ivShare.setOnClickListener(View.OnClickListener {
                interaction?.onForumSelected(pos, item, mBinder.ivShare)
            })

            mBinder.relCOntent.setOnClickListener(View.OnClickListener {
                interaction?.onForumSelected(pos, item, mBinder.relCOntent)
            })
        }
    }

    interface Interaction {
        fun onForumSelected(position: Int, item: TrendingVideoDetails, view : View)
    }
}
