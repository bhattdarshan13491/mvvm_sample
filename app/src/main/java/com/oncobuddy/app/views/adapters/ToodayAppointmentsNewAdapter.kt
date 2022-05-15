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
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawPatientAppointmentBinding
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ToodayAppointmentsNewAdapter(private var appointmentsList : ArrayList<AppointmentDetails>,
                                   private val interaction: Interaction? = null, private val isPastList: Boolean = false) :
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

        val mBinder: RawPatientAppointmentBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_patient_appointment, parent, false
        )

        return ShoppingItemVH(
            mBinder,
            interaction,
            isPastList
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
        binding: RawPatientAppointmentBinding,
        private val interaction: Interaction?,
        private val isPastList: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawPatientAppointmentBinding = binding

        fun bind(item: AppointmentDetails, pos: Int) {
            Log.d("appointment_log", "id "+item.id)
            var doctorName = ""
            var dpLink = ""
            var speciality = ""
            for (paritcipant in item.participants) {
                if(Constants.IS_DOCTOR_LOGGED_IN){
                    if(paritcipant.role.equals(Constants.ROLE_PATIENT, true))
                        doctorName = paritcipant.name
                    if(paritcipant.dpLink != null)dpLink = paritcipant.dpLink

                    Log.d("dp_log","patient name set "+doctorName)
                }else{
                    if(paritcipant.role.equals(Constants.ROLE_DOCTOR, true)){
                        doctorName = paritcipant.name
                        speciality = ""+paritcipant.headline
                        if(paritcipant.dpLink != null) dpLink = paritcipant.dpLink

                        Log.d("dp_log","doctor name set "+doctorName)
                    }
                }
            }
            mBinder.tvDoctorName.setText(doctorName)
            mBinder.tvSpeciality.setText(speciality)
            mBinder.tvDateTime.setText(CommonMethods.getDateForAppt(item.scheduledTime))
            if(item.notes.isNullOrEmpty())mBinder.tvNotes.setText("No notes available")
            else mBinder.tvNotes.setText(item.notes)
            if(!dpLink.isNullOrEmpty()){
                Glide.with(FourBaseCareApp.activityFromApp).load(dpLink).placeholder(R.drawable.gray_circle).circleCrop().into(mBinder.ivProfilePic)
                Log.d("dp_log","dp set "+dpLink)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.gray_circle).circleCrop().into(mBinder.ivProfilePic)
                Log.d("dp_log","dp is empty")
            }

            mBinder.tvJoinRoom.setOnClickListener(View.OnClickListener {
                interaction?.onTodayAppointmentSelected(pos, item, mBinder.tvJoinRoom)
            })
            Log.d("doctor_name","")
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

        fun getTimeAndDate(inputTime: String): String? {
            return try {
                val simpleTimeFormatInput = SimpleDateFormat(
                    Constants.INPUT_DATE_FORMAT,
                    Locale.getDefault()
                )
                val simpleTimeFormatOutput = SimpleDateFormat(
                    "hh:mm aa\ndd MMM yy",
                    Locale.getDefault()
                )
                val mDateTime = simpleTimeFormatInput.parse(inputTime)
                simpleTimeFormatOutput.format(mDateTime)
            } catch (e: ParseException) {
                inputTime
            }

        }

        fun getDateAndTime(inputTime: String): String? {
            return try {
                val simpleTimeFormatInput = SimpleDateFormat(
                    Constants.INPUT_DATE_FORMAT,
                    Locale.getDefault()
                )
                val simpleTimeFormatOutput = SimpleDateFormat(
                    "dd MMM hh:mm\naa",
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
        fun onTodayAppointmentSelected(position: Int, item: AppointmentDetails, view : View)
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

