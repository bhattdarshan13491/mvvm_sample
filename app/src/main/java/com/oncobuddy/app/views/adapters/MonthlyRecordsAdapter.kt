package com.oncobuddy.app.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawHelpVideosBinding
import com.oncobuddy.app.databinding.RawMonthlyRecordsBinding
import com.oncobuddy.app.models.pojo.records_list.MonthyRecords
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MonthlyRecordsAdapter(
    private var recordsInterAction: MedicalRecordsAdapterNew.Interaction,
    private val interaction: Interaction? = null,
    private val edit: Boolean = true,
    private val userId: Int,
    private val source: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MonthyRecords>() {

        override fun areItemsTheSame(oldItem: MonthyRecords, newItem: MonthyRecords): Boolean {
            return oldItem.monthName == newItem.monthName
        }

        override fun areContentsTheSame(oldItem: MonthyRecords, newItem: MonthyRecords): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawMonthlyRecordsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_monthly_records, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,recordsInterAction,
            userId, source
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

    fun submitList(list: List<MonthyRecords>) {
        differ.submitList(list)
    }


    class ShoppingItemVH constructor(
        binding: RawMonthlyRecordsBinding,
        private val interaction: Interaction?,
        private val recordsInterAction: MedicalRecordsAdapterNew.Interaction,
        private val userId: Int,
        private val source: String
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawMonthlyRecordsBinding = binding

        fun bind(item: MonthyRecords, pos: Int) {
            Log.d("month_adap_log","Came here")
            mBinder.tvMonth.setText(item.monthName)

            mBinder.linMonthYear.setOnClickListener(View.OnClickListener {
                //interaction?.onMonthSelected(pos, item, mBinder.root)
                if(item.sHowingDetails){
                    mBinder.rvRecords.visibility = View.GONE
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
                    mBinder.rvRecords.visibility = View.VISIBLE
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
                item.sHowingDetails = !item.sHowingDetails
            })
            Log.d("item_click", "1")

            if(!item.recordArrayList.isNullOrEmpty())
            setRecyclerView(mBinder.rvRecords, item.recordArrayList, recordsInterAction)
        }



        fun setRecyclerView(recyclerView: RecyclerView, list: ArrayList<Record>, recordsInterAction: MedicalRecordsAdapterNew.Interaction) {
            Log.d("month_adap_log","Recycklerview set with "+list.size)
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                var medicalRecordsAdapter =
                    MedicalRecordsAdapterNew(
                        list,
                        recordsInterAction,
                        true,
                        userId,
                        source
                    )
                adapter = medicalRecordsAdapter
                list.sortByDescending { getDateForSorting(it.recordDate) }
                medicalRecordsAdapter.submitList(list)
            }
        }

        fun getDateForSorting(date: String): Date? {
            var dtStart = "2010-10-15T09:27:37Z"
            //dtStart = date.substring(0, date.length - 9)
            dtStart = date

            val format = SimpleDateFormat("yyyy-mm-dd")
            try {
                val date: Date = format.parse(dtStart)
                return date
            } catch (e: ParseException) {
                e.printStackTrace()
                return null
            }
        }
    }

    interface Interaction {
        fun onMonthSelected(position: Int, item: MonthyRecords, view: View)
    }
}
