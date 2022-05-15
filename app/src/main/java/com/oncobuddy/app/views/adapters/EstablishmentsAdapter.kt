package com.oncobuddy.app.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.*
import com.oncobuddy.app.models.pojo.doctor_locations.Establishment


class EstablishmentsAdapter(private var establishMentList: ArrayList<Establishment>,
                            private val interaction: Interaction? = null) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<Establishment>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Establishment>() {

        override fun areItemsTheSame(oldItem: Establishment, newItem: Establishment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Establishment, newItem: Establishment): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawEstablishmentBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_establishment, parent, false
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

    fun submitList(list: List<Establishment>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawEstablishmentBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawEstablishmentBinding = binding

        fun bind(item: Establishment, pos : Int) {

            Log.d("establishment_adap","name "+item.establishmentName)
            mBinder.tvClinicName.text = item.establishmentName
            if(item.address != null)
            mBinder.tvAddress.text = item.address.address1+", "+item.address.address2+"\n"+item.address.state+", "+item.address.city+", "+item.address.postalCode
            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onEstablishmentSelected(pos, item, mBinder.ivMenu)
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

        }


    }




    interface Interaction {
        fun onEstablishmentSelected(position: Int, item: Establishment, view : View)
    }
}
