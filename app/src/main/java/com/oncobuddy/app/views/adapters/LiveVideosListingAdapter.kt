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
import com.oncobuddy.app.databinding.RawLiveVideosBinding
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.utils.CommonMethods


class LiveVideosListingAdapter(private var  trendingVideoDetailsList: ArrayList<TrendingVideoDetails>,
                               private val interaction: Interaction? = null, private val shouldCutWidth : Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TrendingVideoDetails>() {

        override fun areItemsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            var isItemSame = oldItem.post.id == newItem.post.id
            Log.d("pagination_log", "olditem id : "+oldItem.post.id)
            Log.d("pagination_log", "newitem id : "+newItem.post.id)
            Log.d("pagination_log", "Item same : "+isItemSame)
            return isItemSame
        }

        override fun areContentsTheSame(oldItem: TrendingVideoDetails, newItem: TrendingVideoDetails): Boolean {
            var isContentSame = oldItem.post.id == newItem.post.id
            Log.d("pagination_log", "contents same : "+isContentSame)
            return isContentSame
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawLiveVideosBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_live_videos, parent, false
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
        Log.d("pagination_log", "list size count "+differ.currentList.size)
        return differ.currentList.size
    }

    fun submitList(list: List<TrendingVideoDetails>) {
        Log.d("pagination_log", "old size "+differ.currentList.size)
        Log.d("pagination_log", "new size  "+list.size)
        differ.submitList(list)
    }

    fun addList(list: List<TrendingVideoDetails>){
        differ.currentList.addAll(list)
    }

    class ShoppingItemVH constructor(
        binding: RawLiveVideosBinding,
        private val interaction: Interaction?,
        private val shouldCutWidth: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawLiveVideosBinding = binding

        fun bind(item: TrendingVideoDetails, pos : Int) {

            /*if(shouldCutWidth){
                val lp = mBinder.linCOntent.layoutParams
                lp.width = CommonMethods.calculateWidth() - FourBaseCareApp.activityFromApp.resources.getDimension(R.dimen._80sdp).toInt()
                mBinder.linCOntent.layoutParams = lp
            }*/

            mBinder.tvTitle.setText(item.post.title)
            mBinder.tvDateTime.setText(CommonMethods.convertTimeSlotFormat1(item.post.publishedDate))

            if(item.videoDesc.isNullOrEmpty()){
                mBinder.tvDescription.setText("")
            }else{
                mBinder.tvDescription.setText(item.videoDesc)
            }

            mBinder.tvLikesCOunt.setText(CommonMethods.getStringWithOnePadding(""+item.postLikes))

            if(item.content.split("v=").size>1){
                val url = "https://img.youtube.com/vi/" + item.content.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).placeholder(ContextCompat.getDrawable(itemView.context, R.drawable.placeholder)).into(mBinder.ivDoctorImage)

            }



            mBinder.linCOntent.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.root)
            })

            mBinder.linLike.setOnClickListener(View.OnClickListener {
                interaction?.onLiveVideoSelected(pos, item, mBinder.linLike)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }

    }

    interface Interaction {
        fun onLiveVideoSelected(position: Int, item: TrendingVideoDetails, view : View)
    }


}
