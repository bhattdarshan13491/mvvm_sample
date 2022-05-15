package com.oncobuddy.app.views.fragments


import android.app.*
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentDoctorEditTimingsBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.DoctorAvailibilityResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.Weekday
import com.oncobuddy.app.models.pojo.doctor_update.DoctorAvailabilityReq
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import kotlinx.android.synthetic.main.fragment_slot_selection.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class DoctorEditTimingsFragment : BaseFragment() {

    private lateinit var binding: FragmentDoctorEditTimingsBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var weekDayTimingsList = ArrayList<Weekday>()
    private var serverWeekDayTimingsList = ArrayList<Weekday>()

    private var SUNDAY = 6
    private var MONDAY = 0
    private var TUESDAY = 1
    private var WEDNESSDAY = 2
    private var THURSDAY = 3
    private var FRIDAY = 4
    private var SATURDAY = 5

    private var startHour = 0
    private var startMinute = 0
    private var endHour = 0
    private var endMinute = 0

    private var selectedDay = MONDAY
    private lateinit var weekDay: Weekday
    private var IS_TIMING_MODE = true
    private var IS_FIRST_TIME = false
    private var HAS_SELECTED_LOCATION = false
    private var hospitalLocationId = 1
    private var startTimeInMilis = 0L
    private var endTImeInMilis = 0L
    private val weekdaySArray =
        arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }



    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_doctor_edit_timings, container, false
        )

        setupClickListeners()
        showHideBottomButton()
        setupVM()
        setupObservers()
        getDoctorTimings()
        Log.d("days_log", "adding default list")
        weekDayTimingsList = ArrayList()
        for (i in 0..6) {
            var weekday = Weekday()
            weekday.id = i
            weekday.isAvailable = false
            weekday.weekday = weekdaySArray.get(i)
            weekDayTimingsList.add(weekday)
        }
        Log.d("days_log", "added list of " + weekDayTimingsList.size)


    }

    private fun getHospitalsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.getHospitalsListing(
                        getUserAuthToken()
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getDoctorTimings() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.callGetDoctorTimings(
                        getUserAuthToken(), "" + getUserObject().userIdd
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getUpdateTiming() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Timer().schedule(object : TimerTask(){
                override fun run() {
                    if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                        profileViewModel.callGetDoctorTimings(
                            getUserAuthToken(), "" + getUserObject().userIdd
                        )
                    }
                }

            }, Constants.FUNCTION_DELAY);

        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showFromTimePickerDialogue() {
        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog

        mTimePicker = TimePickerDialog(
            FourBaseCareApp.activityFromApp,
            { timePicker, selectedHour, selectedMinute ->

                Log.d("tp_log", "hour " + selectedHour)
                Log.d("tp_log", "hour " + selectedMinute)

                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCal.set(Calendar.MINUTE, selectedMinute)
                startTimeInMilis = selectedCal.timeInMillis



                var fromTime = ""

                if (selectedHour == 0) {
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"
                } else if (selectedHour == 12)
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + selectedHour) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else if (selectedHour > 12)
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"

                binding.linSLotSelection.etFromTime.setText(fromTime)
                startHour = selectedHour
                startMinute = selectedMinute
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time


        mTimePicker.setTitle("Select Start Time")
        mTimePicker.show()

    }

    private fun showToDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog

        mTimePicker = TimePickerDialog(
            FourBaseCareApp.activityFromApp,
            { timePicker, selectedHour, selectedMinute ->

                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCal.set(Calendar.MINUTE, selectedMinute)
                endTImeInMilis = selectedCal.timeInMillis

                var endTime = ""

                if (selectedHour == 0) {
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"
                } else if (selectedHour > 12)
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else if (selectedHour == 12)
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + selectedHour) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"

                binding.linSLotSelection.etToTime.setText(endTime)
                endHour = selectedHour
                endMinute = selectedMinute


            },
            hour,
            minute,
            true
        ) //Yes 24 hour time

        mTimePicker.setTitle("Select End Time")
        mTimePicker.show()
    }


    private fun setSelectedDay() {
        if (!weekDayTimingsList.isNullOrEmpty()) {
            setWeekDayData()
            setDataForSelectedDay()
        }

    }


    private fun setupClickListeners() {

        binding.linSLotSelection.switchAvailibility.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            run {
                if (isChecked) binding.linSLotSelection.etDuration.isEnabled = true
                else binding.linSLotSelection.etDuration.isEnabled = false
            }

        })



        binding.linSLotSelection.etFromTime.setOnClickListener(View.OnClickListener {
            if (linSLotSelection.switchAvailibility.isChecked) {
                showFromTimePickerDialogue()
            } else {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please check availibility to edit timings"
                )
            }

        })

        binding.linSLotSelection.etToTime.setOnClickListener(View.OnClickListener {
            if (linSLotSelection.switchAvailibility.isChecked) {
                showToDatePickerDialogue()
            } else {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please check availibility to edit timings"
                )
            }
        })

        binding.linSLotSelection.linMonday.setOnClickListener(View.OnClickListener {
            selectedDay = MONDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 0) {
                setDataForSelectedDay()
            } else {
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linTue.setOnClickListener(View.OnClickListener {
            selectedDay = TUESDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 1) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == TUESDAY,
                    binding.linSLotSelection.ivTue,
                    binding.linSLotSelection.linTue,
                    binding.linSLotSelection.tvTue
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linWed.setOnClickListener(View.OnClickListener {
            selectedDay = WEDNESSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 2) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == WEDNESSDAY,
                    binding.linSLotSelection.ivWed,
                    binding.linSLotSelection.linWed,
                    binding.linSLotSelection.tvWed
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linThu.setOnClickListener(View.OnClickListener {
            selectedDay = THURSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 3) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == THURSDAY,
                    binding.linSLotSelection.ivThu,
                    binding.linSLotSelection.linThu,
                    binding.linSLotSelection.tvThu
                )
                resetCOntrollers()
            }
        })
        binding.linSLotSelection.linFri.setOnClickListener(View.OnClickListener {
            selectedDay = FRIDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 4) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == FRIDAY,
                    binding.linSLotSelection.ivFri,
                    binding.linSLotSelection.linFri,
                    binding.linSLotSelection.tvFri
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linSat.setOnClickListener(View.OnClickListener {
            selectedDay = SATURDAY
            Log.d("day_log", "0 " + selectedDay)
            setWeekDayData()
            if (weekDayTimingsList.size > 5) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SATURDAY,
                    binding.linSLotSelection.ivSat,
                    binding.linSLotSelection.linSat,
                    binding.linSLotSelection.tvSat
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linSun.setOnClickListener(View.OnClickListener {
            selectedDay = SUNDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 6) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SUNDAY,
                    binding.linSLotSelection.ivSun,
                    binding.linSLotSelection.linSun,
                    binding.linSLotSelection.tvSun
                )
                resetCOntrollers()
            }
        })


        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivSave.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidInput()) {
                var doctorTimingReq = DoctorAvailabilityReq()
                doctorTimingReq.isAvailable =
                    binding.linSLotSelection.switchAvailibility.isChecked
                doctorTimingReq.callDuration =
                    getTrimmedText(binding.linSLotSelection.etDuration).toInt()
                doctorTimingReq.startHour = startHour
                doctorTimingReq.startMinutes = startMinute
                doctorTimingReq.endHour = endHour
                doctorTimingReq.endMinutes = endMinute
                doctorTimingReq.weekday = returnWeekDay()
                profileViewModel.callUpdateDoctorTimings(
                    getUserAuthToken(),
                    "" + getUserObject().userIdd,
                    doctorTimingReq
                )
            }
        })
    }

    private fun resetCOntrollers() {
        binding.linSLotSelection.switchAvailibility.isChecked = false
        binding.linSLotSelection.etFromTime.setText("")
        binding.linSLotSelection.etToTime.setText("")
        binding.linSLotSelection.etDuration.setText("")
    }

    private fun returnWeekDay(): String {
        if (selectedDay == MONDAY) return "MONDAY"
        else if (selectedDay == TUESDAY) return "TUESDAY"
        else if (selectedDay == WEDNESSDAY) return "WEDNESDAY"
        else if (selectedDay == THURSDAY) return "THURSDAY"
        else if (selectedDay == FRIDAY) return "FRIDAY"
        else if (selectedDay == SATURDAY) return "SATURDAY"
        else return "SUNDAY"
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.doctorTimingResponse.observe(viewLifecycleOwner, weekDayListObserver)
        profileViewModel.responseUpdateTimings.observe(viewLifecycleOwner, updateTimingsObserver)
        profileViewModel.isViewLoading.observe(viewLifecycleOwner, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(viewLifecycleOwner, errorMessageObserver)

    }

    private val weekDayListObserver = Observer<DoctorAvailibilityResponse> { responseObserver ->
        if (responseObserver.success) {

            var serverTimingsList = responseObserver.payLoad
            Log.d("days_log", "days came from server " + serverTimingsList.size)
            for (serverDayObj in serverTimingsList) {
                when (serverDayObj.weekday) {
                    "MONDAY" -> {
                        weekDayTimingsList.set(0, serverDayObj)
                        Log.d("days_log", "Monday set")
                    }
                    "TUESDAY" -> {
                        weekDayTimingsList.set(1, serverDayObj)
                        Log.d("days_log", "Tues set")
                    }
                    "WEDNESDAY" -> {
                        weekDayTimingsList.set(2, serverDayObj)
                        Log.d("days_log", "Wed set")
                    }
                    "THURSDAY" -> {
                        weekDayTimingsList.set(3, serverDayObj)
                        Log.d("days_log", "Thurs set")
                    }
                    "FRIDAY" -> {
                        weekDayTimingsList[4] = serverDayObj
                        Log.d("days_log", "Fruday set")
                    }
                    "SATURDAY" -> {
                        weekDayTimingsList.set(5, serverDayObj)
                        Log.d("days_log", "Sat set")
                    }
                    "SUNDAY" -> {
                        weekDayTimingsList.set(6, serverDayObj)
                        Log.d("days_log", "Sunday set")
                    }
                }
            }
            if (IS_FIRST_TIME) {
                selectedDay = MONDAY
                weekDay = weekDayTimingsList.get(0)

            }
            Log.d("clock_log", "0")
            setSelectedDay()


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "No timings found! Please add your suitable timings for virtual consultation"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private fun setWeekDayData() {
        for (weekday in weekDayTimingsList) {
            when (weekday.weekday) {
                "MONDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == MONDAY,
                        binding.linSLotSelection.ivMonday,
                        binding.linSLotSelection.linMonday,
                        binding.linSLotSelection.tvMon
                    )
                }
                "TUESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == TUESDAY,
                        binding.linSLotSelection.ivTue,
                        binding.linSLotSelection.linTue,
                        binding.linSLotSelection.tvTue
                    )
                }
                "WEDNESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == WEDNESSDAY,
                        binding.linSLotSelection.ivWed,
                        binding.linSLotSelection.linWed,
                        binding.linSLotSelection.tvWed
                    )
                }
                "THURSDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable, selectedDay == THURSDAY,
                        binding.linSLotSelection.ivThu,
                        binding.linSLotSelection.linThu,
                        binding.linSLotSelection.tvThu
                    )
                }
                "FRIDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == FRIDAY,
                        binding.linSLotSelection.ivFri,
                        binding.linSLotSelection.linFri,
                        binding.linSLotSelection.tvFri
                    )
                }
                "SATURDAY" -> {

                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SATURDAY,
                        binding.linSLotSelection.ivSat,
                        binding.linSLotSelection.linSat,
                        binding.linSLotSelection.tvSat
                    )
                }
                "SUNDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SUNDAY,
                        binding.linSLotSelection.ivSun,
                        binding.linSLotSelection.linSun,
                        binding.linSLotSelection.tvSun
                    )

                }


            }
        }


    }

    private fun setLookAsPerDay(
        isDayAvailable: Boolean,
        isDaySelected: Boolean,
        ivImage: ImageView,
        linBg: LinearLayout,
        tvTextView: TextView
    ) {

        Log.d("selection_journey", "0")
        if (isDayAvailable) {
            if (isDaySelected) {
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.iv_circular_tick_white
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.red_border_purple_bg)
                tvTextView.setTextColor(getResourceColor(R.color.white))
            } else {
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.iv_circular_tick_white
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.no_border_purple_bg)
                tvTextView.setTextColor(getResourceColor(R.color.white))
            }

        } else {
            Log.d("selection_journey", "1")
            if (isDaySelected) {
                Log.d("selection_journey", "2")
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_add_red
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.red_border_gray_bg)
                tvTextView.setTextColor(getResourceColor(R.color.colorPrimaryDark))
            } else {
                Log.d("selection_journey", "3")
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_add_red
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.no_border_gray_bg)
                tvTextView.setTextColor(getResourceColor(R.color.colorPrimaryDark))
            }
        }
    }

    private fun setDataForSelectedDay() {

        weekDay = weekDayTimingsList.get(selectedDay)

        Log.d("day_log", "weekday " + weekDay.weekday)
        Log.d("day_log", "weekday " + weekDay.isAvailable)
        binding.linSLotSelection.switchAvailibility.isChecked = weekDay.isAvailable
        if (weekDay.isAvailable) {
            //Log.d("clock_log","weekday available")
            var fromTime = ""
            //startHour = weekDay.startHour
            //startMinute = weekDay.startMinutes
            Log.d("clock_log", "weekday available")
            Log.d("clock_log", "start h " + startHour)
            Log.d("clock_log", "start m" + startMinute)

            if (startHour == 0)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " AM"
            else if (startHour > 12)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " PM"
            else if (startHour == 12)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + startHour) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " PM"
            else
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " AM"


            var endTime = ""
            //endHour = weekDay.endHour
            //endMinute = weekDay.endMinutes

            Log.d("clock_log", "end h " + endHour)
            Log.d("clock_log", "end m" + endMinute)

            if (endHour == 0)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " AM"
            else if (endHour > 12)
                endTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " PM"
            else if (endHour == 12)
                endTime =
                    CommonMethods.getStringWithOnePadding("" + endHour) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " PM"
            else
                endTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " AM"

            Log.d("clock_log", "from time set " + fromTime)
            Log.d("clock_log", "to time set " + endTime)

            // var startTime = weekDay.startHour.toString() + " : " + weekDay.startMinutes
            // var endTime = weekDay.endHour.toString() + " : " + weekDay.endMinutes
            binding.linSLotSelection.etFromTime.setText(fromTime)
            binding.linSLotSelection.etToTime.setText(endTime)
            binding.linSLotSelection.etDuration.setText("" + weekDay.callDuration)
            binding.linSLotSelection.etDuration.isEnabled = true

        } else {
            binding.linSLotSelection.etDuration.isEnabled = false
            resetCOntrollers()
        }
    }


    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun isValidInput(): Boolean {
            Log.d("timing_log","start time "+startTimeInMilis)
            Log.d("timing_log","start time "+endTImeInMilis)
            if(getTrimmedText(binding.linSLotSelection.etFromTime).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select From Time")
                return false
            }
            else if(getTrimmedText(binding.linSLotSelection.etToTime).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select to Time")
                return false
            }else if(getTrimmedText(binding.linSLotSelection.etFromTime).equals(getTrimmedText(binding.linSLotSelection.etToTime),true)){
                showToast(FourBaseCareApp.activityFromApp, "Consultation start and end timings cannot be the same")
                return false
            }else if(startTimeInMilis > endTImeInMilis){
                showToast(FourBaseCareApp.activityFromApp, "Consultation start time should be earlier than end time")
                return false
            } else if(getTrimmedText(binding.linSLotSelection.etDuration).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select consultation duration")
                return false
            }else if(getTrimmedText(binding.linSLotSelection.etDuration).toInt() == 0){
                showToast(FourBaseCareApp.activityFromApp, "Duration time cannot be 0")
                return false
            }else{
                return true
            }

    }

    private fun showHideBottomButton() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden")
                        //binding.tvEditProfile.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        // binding.tvEditProfile.setVisibility(View.VISIBLE)
                    }
                }
            })
    }
    private val updateTimingsObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData

            //FILE_URL = responseObserver.message
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Timings updated Successfully")
            binding.executePendingBindings()
            binding.invalidateAll()
            getDoctorTimings()
        }
}