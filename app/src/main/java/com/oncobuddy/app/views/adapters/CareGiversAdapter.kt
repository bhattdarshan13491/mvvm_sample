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
import com.oncobuddy.app.databinding.RawCareGiverBinding
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverDetails
import com.oncobuddy.app.utils.CommonMethods


class CareGiversAdapter(private var youtubeVideosList: ArrayList<CareGiverDetails>,
                        private val interaction: Interaction? = null, private val shouldShowMenu: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<CareGiverDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CareGiverDetails>() {

        override fun areItemsTheSame(oldItem: CareGiverDetails, newItem: CareGiverDetails): Boolean {
            return oldItem.appUserId == newItem.appUserId
        }

        override fun areContentsTheSame(oldItem: CareGiverDetails, newItem: CareGiverDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawCareGiverBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_care_giver, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction, shouldShowMenu
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

    fun submitList(list: List<CareGiverDetails>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawCareGiverBinding,
        private val interaction: Interaction?,
        private val shouldShowMenu: Boolean,
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawCareGiverBinding = binding
        fun bind(item: CareGiverDetails, pos : Int) {

            if(shouldShowMenu){
               mBinder.ivCardMenu.visibility = View.VISIBLE
               mBinder.linAdd.visibility = View.GONE
                mBinder.ivCardMenu.setOnClickListener(View.OnClickListener {
                    Log.d("interaction_lg","0")
                    interaction?.onCGSelected(pos, item, mBinder.ivCardMenu)
                })
            }else{
                mBinder.linAdd.visibility = View.VISIBLE
                mBinder.ivCardMenu.visibility = View.GONE
                mBinder.linAdd.setOnClickListener(View.OnClickListener {
                    Log.d("interaction_lg","0")
                    interaction?.onCGSelected(pos, item, mBinder.linAdd)
                })
            }

            mBinder.tvName.setText(item.name)
            mBinder.tvPhoneNumber.setText(item.phoneNumber)
            mBinder.tvRelation.setText(CommonMethods.returnRelationSHipName(item.relationship))
            Log.d("item_sel_log","0 "+item.isSelected)
            if(item.isSelected){
                mBinder.tvAdd.text = "Added"
                mBinder.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.green_color_button))
                mBinder.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_cicrular_green_tick))
            }
            else {
                mBinder.tvAdd.text = "Add"
                mBinder.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.font_light_blue))
                mBinder.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_add_box_blue))
            }



        }
    }

    interface Interaction {
        fun onCGSelected(position: Int, item: CareGiverDetails, view : View)
    }

}
