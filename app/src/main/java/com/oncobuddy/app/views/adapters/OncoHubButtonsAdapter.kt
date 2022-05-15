package com.oncobuddy.app.views.adapters

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
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.databinding.RawOncoTabBinding
import com.oncobuddy.app.models.pojo.OncoHub
import com.oncobuddy.app.models.pojo.YoutubeVideo


class OncoHubButtonsAdapter(private var youtubeVideosList: ArrayList<OncoHub>,
                            private val interaction: Interaction? = null,private  val selectedPos: Int = 0) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<OncoHub>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OncoHub>() {

        override fun areItemsTheSame(oldItem: OncoHub, newItem: OncoHub): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OncoHub, newItem: OncoHub): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawOncoTabBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_onco_tab, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,selectedPos
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

    fun submitList(list: List<OncoHub>) {
        differ.submitList(list)
    }

    class ShoppingItemVH constructor(
        binding: RawOncoTabBinding,
        private val interaction: Interaction?,
        private val selectedPos: Int
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawOncoTabBinding = binding

        fun bind(item: OncoHub, pos : Int) {
            mBinder.tvTitle.setText(item.name)
            mBinder.ivIcon.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, item.icon))

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onTabSelected(pos, item, mBinder.root)
            })

            if(pos == selectedPos){
                mBinder.linCOntainer.background =  ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.button_background)
                mBinder.tvTitle.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.white))
                mBinder.ivIcon.setColorFilter(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            }else{
                mBinder.linCOntainer.background =  ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.blue_border_white_bg)
                mBinder.tvTitle.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.font_light_blue))
                mBinder.ivIcon.setColorFilter(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.font_light_blue), android.graphics.PorterDuff.Mode.SRC_IN)
            }
        }
    }

    interface Interaction {
        fun onTabSelected(position: Int, item: OncoHub, view : View)
    }


}
