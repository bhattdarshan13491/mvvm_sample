package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawEcrfEventBinding
import com.oncobuddy.app.models.pojo.ecrf_events.EventsItem
import com.oncobuddy.app.utils.CommonMethods


class EcrdEventsAdapter(private var ecrfEventsList: ArrayList<EventsItem>,
                        private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<EventsItem>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventsItem>() {

        override fun areItemsTheSame(oldItem: EventsItem, newItem: EventsItem): Boolean {
            return oldItem.eventId == newItem.eventId
        }

        override fun areContentsTheSame(oldItem: EventsItem, newItem: EventsItem): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawEcrfEventBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_ecrf_event, parent, false
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

    fun submitList(list: List<EventsItem>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawEcrfEventBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawEcrfEventBinding = binding

        fun bind(item: EventsItem, pos : Int) {
            mBinder.tvName.setText(""+item.eventType)
            mBinder.tvCancerType.setText(""+item.hospitalAndLocation)
            mBinder.tvSubType.setText("Event Date : "+CommonMethods.convertTimeSlotFormat1(item.eventDate))

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onSelected(pos, item, mBinder.root)
            })


        }
    }

    interface Interaction {
        fun onSelected(position: Int, item: EventsItem, view : View)
    }

}
