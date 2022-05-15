package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.pojo.Notification
import com.oncobuddy.app.models.pojo.NovChat
import com.oncobuddy.app.models.pojo.YoutubeVideo


class NotificationsAdapter(private var chatList: ArrayList<Notification>,
                           private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val TYPE_HEADER = 1
    private val TYPE_NOTIFICATION = 0

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun getItemViewType(position: Int): Int {
        return if (chatList.get(position).header)
            TYPE_HEADER
        else
            TYPE_NOTIFICATION
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val mBinder: RawHeaderBinding
                mBinder = DataBindingUtil.inflate(
                    LayoutInflater
                        .from(parent.context), R.layout.raw_header, parent, false
                )

                return HeaderItemVH(
                    mBinder,
                    interaction
                )
            }
            else -> {
                val mBinder: RawNotificationBinding
                mBinder = DataBindingUtil.inflate(
                    LayoutInflater
                        .from(parent.context), R.layout.raw_notification, parent, false
                )

                return NotificationItemVH(
                    mBinder,
                    interaction
                )
            }

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderItemVH -> {
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Notification>) {
        differ.submitList(list)
    }



    class HeaderItemVH constructor(
        binding: RawHeaderBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawHeaderBinding = binding

        fun bind(item: Notification, pos : Int) {
            //mBinder.tvMessage.setText(item.content)

        }
    }

    class NotificationItemVH constructor(
        binding: RawNotificationBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawNotificationBinding = binding

        fun bind(item: NovChat, pos : Int) {
            //mBinder.tvMessage.setText(item.content)


        }
    }

    interface Interaction {
        fun onNotificationSelected(position: Int, item: Notification, view : View)
    }


}
