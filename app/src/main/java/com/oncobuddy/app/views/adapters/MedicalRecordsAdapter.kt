package com.oncobuddy.app.views.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawMedicalRecordsBinding
import com.oncobuddy.app.models.pojo.records_list.Record


import java.util.*
import kotlin.collections.ArrayList


class MedicalRecordsAdapter(private var medicalRecordsList : ArrayList<Record>,
                            private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var recordsFilterList = ArrayList<Record>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Record>() {

        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawMedicalRecordsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_medical_records, parent, false
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

    fun submitList(list: List<Record>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawMedicalRecordsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawMedicalRecordsBinding = binding

        fun bind(item: Record, pos : Int) {
        /*    mBinder.tvReportName.setText(item.recordType)
            mBinder.tvDate.setText(item.recordDate)
            mBinder.tvDoctorName.setText(""+item.title)
            mBinder.tvLabName.setText(item.hospitalName)
*/
            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            /*Glide.with(itemView.context).load(item.productImgUrl).transform(
                CircleCrop()
            ).into(itemView.ivProfilePic)
*/

            mBinder.relMainCOntainer.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.relMainCOntainer)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Record, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    recordsFilterList = medicalRecordsList
                } else {
                    val resultList = ArrayList<Record>()
                    for (row in medicalRecordsList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    recordsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = recordsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                recordsFilterList = results?.values as ArrayList<Record>
                submitList(recordsFilterList)
            }

        }
    }
}
