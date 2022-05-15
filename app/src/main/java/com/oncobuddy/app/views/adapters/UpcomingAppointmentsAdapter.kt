package com.oncobuddy.app.views.adapters

import android.util.Log
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
import com.oncobuddy.app.databinding.RawUpcomingAppointmentsBinding
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class UpcomingAppointmentsAdapter(private var appointmentsList : ArrayList<AppointmentDetails>,
                                  private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var appointmentsFilterList = ArrayList<AppointmentDetails>()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppointmentDetails>() {

        override fun areItemsTheSame(oldItem: AppointmentDetails, newItem: AppointmentDetails): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppointmentDetails, newItem: AppointmentDetails): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawUpcomingAppointmentsBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_upcoming_appointments, parent, false
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

    fun submitList(list: List<AppointmentDetails>) {
        differ.submitList(list)
    }

    fun clearALL(){
             appointmentsList.clear()
             appointmentsFilterList.clear()
             notifyDataSetChanged()
    }



    class ShoppingItemVH constructor(
        binding: RawUpcomingAppointmentsBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawUpcomingAppointmentsBinding = binding

        fun bind(item: AppointmentDetails, pos: Int) {
            Log.d("appointment_log", "id "+item.id)
            var doctorName = ""
            for (paritcipant in item.participants) {

                if(Constants.IS_DOCTOR_LOGGED_IN){
                    if(paritcipant.role.equals(Constants.ROLE_PATIENT, true))
                        doctorName = paritcipant.name
                }else{
                    if(paritcipant.role.equals(Constants.ROLE_DOCTOR, true))
                        doctorName = paritcipant.name
                }

            }

            if(Constants.IS_DOCTOR_LOGGED_IN){
            mBinder.tvView.setText("Start Call")
            }

            mBinder.tvDoctorName.setText(doctorName)
            //mBinder.tvPurpose.setText(CommonMethods.returnPurpose(item.appointmentPurpose))
            mBinder.tvTime.setText(changeTimeFormat(item.scheduledTime))
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



        fun changeTimeFormat(inputTime: String): String? {
            return try {
                val simpleTimeFormatInput = SimpleDateFormat(
                    Constants.INPUT_DATE_FORMAT,
                    Locale.getDefault()
                )
                val simpleTimeFormatOutput = SimpleDateFormat(
                    "hh:mm aa",
                    Locale.getDefault()
                )
                val mDateTime = simpleTimeFormatInput.parse(inputTime)
                simpleTimeFormatOutput.format(mDateTime)
            } catch (e: ParseException) {
                inputTime
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: AppointmentDetails, view : View)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    appointmentsFilterList = appointmentsList
                } else {
                    val resultList = ArrayList<AppointmentDetails>()
                    for (row in appointmentsList) {
                        if (row.appointmentStatus.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    appointmentsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = appointmentsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                appointmentsFilterList = results?.values as ArrayList<AppointmentDetails>
                submitList(appointmentsFilterList)
            }

        }
    }

}

