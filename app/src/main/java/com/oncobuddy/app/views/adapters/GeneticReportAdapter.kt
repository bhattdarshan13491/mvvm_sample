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
import com.oncobuddy.app.databinding.RawGeneticReportBinding
import com.oncobuddy.app.databinding.RawTransactionBinding
import com.oncobuddy.app.models.pojo.genetic_report.TestStatusesItem
import com.oncobuddy.app.models.pojo.patient_transactions.Transaction


class GeneticReportAdapter(private var geneticReportList: ArrayList<TestStatusesItem>,
                           private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var youtubeFilterList = ArrayList<TestStatusesItem>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TestStatusesItem>() {

        override fun areItemsTheSame(oldItem: TestStatusesItem, newItem: TestStatusesItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: TestStatusesItem, newItem: TestStatusesItem): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawGeneticReportBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_genetic_report, parent, false
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

    fun submitList(list: List<TestStatusesItem>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawGeneticReportBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawGeneticReportBinding = binding

        fun bind(item: TestStatusesItem, pos : Int) {
            mBinder.tvTitle.setText(item.name)
            mBinder.tvStatus.setText(""+item.status)
            mBinder.tvDescription.setText(item.lastComment)
        }
    }

    interface Interaction {
        fun onGeneticStageSelected(position: Int, item: Transaction, view : View)
    }



}
