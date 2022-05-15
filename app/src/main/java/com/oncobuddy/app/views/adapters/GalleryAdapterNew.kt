package com.oncobuddy.app.views.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.GvItemBinding


class GalleryAdapterNew(private var  uriList: ArrayList<Uri>,
                        private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){



    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Uri>() {

        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: GvItemBinding

        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.gv_item, parent, false
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

    fun submitList(list: List<Uri>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: GvItemBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: GvItemBinding = binding

        fun bind(item: Uri, pos : Int) {

            Glide.with(FourBaseCareApp.activityFromApp).load(item)
                .placeholder(R.drawable.square_placeholder).into(mBinder.ivGallery)
            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            /*Glide.with(itemView.context).load(item.productImgUrl).transform(
                CircleCrop()
            ).into(itemView.ivProfilePic)
*/
            mBinder.ivDelete.setOnClickListener(View.OnClickListener {
                interaction?.onGalleryPhotoSelected(pos, item, mBinder.ivDelete)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }
    }

    interface Interaction {
        fun onGalleryPhotoSelected(position: Int, item: Uri, view : View)
    }


}
