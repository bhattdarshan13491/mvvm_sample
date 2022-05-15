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
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RowLeftBinding
import com.oncobuddy.app.databinding.RowRightBinding
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatMessage


class ChatAdapter(private var chatList: ArrayList<ChatMessage>,
                  private val interaction: Interaction? = null, private val loginId: Int, private val leftProfilePic:String, private val rightProfilePic: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val CHAT_RIGHT = 1
    private val CHAT_LEFT = 0

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatMessage>() {

        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override fun getItemViewType(position: Int): Int {
        return if (chatList.get(position).senderAppUserId == loginId)
            CHAT_RIGHT
        else
            CHAT_LEFT
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHAT_LEFT -> {
                val mBinder: RowLeftBinding
                mBinder = DataBindingUtil.inflate(
                    LayoutInflater
                        .from(parent.context), R.layout.row_left, parent, false
                )

                return LeftItemVH(
                    mBinder,
                    interaction,
                    rightProfilePic
                )
            }
            else -> {
                val mBinder: RowRightBinding
                mBinder = DataBindingUtil.inflate(
                    LayoutInflater
                        .from(parent.context), R.layout.row_right, parent, false
                )

                return RightItemVH(
                    mBinder,
                    interaction,
                    leftProfilePic
                )
            }

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LeftItemVH -> {
                holder.bind(differ.currentList.get(position),position)
            }
            is RightItemVH ->{
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<ChatMessage>) {
        differ.submitList(list)
    }



    class LeftItemVH constructor(
        binding: RowLeftBinding,
        private val interaction: Interaction?,
        private val senderProfilePic: String
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RowLeftBinding = binding

        fun bind(item: ChatMessage, pos : Int) {
            Log.d("chat_aadapter","Left "+item.message)
            Log.d("chat_aadapter","Left "+senderProfilePic)
            mBinder.tvMessage.setText(item.message)
            if(!senderProfilePic.isNullOrEmpty()){
                Glide.with(FourBaseCareApp.activityFromApp).load(senderProfilePic).placeholder(R.drawable.ic_user_image).transform(
                    CircleCrop()
                ).into(mBinder.ivProfile)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_user_image).placeholder(R.drawable.ic_user_image).transform(
                    CircleCrop()
                ).into(mBinder.ivProfile)
            }
        }
    }

    class RightItemVH constructor(
        binding: RowRightBinding,
        private val interaction: Interaction?,
        private val rightProfilePic: String
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RowRightBinding = binding

        fun bind(item: ChatMessage, pos : Int) {
            Log.d("chat_aadapter",item.message)
            Log.d("chat_aadapter",rightProfilePic)
            mBinder.tvReceiverMessage.setText(item.message)
            if(!rightProfilePic.isNullOrEmpty()){
                Glide.with(FourBaseCareApp.activityFromApp).load(rightProfilePic).placeholder(R.drawable.ic_user_image).transform(
                    CircleCrop()
                ).into(mBinder.ivReceiverProfile)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_user_image).placeholder(R.drawable.ic_user_image).transform(
                    CircleCrop()
                ).into(mBinder.ivReceiverProfile)
            }
        }
    }

    interface Interaction {
        fun onChatMsgSelected(position: Int, item: YoutubeVideo, view : View)
    }


}
