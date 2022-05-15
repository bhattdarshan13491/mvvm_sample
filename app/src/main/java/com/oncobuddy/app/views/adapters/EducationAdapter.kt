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
import com.oncobuddy.app.databinding.ItemFeedBinding
import com.oncobuddy.app.databinding.RawEducationBinding
import com.oncobuddy.app.models.pojo.education_degrees.Education


class EducationAdapter(private var educationList: ArrayList<Education>,
                       private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Education>() {

        override fun areItemsTheSame(oldItem: Education, newItem: Education): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Education, newItem: Education): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawEducationBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_education, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShoppingItemVH -> {
                holder.bind(differ.currentList.get(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Education>) {
        differ.submitList(list)
    }


    class ShoppingItemVH constructor(
        binding: RawEducationBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawEducationBinding = binding

        fun bind(item: Education, pos: Int) {
            mBinder.tvDegree.text = item.degree
            mBinder.tvBranch.text = item.branch
            mBinder.ivCardMenu.setOnClickListener(View.OnClickListener {
                interaction?.onDegreeSelected(pos, item, mBinder.ivCardMenu)
            })


        }


    }

    interface Interaction {
        fun onDegreeSelected(position: Int, item: Education, view: View)
    }
}



