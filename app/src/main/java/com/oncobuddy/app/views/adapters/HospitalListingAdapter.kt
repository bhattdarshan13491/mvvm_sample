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
import com.oncobuddy.app.databinding.RawCancerTypeBinding
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalDetails
import java.util.*
import kotlin.collections.ArrayList


class HospitalListingAdapter(private var  hospitalList: ArrayList<HospitalDetails>,
                             private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var hospitalFilterList = ArrayList<HospitalDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HospitalDetails>() {

        override fun areItemsTheSame(oldItem: HospitalDetails, newItem: HospitalDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HospitalDetails, newItem: HospitalDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawCancerTypeBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_cancer_type, parent, false
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

    fun submitList(list: List<HospitalDetails>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawCancerTypeBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawCancerTypeBinding = binding

        fun bind(item: HospitalDetails, pos : Int) {
            mBinder.tvName.setText(item.name)
            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            /*Glide.with(itemView.context).load(item.productImgUrl).transform(
                CircleCrop()
            ).into(itemView.ivProfilePic)
*/
            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onHospitalSelected(pos, item, mBinder.root)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }
    }

    interface Interaction {
        fun onHospitalSelected(position: Int, item: HospitalDetails, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    hospitalFilterList = hospitalList
                } else {
                    val resultList = ArrayList<HospitalDetails>()
                    for (row in hospitalList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    hospitalFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = hospitalFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                hospitalFilterList = results?.values as ArrayList<HospitalDetails>
                submitList(hospitalFilterList)
            }

        }
    }
}
