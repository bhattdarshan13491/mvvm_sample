package com.oncobuddy.app.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawDoctorForAppointmentBinding
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import java.util.*
import kotlin.collections.ArrayList


class DoctorListingForAppointmentAdapter(private var doctorList : ArrayList<Doctor>,
                                         private val interaction: Interaction? = null,
                                         private val isCCLogin: Boolean = false,
                                         private val shouldOpenProfile: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var doctorFilterList = ArrayList<Doctor>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Doctor>() {

        override fun areItemsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem.doctorId == newItem.doctorId
        }

        override fun areContentsTheSame(oldItem: Doctor, newItem: Doctor): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawDoctorForAppointmentBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_doctor_for_appointment, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,
            isCCLogin,
            shouldOpenProfile
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

    fun submitList(list: List<Doctor>) {
        differ.submitList(list)
    }



    class ShoppingItemVH constructor(
        binding: RawDoctorForAppointmentBinding,
        private val interaction: Interaction?,
        private val isCCLogin: Boolean,
        private val shouldOpenProfile: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawDoctorForAppointmentBinding = binding

        fun bind(item: Doctor, pos : Int) {
            mBinder.tvName.setText(item.firstName)
            mBinder.tvFees.setText("Rs "+item.consultationFee)
            if(isCCLogin){
                mBinder.tvHospitalName.setText(item.getPhoneNumber())
            }else{
                if(!item.specialization.isNullOrEmpty())
                mBinder.tvHospitalName.setText(item.specialization)
                else if(!item.designation.isNullOrEmpty())
                mBinder.tvHospitalName.setText(item.designation)
                else
                mBinder.tvHospitalName.setText("")
            }

            if(item.verified){
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified).into(mBinder.ivVerified)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified).into(mBinder.ivVerified)
            }

            if(isCCLogin && !item.verified){
                mBinder.tvVerify.visibility = View.VISIBLE
                mBinder.tvFees.visibility = View.GONE
                mBinder.tvVerify.setOnClickListener(View.OnClickListener {
                    interaction?.onItemSelected(pos, item, mBinder.tvVerify)
                })
            }else{
                if(shouldOpenProfile){
                    mBinder.relImage.setOnClickListener(View.OnClickListener {
                        interaction?.onItemSelected(pos, item, mBinder.relImage)
                    })
                }
                mBinder.tvVerify.visibility = View.GONE
               // mBinder.tvFees.visibility = View.VISIBLE
            }


            if(!item.displayPicUrl.isNullOrEmpty()){
                Glide.with(itemView.context).load(item.displayPicUrl).placeholder(R.drawable.ic_doctor).transform(
                    CircleCrop()
                ).into(mBinder.ivImage)
            }else{
                Glide.with(itemView.context).load(R.drawable.ic_doctor).placeholder(R.drawable.ic_doctor).transform(
                    CircleCrop()
                ).into(mBinder.ivImage)
            }

            mBinder.root.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.root)
            })

            if(item.isRequestPending != null && item.isRequestPending){
                mBinder.tvResponsePending.visibility = View.VISIBLE
                mBinder.linSeperator.visibility = View.GONE
               // mBinder.linRoot.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.lightest_bg))
                if(item.requestedUserId != null && item.requestedUserId == item.doctorId){
                    mBinder.relConnectionCOntainer.visibility = View.VISIBLE
                    mBinder.tvResponsePending.visibility = View.GONE
                    mBinder.ivYes.setOnClickListener(View.OnClickListener {
                        interaction?.onItemSelected(pos, item, mBinder.ivYes)
                    })
                    mBinder.ivNo.setOnClickListener(View.OnClickListener {
                        interaction?.onItemSelected(pos, item, mBinder.ivNo)
                    })
                }else{
                    mBinder.relConnectionCOntainer.visibility = View.GONE
                    mBinder.tvResponsePending.visibility = View.VISIBLE
                }


            }else{
                mBinder.relConnectionCOntainer.visibility = View.GONE
                mBinder.tvResponsePending.visibility = View.GONE
                mBinder.linSeperator.visibility = View.GONE
                mBinder.linRoot.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.white))
            }


            mBinder.linRoot.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.linRoot)
            })



        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Doctor, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    doctorFilterList = doctorList
                } else {
                    val resultList = ArrayList<Doctor>()
                    for (row in doctorList) {
                        if (row.firstName.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    doctorFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = doctorFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                doctorFilterList = results?.values as ArrayList<Doctor>
                submitList(doctorFilterList)
            }

        }
    }
}
