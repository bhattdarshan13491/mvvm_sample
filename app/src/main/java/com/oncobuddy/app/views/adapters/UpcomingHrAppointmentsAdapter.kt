package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawHrUpcomingAppointmentsBinding
import com.oncobuddy.app.models.pojo.MedicalRecord
import java.util.*
import kotlin.collections.ArrayList


class UpcomingHrAppointmentsAdapter(private var medicalRecordsList : ArrayList<MedicalRecord>,
                                    private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var productFilterList = ArrayList<MedicalRecord>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MedicalRecord>() {

        override fun areItemsTheSame(oldItem: MedicalRecord, newItem: MedicalRecord): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MedicalRecord, newItem: MedicalRecord): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawHrUpcomingAppointmentsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_hr_upcoming_appointments, parent, false
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

    fun submitList(list: List<MedicalRecord>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawHrUpcomingAppointmentsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawHrUpcomingAppointmentsBinding = binding

        fun bind(item: MedicalRecord, pos : Int) {
           // mBinder.tvReportName.setText(item.name)
            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            /*Glide.with(itemView.context).load(item.productImgUrl).transform(
                CircleCrop()
            ).into(itemView.ivProfilePic)
*/
            mBinder.tvView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.tvView)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: MedicalRecord, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    productFilterList = medicalRecordsList
                } else {
                    val resultList = ArrayList<MedicalRecord>()
                    for (row in medicalRecordsList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    productFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = productFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productFilterList = results?.values as ArrayList<MedicalRecord>
                submitList(productFilterList)
            }

        }
    }
}
