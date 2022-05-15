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
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawAppointmentDoctorBinding
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DoctorAppointmentsNewAdapter(
    private var appointmentsList: ArrayList<AppointmentDetails>,
    private val interaction: Interaction? = null, private val isPastList: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var appointmentsFilterList = ArrayList<AppointmentDetails>()


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AppointmentDetails>() {

        override fun areItemsTheSame(
            oldItem: AppointmentDetails,
            newItem: AppointmentDetails
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AppointmentDetails,
            newItem: AppointmentDetails
        ): Boolean {
            return oldItem.equals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val mBinder: RawAppointmentDoctorBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_appointment_doctor, parent, false
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
                holder.bind(differ.currentList.get(position), position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<AppointmentDetails>) {
        differ.submitList(list)
    }

    fun clearALL() {
        appointmentsList.clear()
        appointmentsFilterList.clear()
        notifyDataSetChanged()
    }


    class ShoppingItemVH constructor(
        binding: RawAppointmentDoctorBinding,
        private val interaction: Interaction?,
        private val isPastList: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawAppointmentDoctorBinding = binding

        fun bind(item: AppointmentDetails, pos: Int) {
            var doctorName = ""
            var dpLink = ""
            for (paritcipant in item.participants) {
                if (Constants.IS_DOCTOR_LOGGED_IN) {
                    if (paritcipant.role.equals(Constants.ROLE_PATIENT, true))
                        doctorName = paritcipant.name
                    if (paritcipant.dpLink != null) dpLink = paritcipant.dpLink

                    Log.d("dp_log", "patient name set " + doctorName)
                } else {
                    if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                        doctorName = paritcipant.name
                        if (paritcipant.dpLink != null) dpLink = paritcipant.dpLink

                        Log.d("dp_log", "doctor name set " + doctorName)
                    }
                }
            }
            Log.d("dp_log", "final name set " + doctorName)
            mBinder.tvDoctorName.setText(doctorName)
            if (!dpLink.isNullOrEmpty()) {
                Glide.with(FourBaseCareApp.activityFromApp).load(dpLink)
                    .placeholder(R.drawable.gray_circle).circleCrop().into(mBinder.ivPic)
                Log.d("dp_log", "dp set " + dpLink)
            } else {
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.gray_circle)
                    .circleCrop().into(mBinder.ivPic)
                Log.d("dp_log", "dp is empty")
            }

            var date = ""
            var month = ""
            var time = ""

            var dateTIme = getDateAndTime(item.scheduledTime)!!.split(" ")
            date = dateTIme[0]
            month = dateTIme[1]
            time = dateTIme[2]

            if(item.appointmentStatus.equals(Constants.SCHEDULED)){
                mBinder.tvApptDate.visibility = View.VISIBLE
                mBinder.linStatus.visibility = View.GONE
                mBinder.tvApptDate.setText("On " + date + " " + month + " 2021 " + time)
            }else if(item.appointmentStatus.equals(Constants.COMPLETED)){
                mBinder.tvApptDate.visibility = View.GONE
                mBinder.linStatus.visibility = View.VISIBLE
                mBinder.tvStatusVal.text = "Completed On "+getDateAndTime(item.lastModified)
                mBinder.tvStatusVal.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.green_color_button))
            }else if(item.appointmentStatus.equals(Constants.CANCELLED)){
                mBinder.tvApptDate.visibility = View.GONE
                mBinder.linStatus.visibility = View.VISIBLE
                mBinder.tvStatusVal.text = "Cancelled On "+getDateAndTime(item.lastModified)
                mBinder.tvStatusVal.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.skip_to_login_red))
            }else{
                mBinder.tvApptDate.setText("On " + date + " " + month + " 2021 " + time)
                mBinder.tvApptDate.visibility = View.VISIBLE
                mBinder.linStatus.visibility = View.GONE
            }


            if (item.notes.isNullOrEmpty()) mBinder.tvNotes.setText("No notes available")
            else mBinder.tvNotes.setText(item.notes)


            mBinder.ivDropDown.setOnClickListener(View.OnClickListener {
                if (item.isShowingDetails) {
                    Log.d("drop_down_log","details off")
                         mBinder.tvNotes.visibility = View.GONE
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
                } else {
                    Log.d("drop_down_log","details on")
                    mBinder.tvNotes.visibility = View.VISIBLE
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
                }
                item.isShowingDetails = !item.isShowingDetails
            })

            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.ivMenu)
            })

            mBinder.cardViewContainer.setOnClickListener(View.OnClickListener {
                interaction?.onItemSelected(pos, item, mBinder.cardViewContainer)
            })
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
                    "dd MMM yy hh:mm aa",
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
                    "dd MMM hh:mm aa",
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
        fun onItemSelected(position: Int, item: AppointmentDetails, view: View)
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
                        if (row.appointmentStatus.toLowerCase(Locale.ROOT).contains(
                                charSearch.toLowerCase(
                                    Locale.ROOT
                                )
                            )
                        ) {
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

