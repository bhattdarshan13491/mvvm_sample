package com.oncobuddy.app.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawMedicalRecordsBinding
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants


import java.util.*
import kotlin.collections.ArrayList


class MedicalRecordsAdapterNew(private var medicalRecordsList : ArrayList<Record>,
                               private val interaction: Interaction? = null,
                               private val edit: Boolean = true,
                               private val userId : Int,
                               private val source : String) :
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

        //Log.d("records_log ","Editable "+edit)
        return ShoppingItemVH(
            mBinder,
            interaction,
            edit,
            userId,
            source,this@MedicalRecordsAdapterNew
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
        private val interaction: Interaction?,
        private val shouldEdit: Boolean,
        private val userId: Int,
        private val source: String,
        private val medicalRecordsAdapterNew: MedicalRecordsAdapterNew
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawMedicalRecordsBinding = binding

        fun bind(item: Record, pos : Int) {
            Log.d("month_adap_log","Came medical recorder here")
            if(item.categories != null && item.categories.size > 0){
                var str = ""
                for(category in item.categories){
                    str += category+","
                }
                mBinder.tvName.setText(str.substring(0,str.length - 1))

            }else{
                mBinder.tvName.setText(item.title)
            }

            if(item.uploadedUserName != null){
                mBinder.tvUploadedBy.visibility = View.VISIBLE
                mBinder.tvUploadedBy.setText("Uploaded by "+item.uploadedUserName)
            }else{
                mBinder.tvUploadedBy.visibility = View.GONE
            }

            if(item.selected){
                mBinder.ivDoctorImage.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_file_select))
                mBinder.relMainCOntainer.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.lightest_bg))
            }else{
                mBinder.ivDoctorImage.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_records_icon))
                mBinder.relMainCOntainer.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.white))
            }

           // mBinder.tvName.setText(""+item.title)

            if(!item.tags.isNullOrEmpty()){
                mBinder.tvNoTag.visibility = View.GONE
                mBinder.rvTags.visibility = View.VISIBLE
                mBinder.rvTags.apply {
                    layoutManager = LinearLayoutManager(context)
                    (layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
                    val tagListAdapter = TagListingAdapter()
                    adapter = tagListAdapter
                    tagListAdapter.submitList(item.tags.distinct())
                }
           }else{
                mBinder.tvNoTag.visibility = View.VISIBLE
                mBinder.rvTags.visibility = View.GONE
            }
            mBinder.tvReportDate.setText(CommonMethods.utcTOLocalDate(item.recordDate))
            //Log.d("records_log ","Editable "+shouldEdit)
            if(shouldEdit){
                mBinder.linUpdateContainer.visibility = View.VISIBLE
            }else{
                mBinder.linUpdateContainer.visibility = View.INVISIBLE
            }

            mBinder.relMainCOntainer.setOnClickListener(View.OnClickListener {
                Log.d("item_click", "2")
                interaction?.onItemSelected(pos, item, mBinder.relMainCOntainer, medicalRecordsAdapterNew)
            })

            mBinder.relMainCOntainer.setOnLongClickListener{
                interaction?.onItemLongPress(pos, item, mBinder.relMainCOntainer, medicalRecordsAdapterNew)
                return@setOnLongClickListener true
            }

            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivMenu, medicalRecordsAdapterNew)
            })

            mBinder.ivDoctorImage.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivDoctorImage, medicalRecordsAdapterNew)
            })

            mBinder.ivDoctorImage.setOnLongClickListener{
                interaction?.onItemLongPress(pos, item, mBinder.ivDoctorImage, medicalRecordsAdapterNew)
                return@setOnLongClickListener true
            }

            mBinder.linUpdateContainer.visibility = View.GONE

            mBinder.ivDropDown.setOnClickListener(View.OnClickListener {
                //interaction?.onMonthSelected(pos, item, mBinder.root)
                if(!item.isShowingDetails){
                    mBinder.linUpdateContainer.visibility = View.VISIBLE
                    val rotate = RotateAnimation(
                        0F,
                        180F,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate.duration = 300
                    rotate.interpolator = LinearInterpolator()
                    rotate.fillAfter = true
                    mBinder.ivDropDown.startAnimation(rotate)
                }else{
                    mBinder.linUpdateContainer.visibility = View.GONE
                    val rotate = RotateAnimation(
                        180F,
                        0F,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                    )
                    rotate.duration = 300
                    rotate.interpolator = LinearInterpolator()
                    rotate.fillAfter = true
                    mBinder.ivDropDown.startAnimation(rotate)
                }
                item.isShowingDetails = !item.isShowingDetails
            })


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Record, view : View, medicalRecordsAdapterNew: MedicalRecordsAdapterNew)
        fun onItemLongPress(position: Int, item: Record, view : View, medicalRecordsAdapterNew: MedicalRecordsAdapterNew)
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
