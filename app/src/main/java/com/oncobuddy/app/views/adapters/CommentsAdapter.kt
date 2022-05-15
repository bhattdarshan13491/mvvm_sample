package com.oncobuddy.app.views.adapters

import android.content.Context
import android.text.Html
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
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawCommentBinding
import com.oncobuddy.app.databinding.RawNovCommentBinding
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import java.util.*


class CommentsAdapter(private var forumsList : ArrayList<CommentItem>,
                      private val interaction: Interaction? = null, private val loggedInUserId: Int = -1, private val context: Context? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //var dailyQuestionsList = ArrayList<DailyCon>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentItem>() {

        override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            return oldItem.comment.id == newItem.comment.id
        }

        override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawNovCommentBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_nov_comment, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,
            loggedInUserId,
            context
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

    fun submitList(list: List<CommentItem>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawNovCommentBinding,
        private val interaction: Interaction?,
        private val loggedInUserId: Int = -1,
        private val context: Context?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawNovCommentBinding = binding

        fun bind(item: CommentItem, pos : Int) {
            Log.d("comment_adap_log","role "+item.comment.author.role)

            if(item.liked){
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.ic_like_filled)).into(mBinder.ivLike)
            }else{
                Glide.with(itemView.context).load(ContextCompat.getDrawable(itemView.context, R.drawable.like_img)).into(mBinder.ivLike)
            }

            if(!item.comment.anonymous && item.comment.author.role != null && item.comment.author.role.equals(Constants.ROLE_DOCTOR)){
                mBinder.ivVerified.visibility = View.VISIBLE
                if(item.comment.author.verified){
                    Log.d("comment_adap_log","Verified true "+context)
                    if (context != null) {
                        Glide.with(context).load(R.drawable.ic_verified).into(mBinder.ivVerified)
                    }
                }else{
                    Log.d("comment_adap_log","Verified false "+mBinder.ivVerified.visibility)
                    if (context != null) {
                        Glide.with(context).load(R.drawable.ic_not_verified).into(mBinder.ivVerified)
                    }
                }
            }else{
                Log.d("comment_adap_log","Verified gone")
                mBinder.ivVerified.visibility = View.GONE
            }

            Log.d("comment_adap_log","rel image")

            if(!item.comment.anonymous){
                mBinder.relImage.setOnClickListener(View.OnClickListener {
                    interaction?.onCommentItemSelected(pos, item, mBinder.relImage)
                })
            }


            if(item.comment.publishedDateTime.isNullOrEmpty()){
                mBinder.tvDate.setText("")
            }else{
                mBinder.tvDate.setText(CommonMethods.changeCOmmentDateTimeFormat(item.comment.publishedDateTime))
            }

            if(context != null && item.comment.author.dpLink != null && !item.comment.anonymous){
                Log.d("comment_adap_log","link "+item.comment.author.dpLink)
                         Glide.with(context)
                        .load(item.comment.author.dpLink)
                        .placeholder(R.drawable.ic_user_image)
                        .circleCrop()
                        .into(mBinder.ivUserProfile)

            }else{
                if (context != null) {
                    Glide.with(context)
                        .load(R.drawable.ic_user_image)
                        .circleCrop()
                        .into(mBinder.ivUserProfile)
                }
            }

            if(item.comment.anonymous) {
                mBinder.tvName.setText("Anonymous")
                mBinder.tvHeadline.setText("")
            }else{
                if(item.comment.author != null){
                    mBinder.tvName.setText(item.comment.author.name)
                    mBinder.tvHeadline.setText(item.comment.author.headline)
                }

            }
            mBinder.tvComment.setText(item.comment.content)

            if (item.comment.content.length > 150) {
                mBinder.tvComment.text = Html.fromHtml(item.comment.content.substring(0, 150) + "..." + "<font color='#35b1c4'> <u>View More</u></font>")
            } else {
                mBinder.tvComment.text = item.comment.content
            }
            mBinder.tvComment.setOnClickListener {
                if (mBinder.tvComment.text.toString().endsWith("View More")) {
                    mBinder.tvComment.text = item.comment.content
                } else {
                    if (item.comment.content.length > 150) {
                        mBinder.tvComment.text = Html.fromHtml(item.comment.content.substring(0, 150) + "..." + "<font color='#35b1c4'> <u>View More</u></font>")
                    } else mBinder.tvComment.text = item.comment.content
                }
            }


            if(item.numberOfLikes != null && item.numberOfLikes > 0){
                if(item.liked) {
                    mBinder.tvLikesCount.setText(CommonMethods.getStringWithOnePadding(item.numberOfLikes.toString()))
                }else{
                    mBinder.tvLikesCount.setText(CommonMethods.getStringWithOnePadding(item.numberOfLikes.toString()))
                }
            }else{
                //mBinder.tvLike.setText("Like")
                mBinder.tvLikesCount.setText("00")
            }

            /*if(item.comment.author.userId == loggedInUserId){
                    mBinder.ivMenu.visibility = View.VISIBLE
                mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                    interaction?.onCommentItemSelected(pos, item, mBinder.ivMenu)
                })
            }else{
                    mBinder.ivMenu.visibility = View.GONE
            }*/




            mBinder.linLikeCOntainer.setOnClickListener(View.OnClickListener {
                interaction?.onCommentItemSelected(pos, item, mBinder.linLikeCOntainer)
            })

        }
    }

    interface Interaction {
        fun onCommentItemSelected(position: Int, item: CommentItem, view : View)
    }




}
