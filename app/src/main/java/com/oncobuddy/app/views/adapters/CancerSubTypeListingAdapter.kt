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
import com.oncobuddy.app.models.pojo.profile.CancerType
import java.util.*
import kotlin.collections.ArrayList


class CancerSubTypeListingAdapter(private var  cancerTypeList: ArrayList<CancerType>,
                                  private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var CancerTypeFilterList = ArrayList<CancerType>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CancerType>() {

        override fun areItemsTheSame(oldItem: CancerType, newItem: CancerType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CancerType, newItem: CancerType): Boolean {
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

    fun submitList(list: List<CancerType>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawCancerTypeBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawCancerTypeBinding = binding

        fun bind(item: CancerType, pos : Int) {
            mBinder.tvName.setText(item.name)
            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            /*Glide.with(itemView.context).load(item.productImgUrl).transform(
                CircleCrop()
            ).into(itemView.ivProfilePic)
*/
            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onCancerSubTypeSelected(pos, item, mBinder.root)
            })

            /*mBinder.ivView.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivView)
            })*/
        }
    }

    interface Interaction {
        fun onCancerSubTypeSelected(position: Int, item: CancerType, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    CancerTypeFilterList = cancerTypeList
                } else {
                    val resultList = ArrayList<CancerType>()
                    for (row in cancerTypeList) {
                        if (row.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    CancerTypeFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = CancerTypeFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                CancerTypeFilterList = results?.values as ArrayList<CancerType>
                submitList(CancerTypeFilterList)
            }

        }
    }
}
