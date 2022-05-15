package com.oncobuddy.app.views.adapters

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawQueAndAnsBinding
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants


class MyQuestionsListingAdapter(
    private var trendingVideoDetailsList: ArrayList<TrendingVideoDetails>,
    private val interaction: Interaction? = null,
    private val loggedInUserId: Int = -1
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingVideoDetails>() {

        override fun areItemsTheSame(
            oldItem: TrendingVideoDetails,
            newItem: TrendingVideoDetails
        ): Boolean {
            return oldItem.post.id == newItem.post.id
        }

        override fun areContentsTheSame(
            oldItem: TrendingVideoDetails,
            newItem: TrendingVideoDetails
        ): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawQueAndAnsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_que_and_ans, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,
            loggedInUserId
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
        binding: RawQueAndAnsBinding,
        private val interaction: Interaction?,
        private val loggedInUserId: Int = -1,
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawQueAndAnsBinding = binding

        fun bind(item: TrendingVideoDetails, pos: Int) {
            mBinder.tvPatientName.setText(if (item.post.anonymous) "Anonymous" else item.post.author.name)
            //mBinder.tvPatientHeadline.setText(""+item.headline)

            if (item.post.author.role != null && item.post.author.role.equals(Constants.ROLE_DOCTOR) && !item.post.anonymous) {
                mBinder.ivVerified.visibility = View.VISIBLE
                if (item.post.author.isVerified) {
                    Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified)
                        .into(mBinder.ivVerified)
                } else {
                    Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified)
                        .into(mBinder.ivVerified)
                }
            } else {
                mBinder.ivVerified.visibility = View.GONE
            }

            if (!item.post.author.dpLink.isNullOrEmpty() && !item.post.anonymous)
                Glide.with(FourBaseCareApp.activityFromApp).load(item.post.author.dpLink)
                    .circleCrop().into(mBinder.ivPatientImage)
            else
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_user_image)
                    .circleCrop().into(mBinder.ivPatientImage)

            if (!item.questionAttachmentUrl.isNullOrEmpty()) {
                mBinder.ivQuestionImage.visibility = View.VISIBLE
                Glide.with(FourBaseCareApp.activityFromApp).load(item.questionAttachmentUrl).into(mBinder.ivQuestionImage)
            } else {
                mBinder.ivQuestionImage.visibility = View.GONE
            }

            mBinder.tvQuestionText.setText(item.content)

            if(!item.questionTitle.isNullOrEmpty()){
                mBinder.tvQuestionTitle.setText(item.questionTitle)
                mBinder.tvQuestionTitle.visibility = View.VISIBLE
            }else{
                mBinder.tvQuestionTitle.visibility = View.GONE
            }

            mBinder.tvAnswersCOunt.setText(CommonMethods.getStringWithOnePadding("" + item.commentsCount))

            if (item.post.author.headline != null && !item.post.anonymous) {
                mBinder.tvHeaderTitle.setText(item.post.author.headline)
            } else {
                mBinder.tvHeaderTitle.setText("")
            }

            mBinder.tvDate.setText(CommonMethods.changeCOmmentDateTimeFormat(item.post.publishedDate))

            mBinder.relImage.setOnClickListener(View.OnClickListener {
                interaction?.onQuestionItemSelected(pos, item, mBinder.relImage)
            })

            mBinder.ivShare.setOnClickListener(View.OnClickListener {
                interaction?.onQuestionItemSelected(pos, item, mBinder.ivShare)
            })

            mBinder.linCOntainer.setOnClickListener(View.OnClickListener {
                interaction?.onQuestionItemSelected(pos, item, mBinder.linCOntainer)
            })

            /*mBinder.linComments.setOnClickListener(View.OnClickListener {
                interaction?.onQuestionItemSelected(pos, item, mBinder.linComments)
            })*/
            mBinder.tvCommentsCount.setText(CommonMethods.getStringWithOnePadding("" + item.commentsCount))


            Log.d("count_log", "comments count " + item.commentsCount)
            Log.d("count_log", "comments count " + item.content)
            Log.d("count_log", "text " + mBinder.tvCommentsCount.text)



            if (item.comments.isNullOrEmpty()) {
                mBinder.linANswerCOntainer.visibility = View.GONE
            } else {
                mBinder.linANswerCOntainer.visibility = View.VISIBLE

                var commentObj = item.comments.get(0)

                mBinder.relAnsImage.setOnClickListener(View.OnClickListener {
                    interaction?.onQuestionItemSelected(pos, item, mBinder.relAnsImage)
                })



                mBinder.tvANswerName.setText(commentObj.comment.author.name)
                mBinder.tvAnswerDate.setText(CommonMethods.changeCOmmentDateTimeFormat(commentObj.comment.publishedDateTime))

                Log.d(
                    "anonymous_log",
                    "Author " + commentObj.comment.author.name + " is anonymous " + commentObj.comment.isAnonymous
                )

                if (commentObj.comment.isAnonymous) {
                    mBinder.tvANswerName.setText("Anonymous")
                    mBinder.tvAnswerHeadline.setText("")
                } else {
                    mBinder.tvANswerName.setText(commentObj.comment.author.name)
                    mBinder.tvAnswerHeadline.setText(commentObj.comment.author.headline)
                }

                if (!commentObj.comment.author.dpLink.isNullOrEmpty() && !commentObj.comment.isAnonymous) {
                    Log.d("anonymous_log", "showing image for " + commentObj.comment.author.dpLink)
                    Log.d("anonymous_log", "showing image for " + commentObj.comment.author.name)
                    Glide.with(FourBaseCareApp.activityFromApp)
                        .load(commentObj.comment.author.dpLink)
                        .placeholder(R.drawable.ic_user_image).circleCrop()
                        .into(mBinder.ivAnswerImage)

                } else {
                    Log.d("anonymous_log", "Hidden image for " + commentObj.comment.author.name)
                    Glide.with(FourBaseCareApp.activityFromApp)
                        .load(R.drawable.ic_user_image).circleCrop().into(mBinder.ivAnswerImage)
                }


                if (commentObj.comment.author.role != null && commentObj.comment.author.role.equals(
                        Constants.ROLE_DOCTOR
                    ) && !commentObj.comment.isAnonymous
                ) {
                    mBinder.ivAnsVerified.visibility = View.VISIBLE
                    if (commentObj.comment.author.isVerified) {
                        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified)
                            .into(mBinder.ivAnsVerified)
                    } else {
                        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified)
                            .into(mBinder.ivAnsVerified)
                    }
                } else {
                    mBinder.ivAnsVerified.visibility = View.GONE
                }

                mBinder.tvAnswer.setText(commentObj.comment.content)


                if (commentObj.comment.content.length > 150) {
                    mBinder.tvAnswer.text = Html.fromHtml(
                        commentObj.comment.content.substring(
                            0,
                            150
                        ) + "..." + "<font color='#35b1c4'> <u>View More</u></font>"
                    )
                } else {
                    mBinder.tvAnswer.text = commentObj.comment.content
                }

                if (commentObj.comment.content.length > 150) {
                    mBinder.tvAnswer.setOnClickListener {
                        if (mBinder.tvAnswer.text.toString().endsWith("View More")) {
                            mBinder.tvAnswer.text = commentObj.comment.content
                        } else {
                            if (commentObj.comment.content.length > 150) {
                                mBinder.tvAnswer.text = Html.fromHtml(
                                    commentObj.comment.content.substring(
                                        0,
                                        150
                                    ) + "..." + "<font color='#35b1c4'> <u>View More</u></font>"
                                )
                            } else mBinder.tvAnswer.text = commentObj.comment.content
                        }
                    }
                }

                mBinder.tvLikesCount.setText(CommonMethods.getStringWithOnePadding("" + commentObj.numberOfLikes))

                /*mBinder.tvLikesCount.setOnClickListener(View.OnClickListener {
                    interaction?.onQuestionItemSelected(pos, item, mBinder.tvLikesCount)
                })

                mBinder.ivLikes.setOnClickListener(View.OnClickListener {
                    interaction?.onQuestionItemSelected(pos, item, mBinder.ivLikes)
                })*/

                if (!commentObj.comment.author.headline.isNullOrEmpty() && !commentObj.comment.isAnonymous) {
                    mBinder.tvAnswerHeadline.setText("" + commentObj.comment.author.headline)
                } else {
                    mBinder.tvAnswerHeadline.setText("")
                }

            /*    if(commentObj.comment.author.userId == loggedInUserId){
                    mBinder.ivMenu.visibility = View.VISIBLE
                    mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                        interaction?.onQuestionItemSelected(pos, item, mBinder.ivMenu)
                    })
                }else{
                    mBinder.ivMenu.visibility = View.GONE
                }*/

                mBinder.ivMenu.visibility = View.GONE


            }
        }
    }

    interface Interaction {
        fun onQuestionItemSelected(position: Int, item: TrendingVideoDetails, view: View)
    }
}
