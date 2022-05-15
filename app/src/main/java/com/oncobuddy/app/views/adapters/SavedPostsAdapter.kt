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
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.pagination.PaginationAdapterCallback


class SavedPostsAdapter(private var youtubeVideosList: ArrayList<TrendingVideoDetails>,
                        private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var youtubeFilterList = ArrayList<TrendingVideoDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingVideoDetails>() {

        override fun areItemsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            var isItemSame = oldItem.post.id == newItem.post.id
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

        val mBinder: ItemFeedBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.item_feed, parent, false
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
        binding: ItemFeedBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: ItemFeedBinding = binding

        fun bind(item: TrendingVideoDetails, pos: Int) {

            mBinder.tvPost.setText(item.content)

            if(item.post.anonymous) {
                mBinder.tvProfileName.setText("Anonymous")
                mBinder.tvSubName.setText("")
                mBinder.ivVerified.visibility = View.GONE
            }else{
                if(item.post.author != null){
                    mBinder.tvProfileName.setText(item.post.author.name)
                    mBinder.tvSubName.setText(item.post.author.headline)
                    if(!item.post.author.dpLink.isNullOrEmpty())
                        Glide.with(FourBaseCareApp.activityFromApp)
                            .load(item.post.author.dpLink)
                            .placeholder(R.drawable.ic_user_image)
                            .circleCrop()
                            .into(mBinder.ivImage)

                    if(item.post.author.role == Constants.ROLE_DOCTOR){
                        if(item.post.author.isVerified){
                            mBinder.ivVerified.visibility = View.VISIBLE
                            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(mBinder.ivVerified)
                        }else{
                            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(mBinder.ivVerified)
                        }
                    }else{
                        mBinder.ivVerified.visibility = View.GONE
                    }
                }

            }

            mBinder.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(""+item.postLikes))
            mBinder.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding(item.commentsCount.toString()))

            mBinder.tvPostedOn.setText(CommonMethods.convertTimeSlotFormat1(item.post.publishedDate))

            if(item.questionAttachmentUrl == null){
                mBinder.ivPost.visibility = View.GONE
            }else{
                mBinder.ivPost.visibility = View.VISIBLE
                Glide.with(itemView.context).load(item.questionAttachmentUrl).into(mBinder.ivPost)
                mBinder.ivPost.setOnClickListener(View.OnClickListener {
                    interaction?.onSavedPostSelected(pos, item, mBinder.ivPost)
                })
            }
            mBinder.linCOmments.setOnClickListener(View.OnClickListener {
                interaction?.onSavedPostSelected(pos, item, mBinder.linCOmments)
            })

            mBinder.linLike.setOnClickListener(View.OnClickListener {
                interaction?.onSavedPostSelected(pos, item, mBinder.linLike)
            })

            mBinder.ivShare.setOnClickListener(View.OnClickListener {
                interaction?.onSavedPostSelected(pos, item, mBinder.ivShare)
            })

            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onSavedPostSelected(pos, item, mBinder.ivMenu)
            })

            mBinder.ivSave.setOnClickListener(View.OnClickListener {
                interaction?.onSavedPostSelected(pos, item, mBinder.ivSave)
            })

            if(item.isLiked){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.ic_like_filled)).into(mBinder.ivLike)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.like_img)).into(mBinder.ivLike)
            }

        }

    }
    interface Interaction {
        fun onSavedPostSelected(position: Int, item: TrendingVideoDetails, view: View)
    }
}



