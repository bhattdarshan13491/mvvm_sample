package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawNovMyPatientsBinding
import com.oncobuddy.app.models.pojo.YoutubeVideo
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails


class NovMyPatientsAdapter(private var youtubeVideosList: ArrayList<PatientDetails>,
                           private val interaction: ShoppingItemVH.Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var youtubeFilterList = ArrayList<PatientDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PatientDetails>() {

        override fun areItemsTheSame(oldItem: PatientDetails, newItem: PatientDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PatientDetails, newItem: PatientDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawNovMyPatientsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_nov_my_patients, parent, false
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

    fun submitList(list: List<PatientDetails>) {
        differ.submitList(list)
    }


    class ShoppingItemVH constructor(
        binding: RawNovMyPatientsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawNovMyPatientsBinding = binding

        fun bind(item: PatientDetails, pos: Int) {
            /*    mBinder.tvTitle.setText(item.title)

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onVideoSelected(pos, item, mBinder.root)
            })

            if(item.videoUrl.split("v=").size > 1) {
                val url = "https://img.youtube.com/vi/" + item.videoUrl.split("v=").get(1).toString() + "/sddefault.jpg"
                Glide.with(itemView.context).load(url).into(mBinder.ivThumbnail)

            }
        }*/
        }

        interface Interaction {
            fun onPatientSelected(position: Int, item: YoutubeVideo, view: View)
        }
    }
}

   /* override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    youtubeFilterList = youtubeVideosList
                } else {
                    val resultList = ArrayList<YoutubeVideo>()
                    for (row in youtubeVideosList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    youtubeFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = youtubeFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                youtubeFilterList = results?.values as ArrayList<YoutubeVideo>
                submitList(youtubeFilterList)
            }

        }
    }*/

