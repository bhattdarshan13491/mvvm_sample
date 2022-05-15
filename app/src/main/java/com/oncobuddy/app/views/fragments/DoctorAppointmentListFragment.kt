package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAddOrUpdateAppointment2Binding
import com.oncobuddy.app.databinding.FragmentAppointmentsDoctorListBinding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.appointments.AddAppointmentResponse
import com.oncobuddy.app.models.pojo.appointments.ParticipantInput
import com.oncobuddy.app.models.pojo.appointments.input.AddAppointmentInput
import com.oncobuddy.app.models.pojo.appointments.input.AddGuestInput
import com.oncobuddy.app.models.pojo.appointments.input.PaymentInput
import com.oncobuddy.app.models.pojo.appointments.input.TimeSlotsInput
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverDetails
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.time_slots_listing.TimeSlot
import com.oncobuddy.app.models.pojo.doctors.time_slots_listing.TimeSlotsListingResponse
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalDetails
import com.oncobuddy.app.models.pojo.vouchers_response.VoucherDetails
import com.oncobuddy.app.models.pojo.vouchers_response.VoucherListResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.PaymentActivity
import com.oncobuddy.app.views.adapters.*
import kotlinx.android.synthetic.main.fragment_appointment_details.*
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class DoctorAppointmentListFragment : BaseFragment(), DatePickerListener, DoctorAppointmentsNewAdapter.Interaction{

    private lateinit var binding: FragmentAppointmentsDoctorListBinding
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var root_view: View
    private var selectedDateTime = DateTime()
    private var selectedDate: DateTime = DateTime()
    private lateinit var apppointmentsList: ArrayList<AppointmentDetails>
    private lateinit var filteredApppointmentsList: ArrayList<AppointmentDetails>
    private lateinit var doctorAPpointmentsAdapter: DoctorAppointmentsNewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        root_view = binding.root
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_appointments_doctor_list, container, false)

        Log.d("status_log","0")
        setupDatePicker()
        setupVM()
        setupObservers()
        getData()
        setClickListeners()
    }

    private fun getData() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                CommonMethods.showLog("record_log", "0")
                getAppointmentsFromServer()
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setClickListeners() {
        binding.ivCalender.setOnClickListener(View.OnClickListener {
            showDatePickerDialogue()
        })
    }

    private fun showDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val yy = calendar[Calendar.YEAR]
        val mm = calendar[Calendar.MONTH]
        val dd = calendar[Calendar.DAY_OF_MONTH]
        val datePicker =
            DatePickerDialog(
                FourBaseCareApp.activityFromApp,
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val chosenDate = Time()
                    chosenDate[dayOfMonth, monthOfYear] = year
                    val dtDob = chosenDate.toMillis(true)
                    var strDate = DateFormat.format("yyyy-MM-dd", dtDob)
                    //selectedDate = strDate.toString()
                    selectedDateTime = DateTime.parse(strDate.toString())
                    binding.datePicker.setDate(selectedDateTime)

                }, yy, mm, dd
            )
             datePicker.show()
    }

    private fun getAppointmentsFromServer() {
        appointmentViewModel.callGetAppointment(false,false,getUserAuthToken(), getUserIdd().toString())
    }

    private fun setupDatePicker() {
        binding
            .datePicker
            .setOffset(90)
            .setListener(this)
            .setDateSelectedColor(getResourceColor(R.color.colorPrimaryDark))
            .setDateSelectedTextColor(getResourceColor(R.color.white))
            .setMonthAndYearTextColor(getResourceColor(R.color.skip_to_login_red))
            .init()

        binding.datePicker.setBackgroundColor(
            ContextCompat.getColor(
                FourBaseCareApp.activityFromApp.applicationContext,
                R.color.comments_gray_bg
            )
        )
        selectedDateTime = DateTime()
        Log.d("counter_log", "Initial date set " + selectedDateTime)
        binding.datePicker.setDate(DateTime())
    }

    private fun setupVM() {
        appointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)
    }

    private fun setupObservers() {
        appointmentViewModel.appointmentsList.observe(this, responseObserver)
        appointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    override fun onDateSelected(dateSelected: DateTime?) {
        if (dateSelected != null) {
            selectedDate = dateSelected
            selectedDateTime = dateSelected
            filteredAppointmentList()
        }
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val responseObserver = androidx.lifecycle.Observer<Response<AppointmentsListResponse?>>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if(responseObserver.isSuccessful){
            apppointmentsList = ArrayList()
            val arrayList = responseObserver.body()?.payLoad
            Log.d("list_log","total size "+arrayList?.size)

            apppointmentsList = responseObserver.body()?.payLoad as ArrayList<AppointmentDetails>

            filteredAppointmentList()
        }

    }

    private fun filteredAppointmentList() {

        if(::apppointmentsList.isInitialized){

            filteredApppointmentsList = ArrayList()
            val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
            val strFormattedDate: String = fmt.print(selectedDate)
            Log.d("filter_date","Selected date "+strFormattedDate)

            apppointmentsList.forEach { appointment ->
                if (appointment.scheduledTime != null && !appointment.paymentStatus.equals("Pending", true)
                ) {
                    //var scheduleDate = CommonMethods.changeCOmmentDateTimeFormat()
                    var formattedDate = changeAppointmentDateFormat(appointment.scheduledTime)
                    Log.d("filter_date","scehduled formattedDate "+formattedDate)
                    if (strFormattedDate.equals(formattedDate)) {
                        filteredApppointmentsList.add(appointment)
                    }
                }

            }
                setRecyclerView()
        }
    }

    fun changeAppointmentDateFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    private fun setRecyclerView() {
        if(::filteredApppointmentsList.isInitialized && !filteredApppointmentsList.isEmpty() && filteredApppointmentsList.size > 0){
            binding.recyclerView.apply {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
                layoutManager = LinearLayoutManager(activity)
                doctorAPpointmentsAdapter = DoctorAppointmentsNewAdapter(
                    filteredApppointmentsList,
                    this@DoctorAppointmentListFragment,
                    false
                )
                adapter = doctorAPpointmentsAdapter
                doctorAPpointmentsAdapter.submitList(filteredApppointmentsList)
            }
        }else{
            binding.recyclerView.visibility = View.GONE
            binding.tvNoData.visibility = View.VISIBLE
        }



    }

    override fun onItemSelected(position: Int, item: AppointmentDetails, view: View) {


        if (!isDoubleClick()) {
            if (view.id == R.id.cardViewContainer) {

                var bundle = Bundle()
                bundle.putParcelable(Constants.APPOINTMENT_OBJ, item)
                bundle.putString(Constants.SOURCE, "doctor_appointments")
                var appointmentDetails = AppointmentDetailsFragment()
                appointmentDetails.arguments = bundle
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    appointmentDetails,
                    this,
                    false
                )

            }
        }
    }

}