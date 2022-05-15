package com.oncobuddy.app.views.adapters

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
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.databinding.RawProgressBinding
import com.oncobuddy.app.models.pojo.ProfileStatus
import com.oncobuddy.app.models.pojo.YoutubeVideo


class ProgressTrackerAdapter(private var profileSTatusList: ArrayList<ProfileStatus>,private var profileCOmpletionStage: Int = 0,
                             private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<ProfileStatus>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileStatus>() {

        override fun areItemsTheSame(oldItem: ProfileStatus, newItem: ProfileStatus): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProfileStatus, newItem: ProfileStatus): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: com.oncobuddy.app.databinding.RawProgressBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_progress, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            profileCOmpletionStage,
            interaction
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

    fun submitList(list: List<ProfileStatus>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawProgressBinding,
        private val profileCOmpletionStage: Int = 0,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawProgressBinding = binding

        fun bind(item: ProfileStatus, pos : Int) {
        if(pos == 5){
            mBinder.ivLine.visibility = View.GONE
        }
            if(item.isPending){
               Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_gray_line).into(mBinder.ivLine)
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_grey_dot).into(mBinder.ivDot)
                Log.d("profile_completion_level", "gray line "+item.id)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_green_line).into(mBinder.ivLine)
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_green_dot).into(mBinder.ivDot)
                Log.d("profile_completion_level", "green line "+item.id)
            }

        /*mBinder.tvTitle.setText(item.title)



            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onVideoSelected(pos, item, mBinder.root)
            })

            if(item.videoUrl.split("v=").size > 1) {
                val url = "https://img.youtube.com/vi/" + item.videoUrl.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).into(mBinder.ivThumbnail)

            }
*/        }
    }

    interface Interaction {
        fun onVideoSelected(position: Int, item: YoutubeVideo, view : View)
    }

   /* override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    youtubeFilterList = youtubeVideosList
                } else {
                    val resultList = ArrayList<YoutubeVideo>()
                    for (row in youtubeVideosList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    youtubeFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = youtubeFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                youtubeFilterList = results?.values as ArrayList<YoutubeVideo>
                submitList(youtubeFilterList)
            }

        }
    }*/
}
