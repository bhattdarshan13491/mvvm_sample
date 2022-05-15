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
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawPatientBinding
import com.oncobuddy.app.models.pojo.patient_list.PatientDetails
import com.oncobuddy.app.utils.Constants
import java.util.*
import kotlin.collections.ArrayList


class PatientAdapter(private var  patientDetailsList: ArrayList<PatientDetails>,
                     private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var patientFilterList = ArrayList<PatientDetails>()

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

        val mBinder: RawPatientBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_patient, parent, false
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

    fun submitList(list: List<PatientDetails>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawPatientBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawPatientBinding = binding

        fun bind(item: PatientDetails, pos : Int) {
            mBinder.tvName.setText(item.firstName)
            mBinder.tvPhoneNumber.setText(item.phoneNumber)
            mBinder.tvCancerType.setText("")
            //if(item.cancerType != null)mBinder.tvCancerType.setText(item.cancerType.name) else mBinder.tvCancerType.setText("")

            //mBinder.tvPrice.setText("Price $"+item.productPrice)

            if(!item.dpLink.isNullOrEmpty())
                Glide.with(FourBaseCareApp.activityFromApp).load(item.dpLink).placeholder(R.drawable.ic_user_image).circleCrop().into(mBinder.ivImage)
            else
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_user_image).circleCrop().into(mBinder.ivImage)


            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onPatientSelected(pos, item, mBinder.root)
            })

            if(Constants.IS_DOCTOR_LOGGED_IN && item.isRequestPending != null && item.isRequestPending){
                mBinder.ivSeperator.visibility = View.VISIBLE
                mBinder.tvResponsePending.visibility = View.GONE
                if(item.requestedUserId != null && item.requestedUserId == item.id){
                    mBinder.relConnectionCOntainer.visibility = View.VISIBLE
                    mBinder.ivYes.setOnClickListener(View.OnClickListener {
                        interaction?.onPatientSelected(pos, item, mBinder.ivYes)
                    })
                    mBinder.ivNo.setOnClickListener(View.OnClickListener {
                        interaction?.onPatientSelected(pos, item, mBinder.ivNo)
                    })
                }else{
                        mBinder.relConnectionCOntainer.visibility = View.GONE
                        mBinder.tvResponsePending.visibility = View.VISIBLE
                }


            }else{
                mBinder.ivSeperator.visibility = View.GONE
                mBinder.relConnectionCOntainer.visibility = View.GONE
                mBinder.tvResponsePending.visibility = View.GONE
            }
        }
    }

    interface Interaction {
        fun onPatientSelected(position: Int, item: PatientDetails, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    patientFilterList = patientDetailsList
                } else {
                    val resultList = ArrayList<PatientDetails>()
                    for (row in patientDetailsList) {
                        if (row.firstName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    patientFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = patientFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                patientFilterList = results?.values as ArrayList<PatientDetails>
                submitList(patientFilterList)
            }

        }
    }
}
