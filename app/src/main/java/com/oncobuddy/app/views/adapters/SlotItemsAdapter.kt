package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawTimingsBinding
import com.oncobuddy.app.databinding.RawTransactionBinding
import com.oncobuddy.app.models.pojo.doctor_availibility.SlotsItem
import com.oncobuddy.app.models.pojo.patient_transactions.Transaction
import com.oncobuddy.app.utils.CommonMethods


class SlotItemsAdapter(private var youtubeVideosList: ArrayList<SlotsItem>,
                       private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<SlotsItem>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SlotsItem>() {

        override fun areItemsTheSame(oldItem: SlotsItem, newItem: SlotsItem): Boolean {
            return oldItem.slotNumber == newItem.slotNumber
        }

        override fun areContentsTheSame(oldItem: SlotsItem, newItem: SlotsItem): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawTimingsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_timings, parent, false
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

    fun submitList(list: List<SlotsItem>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawTimingsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawTimingsBinding = binding

        fun bind(item: SlotsItem, pos : Int) {
            var position = pos+1
            mBinder.tvSLotNumber.setText("Slot Number "+position)
            mBinder.tvStartTIme.setText(returnFormattedTIme(item.startHour, item.startMinutes))
            mBinder.tvEndTime.setText(returnFormattedTIme(item.endHour, item.endMinutes))

            mBinder.relStart.setOnClickListener(View.OnClickListener {
                interaction?.onSlotItemSelected(pos, item, mBinder.relStart)
            })

            mBinder.relEnd.setOnClickListener(View.OnClickListener {
                interaction?.onSlotItemSelected(pos, item, mBinder.relEnd)
            })

            mBinder.ivOptions.setOnClickListener(View.OnClickListener {
                interaction?.onSlotItemSelected(pos, item, mBinder.ivOptions)
            })

        }

        private fun returnFormattedTIme(selectedHour: Int, selectedMinute: Int): String{
            var formattedTime = ""

            if (selectedHour == 0) {
                formattedTime =
                    CommonMethods.getStringWithOnePadding("" + (selectedHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + selectedMinute
                    ) + " AM"
            } else if (selectedHour > 12)
                formattedTime =
                    CommonMethods.getStringWithOnePadding("" + (selectedHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + selectedMinute
                    ) + " PM"
            else if (selectedHour == 12)
                formattedTime =
                    CommonMethods.getStringWithOnePadding("" + selectedHour) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + selectedMinute
                    ) + " PM"
            else
                formattedTime =
                    CommonMethods.getStringWithOnePadding("" + (selectedHour)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + selectedMinute
                    ) + " AM"



            return  formattedTime
        }
    }

    interface Interaction {
        fun onSlotItemSelected(position: Int, item: SlotsItem, view : View)
    }





}
