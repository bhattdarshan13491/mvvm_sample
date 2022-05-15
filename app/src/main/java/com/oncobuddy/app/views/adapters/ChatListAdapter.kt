package com.oncobuddy.app.views.adapters

import android.text.format.DateUtils
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
import com.oncobuddy.app.databinding.ItemChatBinding
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.chats.chat_groups.GroupDetails
import com.oncobuddy.app.utils.CommonMethods
import java.util.concurrent.TimeUnit


class ChatListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<GroupDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GroupDetails>() {

        override fun areItemsTheSame(oldItem: GroupDetails, newItem: GroupDetails): Boolean {
            return oldItem.chatGroupId == newItem.chatGroupId
        }

        override fun areContentsTheSame(oldItem: GroupDetails, newItem: GroupDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: ItemChatBinding = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.item_chat, parent, false
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

    fun submitList(list: List<GroupDetails>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: ItemChatBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: ItemChatBinding = binding

        fun bind(item: GroupDetails, pos : Int) {
            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onChatSelected(pos, item, mBinder.root)
            })

            mBinder.tvProfileName.setText(item.userDisplayName)
           // Log.d("time_ago_",""+CommonMethods.getTimeAgo(TimeUnit.SECONDS.toMillis(item.lastMessageAgo.toLong())))
            Log.d("time_ago",""+CommonMethods.getTimeAgo(item.lastMessageAgo.toLong()))
            mBinder.tvLastMessageAgo.setText(CommonMethods.getTimeAgo(item.lastMessageAgo.toLong()))




            if(item.lastMessage.isNullOrEmpty()){
                mBinder.tvLastMessage.setText("No new message")
            }else{
                mBinder.tvLastMessage.setText(""+item.lastMessage)
            }

            if(item.numberOfUnreadMessages == 0){
                mBinder.relNewMessages.visibility = View.GONE
            }else{
                mBinder.relNewMessages.visibility = View.VISIBLE
                mBinder.tvMessageCount.setText(item.numberOfUnreadMessages)

            }

            if(!item.userDisplayPic.isNullOrEmpty()){
                Glide.with(itemView.context).load(item.userDisplayPic).placeholder(R.drawable.ic_user_image).transform(CircleCrop()).into(mBinder.ivProfileImage)
            }else{
                Glide.with(itemView.context).load(R.drawable.ic_user_image).placeholder(R.drawable.ic_user_image).transform(
                    CircleCrop()
                ).into(mBinder.ivProfileImage)
            }
        }


    }

    interface Interaction {
        fun onChatSelected(position: Int, item: GroupDetails, view : View)
    }


}
