package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawFilterTagBinding
import com.oncobuddy.app.models.pojo.FilterTag

class FilterTagListingAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface Interaction {
        fun onFilterTagSelected(position: Int, item: FilterTag, view : View)
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FilterTag>() {

        override fun areItemsTheSame(oldItem: FilterTag, newItem: FilterTag): Boolean {
            return oldItem.tagName == newItem.tagName
        }

        override fun areContentsTheSame(oldItem: FilterTag, newItem: FilterTag): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawFilterTagBinding

        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_filter_tag, parent, false
        )

        return ShoppingItemVH(mBinder,interaction)
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

    fun submitList(list: List<FilterTag>) {
        differ.submitList(list)
    }





    class ShoppingItemVH constructor(
        binding: RawFilterTagBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {


        var mBinder: RawFilterTagBinding = binding

        fun bind(item: FilterTag, pos : Int) {

            if(item.isSelected){
                mBinder.linRoot.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.badge_background_red)
                mBinder.tvTag.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.font_light_black))
            }else{
                mBinder.linRoot.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp,R.drawable.badge_gray_bg)
                mBinder.tvTag.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.font_light_black))
            }

            mBinder.tvTag.setText(item.tagName+" ("+item.numbers+")")
            //mBinder.tvNumbers.setText("("+item.numbers+")");
            mBinder.linRoot.setOnClickListener(View.OnClickListener {
                interaction?.onFilterTagSelected(pos, item, mBinder.root)
            })
        }
    }

}
