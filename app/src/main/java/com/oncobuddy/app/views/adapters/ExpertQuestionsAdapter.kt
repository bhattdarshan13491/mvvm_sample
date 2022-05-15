package com.oncobuddy.app.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
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


class ExpertQuestionsAdapter(private var youtubeVideosList: ArrayList<TrendingVideoDetails>,
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

            if(item.questionTitle.isNullOrEmpty()){
                mBinder.tvSubject.visibility = View.GONE
            }else{
                mBinder.tvSubject.visibility = View.VISIBLE
                mBinder.tvSubject.text = item.questionTitle
            }

            mBinder.tvPost.setText(item.content)
            mBinder.tvProfileName.setText(item.author.name)
            if(item.author.role != null && item.author.role.equals(Constants.ROLE_PATIENT)){
               mBinder.tvSubName.visibility = View.GONE
            }else{
                mBinder.tvSubName.visibility = View.VISIBLE
                mBinder.tvSubName.setText(item.author.headline)
            }

            if(!item.author.dpLink.isNullOrEmpty())
                Glide.with(FourBaseCareApp.activityFromApp)
                    .load(item.author.dpLink)
                    .placeholder(R.drawable.ic_user_image)
                    .circleCrop()
                    .into(mBinder.ivImage)

            if(item.author.role == Constants.ROLE_DOCTOR){
                if(item.author.isVerified){
                    mBinder.ivVerified.visibility = View.VISIBLE
                    Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(mBinder.ivVerified)
                }else{
                    Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(mBinder.ivVerified)
                }
            }else{
                       mBinder.ivVerified.visibility = View.GONE
            }

            mBinder.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(""+item.postLikes))
            mBinder.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding(item.commentsCount.toString()))

            mBinder.tvPostedOn.setText(CommonMethods.convertTimeSlotFormat1(item.post.publishedDate))

            if(item.questionAttachmentUrl == null){
                mBinder.ivPost.visibility = View.GONE
            }else{
                if(item.questionAttachmentType != null && item.questionAttachmentType.equals(Constants.ATTACHEMENT_TYPE_IMAGE)){
                    mBinder.ivPost.visibility = View.VISIBLE
                    mBinder.tvFileName.visibility = View.GONE
                    Glide.with(itemView.context).load(item.questionAttachmentUrl).into(mBinder.ivPost)
                    mBinder.ivPost.setOnClickListener(View.OnClickListener {
                        interaction?.onExpertQuestionSelected(pos, item, mBinder.ivPost)
                    })
                }else if(item.questionAttachmentType != null && item.questionAttachmentType.equals(Constants.ATTACHEMENT_TYPE_DOCUMENT)){
                    run {
                    }
                        mBinder.tvFileName.visibility = View.VISIBLE
                        mBinder.ivPost.visibility = View.GONE
                        var fileName = URLUtil.guessFileName(item.questionAttachmentUrl, null, null)
                        mBinder.tvFileName.text = "View Attachement"
                        mBinder.tvFileName.setOnClickListener(View.OnClickListener {
                            interaction?.onExpertQuestionSelected(pos, item, mBinder.tvFileName)
                        })
                    }

            }

            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onExpertQuestionSelected(pos, item, mBinder.ivMenu)
            })


            mBinder.linCOmments.setOnClickListener(View.OnClickListener {
                interaction?.onExpertQuestionSelected(pos, item, mBinder.linCOmments)
            })

            mBinder.linLike.setOnClickListener(View.OnClickListener {
                interaction?.onExpertQuestionSelected(pos, item, mBinder.linLike)
            })

            mBinder.ivShare.setOnClickListener(View.OnClickListener {
                interaction?.onExpertQuestionSelected(pos, item, mBinder.ivShare)
            })

            mBinder.ivSave.setOnClickListener(View.OnClickListener {
                interaction?.onExpertQuestionSelected(pos, item, mBinder.ivSave)
            })

            if(item.isLiked){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.ic_like_filled)).into(mBinder.ivLike)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.like_img)).into(mBinder.ivLike)
            }

            if(item.saved){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.save_icon_filled)).into(mBinder.ivSave)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.save_icon)).into(mBinder.ivSave)
            }

        }

    }
    interface Interaction {
        fun onExpertQuestionSelected(position: Int, item: TrendingVideoDetails, view: View)
    }
}



