package com.oncobuddy.app.views.adapters

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
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
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.RawAppointmentNewBinding
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import java.text.ParseException
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


class PatientFutureAppointmentsNewAdapter(private var appointmentsList : ArrayList<AppointmentDetails>,
                                          private val interaction: Interaction? = null,
                                          private val isPastList: Boolean = false) :
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

        val mBinder: RawAppointmentNewBinding
        mBinder = DataBindingUtil.inflate(
            LayoutInflater
                .from(parent.context), R.layout.raw_appointment_new, parent, false
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
        binding: RawAppointmentNewBinding,
        private val interaction: Interaction?,
        private val isPastList: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        var mBinder: RawAppointmentNewBinding = binding

        fun bind(item: AppointmentDetails, pos: Int) {
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
            Log.d("dp_log","final name set "+doctorName)
            mBinder.tvDoctorName.setText(doctorName)
            mBinder.tvSpeciality.setText(speciality)
            if(!dpLink.isNullOrEmpty()){
                Glide.with(FourBaseCareApp.activityFromApp).load(dpLink).placeholder(R.drawable.gray_circle).circleCrop().into(mBinder.ivPic)
                Log.d("dp_log","dp set "+dpLink)
            }else{
                Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.gray_circle).circleCrop().into(mBinder.ivPic)
                Log.d("dp_log","dp is empty")
            }



            Log.d("apt_date_log","Date set "+item.scheduledTime)
           mBinder.tvApptDate.setText("On "+CommonMethods.getDateForAppt(item.scheduledTime))
             if(Constants.IS_DOCTOR_LOGGED_IN && item.patientRequestedCallback != null && item.patientRequestedCallback){
                    //mBinder.ivCallBack.visibility = View.VISIBLE
                   /* mBinder.ivCallBack.setOnClickListener(View.OnClickListener {
                        interaction?.onItemSelected(pos, item, mBinder.ivCallBack)
                    })*/
                }else{
                   // mBinder.ivCallBack.visibility = View.GONE
                }

            if(isPastList){
                mBinder.tvNotes.visibility = View.GONE
                mBinder.tvApptDate.visibility = View.GONE
                Log.d("apt_date_log","Gone")
                mBinder.ivDropDown.visibility = View.GONE
                mBinder.linStatus.visibility = View.VISIBLE
                //mBinder.tvRequestReSchedule.visibility = View.GONE
               // mBinder.ivView.visibility = View.GONE
                //mBinder.tvCompletionDate.visibility = View.VISIBLE



                if(Constants.IS_DOCTOR_LOGGED_IN){
                if(item.patientRequestedCallback !=null && item.patientRequestedCallback){
                  //  mBinder.ivView.visibility = View.VISIBLE
                }else{
                   // mBinder.ivView.visibility = View.GONE
                }
                }

                if(item.appointmentStatus.equals("CANCELLED")){
                    mBinder.tvStatusVal.setText("Cancelled on : "+getTimeAndDate(item.scheduledTime))
                    mBinder.tvStatusVal.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.skip_to_login_red))

                }else if(item.appointmentStatus.equals("COMPLETED")){
                    mBinder.tvStatusVal.setText("Completed on : "+getTimeAndDate(item.scheduledTime))
                    mBinder.tvStatusVal.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.green_color_button))
                } else{
                   mBinder.tvStatusVal.setText("Scheduled on : "+getTimeAndDate(item.scheduledTime))
                   //
                }

                /*if(Constants.IS_DOCTOR_LOGGED_IN || item.appointmentStatus.equals("CANCELLED")){
                    //mBinder.tvRequestCallback.visibility = View.GONE
                }else{
                    var HOUR = 3600*1000
                    //var dateAdd72Hours = Date().time.plus(72 * HOUR)
                    Log.d("date_log","scheduled time "+item.scheduledTime)
                   // Log.d("date_log","current time "+Date().toString())
                    Log.d("date_log","3 days scheduled time "+Date(CommonMethods.getDate(item.scheduledTime)!!.time.plus(72 * HOUR)))

                    if(Date().after(Date(CommonMethods.getDate(item.scheduledTime)!!.time.plus(72 * HOUR))) == true){
                       // mBinder.tvRequestCallback.visibility = View.GONE
                        Log.d("date_log","After 72 hours")
                    }else{
                        Log.d("date_log","Before 72 hours")
                        //mBinder.tvRequestCallback.visibility = View.VISIBLE
                        *//*mBinder.tvRequestCallback.setOnClickListener(View.OnClickListener {
                            interaction?.onItemSelected(pos, item, mBinder.tvRequestCallback)
                        })*//*
                    }
                }*/
            }else{
                //mBinder.tvApptDate.visibility = View.GONE
                //mBinder.tvNotes.visibility = View.VISIBLE
                mBinder.linStatus.visibility = View.GONE
                //mBinder.ivView.visibility = View.VISIBLE
               // mBinder.ivCallBack.visibility = View.GONE
          /*      mBinder.tvCompletionDate.visibility = View.GONE
                mBinder.tvRequestCallback.visibility = View.GONE
*/              mBinder.ivDropDown.visibility = View.VISIBLE
                if(item.notes.isNullOrEmpty())mBinder.tvNotes.setText("No notes available")
                else mBinder.tvNotes.setText(item.notes)


                mBinder.ivDropDown.setOnClickListener(View.OnClickListener {
                    //interaction?.onMonthSelected(pos, item, mBinder.root)
                    if(item.isShowingDetails){
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
                    }else{
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


            }

            mBinder.ivMenu.setOnClickListener(View.OnClickListener {
                interaction?.onFutureApptItemSelected(pos, item, mBinder.ivMenu)
            })
            mBinder.cardContainer.setOnClickListener(View.OnClickListener {
                interaction?.onFutureApptItemSelected(pos, item, mBinder.cardContainer)
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
        fun onFutureApptItemSelected(position: Int, item: AppointmentDetails, view : View)
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

