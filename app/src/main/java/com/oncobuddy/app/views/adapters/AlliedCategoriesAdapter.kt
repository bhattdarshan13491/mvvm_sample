package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ItemChatBinding
import com.oncobuddy.app.databinding.ItemSpecialistCardBinding
import com.oncobuddy.app.databinding.RawAlliedCategoriesBinding
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.models.pojo.AlliedCategory
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.records_list.Record

/**
 * Allied categories adapter
 * Shows categorised list of in house doctors
 * @property youtubeVideosList
 * @property interaction
 * @constructor Create empty Allied categories adapter
 */

class AlliedCategoriesAdapter(private var youtubeVideosList: ArrayList<AlliedCategory>,
                              private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<AlliedCategory>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AlliedCategory>() {

        override fun areItemsTheSame(oldItem: AlliedCategory, newItem: AlliedCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AlliedCategory, newItem: AlliedCategory): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawAlliedCategoriesBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_allied_categories, parent, false
        )

        return ShoppingItemVH(
            mBinder,
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

    fun submitList(list: List<AlliedCategory>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawAlliedCategoriesBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawAlliedCategoriesBinding = binding

        fun bind(item: AlliedCategory, pos : Int) {
            mBinder.tvViewAll.setOnClickListener(View.OnClickListener {
                interaction?.onCategorySelected(pos, item, mBinder.tvViewAll)
            })
         /*   mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onChatSelected(pos, item, mBinder.root)
            })*/
           /* mBinder.tvTitle.setText(item.title)

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onVideoSelected(pos, item, mBinder.root)
            })

            if(item.videoUrl.split("v=").size > 1) {
                val url = "https://img.youtube.com/vi/" + item.videoUrl.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).into(mBinder.ivThumbnail)

            }*/
            setRecyclerView(mBinder.rvRecords, item.doctorsList, null)
        }

        fun setRecyclerView(recyclerView: RecyclerView, list: ArrayList<YoutubeVideo>, doctorInterAction: AlliedDoctorsAdapter.Interaction?) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                var alliedDoctorsAdapter = AlliedDoctorsAdapter(list, null)
                adapter = alliedDoctorsAdapter
                alliedDoctorsAdapter.submitList(list)
            }
        }
    }




    interface Interaction {
        fun onCategorySelected(position: Int, item: AlliedCategory, view : View)
    }
}
