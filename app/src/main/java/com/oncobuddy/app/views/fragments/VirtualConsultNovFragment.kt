package com.oncobuddy.app.views.fragments

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.LayoutDoctorVcSetupBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.bank_acct_details.BankAccountInput
import com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityInput
import com.oncobuddy.app.models.pojo.doctor_availibility.SlotsItem
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.DoctorAvailibilityResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.Weekday
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.education_degrees.Education
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.EducationAdapter
import com.oncobuddy.app.views.adapters.SlotItemsAdapter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class VirtualConsultNovFragment : BaseFragment(), SlotItemsAdapter.Interaction {

    private lateinit var binding: LayoutDoctorVcSetupBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val TIME_SLOT = 0
    private val BANK_DETAILS = 1
    private var IS_SHOWING_TIME_SLOT = true

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
    private var weekDayTimingsList = ArrayList<Weekday>()
    private var IS_FIRST_TIME = false
    private var IS_BANK_OPTION_SELECTED = false
    private var IS_DETAIL_ADDED = false
    private var slotTimingsList = ArrayList<SlotsItem>()
    private lateinit var slotItemsAdapter: SlotItemsAdapter

    private val weekdaySArray =
        arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

    private var startTimeInMilis = 0L
    private var endTImeInMilis = 0L

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_doctor_vc_setup, container, false
        )
        Log.d("schedule_log","Opened")
        setupClickListeners()
        initializeTimeSLotsList()
        setupTitle()
        setupVM()
        setupObservers()
        getDoctorTimings()
        weekDayTimingsList = ArrayList()
        for (i in 0..6) {
            var weekday = Weekday()
            weekday.id = i
            weekday.isAvailable = false
            weekday.weekday = weekdaySArray.get(i)
            weekDayTimingsList.add(weekday)
        }

        Log.d("detail_log","0")
        if (getUserObject().profile.bankDetailsAdded != null) {
            Log.d("detail_log","1")
            if (getUserObject().profile.bankDetailsAdded.equals("true")) {
                Log.d("detail_log","2")
                IS_DETAIL_ADDED = true
                binding.layoutBankDetails.linBankForm.visibility = View.GONE
                binding.layoutBankDetails.relUpdatedMessage.visibility = View.VISIBLE
            } else {
                Log.d("detail_log","3")
                IS_DETAIL_ADDED = false
                binding.layoutBankDetails.linBankForm.visibility = View.VISIBLE
                binding.layoutBankDetails.relUpdatedMessage.visibility = View.GONE
            }

        }

        var strFees = "" + getUserObject().profile.consultationFee
        if (strFees.equals("null", true)) {
            binding.layoutBankDetails.edFees.setText("")
        } else {
            val df = DecimalFormat("#.##")
            binding.layoutBankDetails.edFees.setText("" + df.format(getUserObject().profile.consultationFee))
        }

    }

    private fun setTimeSLotsList(list: List<SlotsItem>) {

        slotTimingsList = ArrayList()
        slotTimingsList.addAll(list)
        //slotTimingsList.add(SlotsItem())
        binding.layoutTimeSLot.rvTImeSlot.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            slotItemsAdapter = SlotItemsAdapter(slotTimingsList, this@VirtualConsultNovFragment)
            adapter = slotItemsAdapter
            slotItemsAdapter.submitList(slotTimingsList)
        }
    }

    private fun initializeTimeSLotsList() {
        slotTimingsList = ArrayList()
        binding.layoutTimeSLot.rvTImeSlot.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            slotItemsAdapter = SlotItemsAdapter(slotTimingsList, this@VirtualConsultNovFragment)
            adapter = slotItemsAdapter
            slotItemsAdapter.submitList(slotTimingsList)
        }
    }


    private fun setSelectionColor(category : Int){
        if(category == TIME_SLOT){
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))

        }else{
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.nov_line_gray))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

        if(category == BANK_DETAILS){
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))
        }else{
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.gray))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

    }


    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.liveUpdateDrAvailibilityResponse.observe(this, updateTimeSlotData)
        profileViewModel.addBankAcctResonseData.observe(this, updateBankDetailsData)
        profileViewModel.doctorTimingResponse.observe(viewLifecycleOwner, weekDayListObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun updateTImings(){
        if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            if(isValidInput()) {
                var doctorAvailibilityInput = DoctorAvailibilityInput()
                if(binding.layoutTimeSLot.switchAvailibility.isChecked){
                    doctorAvailibilityInput.weekday = returnWeekDay()
                    doctorAvailibilityInput.callDuration = getTrimmedText(binding.layoutTimeSLot.edDuration).toInt()
                    doctorAvailibilityInput.isAvailable = true
                    doctorAvailibilityInput.slots = slotTimingsList
                    Log.d("slot_timings","Sent "+slotTimingsList.size)
                    profileViewModel.updateDoctorTimeSLots(getUserAuthToken(), doctorAvailibilityInput)
                }else{
                    doctorAvailibilityInput.weekday = returnWeekDay()
                    doctorAvailibilityInput.isAvailable = false
                    profileViewModel.updateDoctorTimeSLots(getUserAuthToken(), doctorAvailibilityInput)
                }


            }
        }
    }

    private fun updateBankDetails(){
        if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
            if(isValidBankInput()) {

                if(IS_BANK_OPTION_SELECTED){
                    var bankAccountInput = BankAccountInput()
                    bankAccountInput.bankAccountType = Constants.ACCOUNT_TYPE_BANK
                    bankAccountInput.phoneNumber = getUserObject().phoneNumber
                    bankAccountInput.name = getTrimmedText(binding.layoutBankDetails.edName).toUpperCase()
                    bankAccountInput.accountNumber = getTrimmedText(binding.layoutBankDetails.edAccountNo)
                    bankAccountInput.ifsc = getTrimmedText(binding.layoutBankDetails.edIFSCCode).toUpperCase()
                    bankAccountInput.address = ""
                    profileViewModel.callAddBankAcctDetails(getUserAuthToken(), bankAccountInput)

                }else{
                    var bankAccountInput = BankAccountInput()
                    bankAccountInput.bankAccountType = Constants.ACCOUNT_TYPE_UPI
                    bankAccountInput.phoneNumber = getUserObject().phoneNumber
                    bankAccountInput.address = getTrimmedText(binding.layoutBankDetails.edUpi)
                    bankAccountInput.name = getTrimmedText(binding.layoutBankDetails.edUpiName).toUpperCase()
                    bankAccountInput.accountNumber = ""
                    bankAccountInput.ifsc = ""


                    profileViewModel.callAddBankAcctDetails(getUserAuthToken(), bankAccountInput)
                }


            }
        }
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

    private fun showSectionPerSelection() {

        if (IS_SHOWING_TIME_SLOT) {
            binding.layoutTimeSLot.relContainer.visibility = View.VISIBLE
            binding.layoutBankDetails.relCOntainer.visibility = View.GONE
        } else {
            binding.layoutTimeSLot.relContainer.visibility = View.GONE
            binding.layoutBankDetails.relCOntainer.visibility = View.VISIBLE
        }
    }

    private fun setupClickListeners() {

        binding.layoutBankDetails.relSaveFees.setOnClickListener(View.OnClickListener {
            validateAndSendData()
        })

        binding.layoutBankDetails.relBankAcct.setOnClickListener(View.OnClickListener {
            IS_BANK_OPTION_SELECTED  =  true
            binding.layoutBankDetails.relUpi.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.no_border_white_bg)
            binding.layoutBankDetails.relBankAcct.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.blue_border_white_bg)
            binding.layoutBankDetails.linUpi.visibility = View.GONE
            binding.layoutBankDetails.linBankDetails.visibility = View.VISIBLE
        })

        binding.layoutBankDetails.relUpi.setOnClickListener(View.OnClickListener {
            IS_BANK_OPTION_SELECTED  =  false
            binding.layoutBankDetails.relUpi.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.blue_border_white_bg)
            binding.layoutBankDetails.relBankAcct.background = ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.no_border_white_bg)
            binding.layoutBankDetails.linUpi.visibility = View.VISIBLE
            binding.layoutBankDetails.linBankDetails.visibility = View.GONE
        })

        binding.layoutTimeSLot.linMonday.setOnClickListener(View.OnClickListener {
            selectedDay = MONDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 0) {
                setDataForSelectedDay()
            } else {
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.linTue.setOnClickListener(View.OnClickListener {
            selectedDay = TUESDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 1) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == TUESDAY,
                    binding.layoutTimeSLot.ivTue,
                    binding.layoutTimeSLot.linTue,
                    binding.layoutTimeSLot.tvTue
                )
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.linWed.setOnClickListener(View.OnClickListener {
            selectedDay = WEDNESSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 2) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == WEDNESSDAY,
                    binding.layoutTimeSLot.ivWed,
                    binding.layoutTimeSLot.linWed,
                    binding.layoutTimeSLot.tvWed
                )
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.linThu.setOnClickListener(View.OnClickListener {
            selectedDay = THURSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 3) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == THURSDAY,
                    binding.layoutTimeSLot.ivThu,
                    binding.layoutTimeSLot.linThu,
                    binding.layoutTimeSLot.tvThu
                )
                resetCOntrollers()
            }
        })
        binding.layoutTimeSLot.linFri.setOnClickListener(View.OnClickListener {
            selectedDay = FRIDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 4) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == FRIDAY,
                    binding.layoutTimeSLot.ivFri,
                    binding.layoutTimeSLot.linFri,
                    binding.layoutTimeSLot.tvFri
                )
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.linSat.setOnClickListener(View.OnClickListener {
            selectedDay = SATURDAY
            Log.d("day_log", "0 " + selectedDay)
            setWeekDayData()
            if (weekDayTimingsList.size > 5) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SATURDAY,
                    binding.layoutTimeSLot.ivSat,
                    binding.layoutTimeSLot.linSat,
                    binding.layoutTimeSLot.tvSat
                )
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.linSun.setOnClickListener(View.OnClickListener {
            selectedDay = SUNDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 6) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SUNDAY,
                    binding.layoutTimeSLot.ivSun,
                    binding.layoutTimeSLot.linSun,
                    binding.layoutTimeSLot.tvSun
                )
                resetCOntrollers()
            }
        })

        binding.layoutTimeSLot.tvSave.setOnClickListener(View.OnClickListener {
            if(IS_SHOWING_TIME_SLOT){
                updateTImings()
            }else{
                updateBankDetails()
            }

        })



        binding.layoutTimeSLot.linAddTImeSlot.setOnClickListener(View.OnClickListener {
            slotTimingsList.add(SlotsItem())
            slotItemsAdapter.notifyItemInserted(slotTimingsList.size - 1)
        })

        binding.linTimeSlot.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                setSelectionColor(TIME_SLOT)
                IS_SHOWING_TIME_SLOT = true
                showSectionPerSelection()
            }
        })

        binding.linFeeBankDetails.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                setSelectionColor(BANK_DETAILS)
                IS_SHOWING_TIME_SLOT = false
                showSectionPerSelection()
            }

        })

        binding.layoutAcSetup.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })

        /*binding.layoutTimeSLot.tvSave.setOnClickListener(View.OnClickListener {
            *//*getUserObject().profile.profileCompletionLevel = 7
            showToast(FourBaseCareApp.activityFromApp, "Everything done successfullY!")*//*
            updateTImings()
        })*/

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.layoutBankDetails.relContinue.setOnClickListener(View.OnClickListener {
            if(IS_SHOWING_TIME_SLOT){
                updateTImings()
            }else{
                updateBankDetails()
            }
        })

        /*binding.relContinue.setOnClickListener {
            //decideRedirection()
        }*

        /
         */
    }

    private fun setupTitle() {
        if (Constants.IS_ACC_SETUP_MODE) {
            binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.VISIBLE
            binding.layoutAcSetup.tvCurrentStep.setText("Virtual Consultation Setup")
            binding.layoutAcSetup.tvNextStep.setText("Next : Get Started")
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_6_of_6).into(binding.layoutAcSetup.ivStep)
        } else {
            binding.layoutHeader.tvTitle.setText("Virtual Consultation Setup")
            binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        return binding.root
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val updateTimeSlotData = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Timing updated successfully!")
            getDoctorTimings()
        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while updating time")
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val updateBankDetailsData = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Bank details updated successfully!")
            var userObj = getUserObject()
            userObj.profile.bankDetailsAdded = "true"
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)
            binding.layoutBankDetails.linBankForm.visibility = View.GONE
            binding.layoutBankDetails.relUpdatedMessage.visibility = View.VISIBLE
            if (Constants.IS_ACC_SETUP_MODE) {
                backToHome()
            }
        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while updating bank details")
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private fun backToHome() {
        val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
        val count: Int = fm.getBackStackEntryCount()
        Constants.IS_LIST_UPDATED = true
        for (i in 0 until count) {
            fm.popBackStackImmediate()
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

    private fun setWeekDayData() {
        for (weekday in weekDayTimingsList) {
            when (weekday.weekday) {
                "MONDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == MONDAY,
                        binding.layoutTimeSLot.ivMonday,
                        binding.layoutTimeSLot.linMonday,
                        binding.layoutTimeSLot.tvMon
                    )
                }
                "TUESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == TUESDAY,
                        binding.layoutTimeSLot.ivTue,
                        binding.layoutTimeSLot.linTue,
                        binding.layoutTimeSLot.tvTue
                    )
                }
                "WEDNESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == WEDNESSDAY,
                        binding.layoutTimeSLot.ivWed,
                        binding.layoutTimeSLot.linWed,
                        binding.layoutTimeSLot.tvWed
                    )
                }
                "THURSDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable, selectedDay == THURSDAY,
                        binding.layoutTimeSLot.ivThu,
                        binding.layoutTimeSLot.linThu,
                        binding.layoutTimeSLot.tvThu
                    )
                }
                "FRIDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == FRIDAY,
                        binding.layoutTimeSLot.ivFri,
                        binding.layoutTimeSLot.linFri,
                        binding.layoutTimeSLot.tvFri
                    )
                }
                "SATURDAY" -> {

                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SATURDAY,
                        binding.layoutTimeSLot.ivSat,
                        binding.layoutTimeSLot.linSat,
                        binding.layoutTimeSLot.tvSat
                    )
                }
                "SUNDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SUNDAY,
                        binding.layoutTimeSLot.ivSun,
                        binding.layoutTimeSLot.linSun,
                        binding.layoutTimeSLot.tvSun
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
        Log.d("day_log", "duration " + weekDay.callDuration)
        binding.layoutTimeSLot.switchAvailibility.isChecked = weekDay.isAvailable
        if (weekDay.isAvailable) {
            setTimeSLotsList(weekDay.slots)
            binding.layoutTimeSLot.edDuration.setText(""+weekDay.callDuration)
        } else {
            //binding.layoutTimeSLot.edDuration.isEnabled = false
            resetCOntrollers()
        }
    }


    private fun resetCOntrollers() {
        binding.layoutTimeSLot.edDuration.setText("")
        binding.layoutTimeSLot.switchAvailibility.isChecked = false
        initializeTimeSLotsList()
     //   binding.layoutTimeSLot.switchAvailibility.isChecked = false
        /*binding.layoutTimeSLot.etFromTime.setText("")
        binding.layoutTimeSLot.etToTime.setText("")
        binding.layoutTimeSLot.etDuration.setText("")*/
    }

    private val weekDayListObserver = androidx.lifecycle.Observer<DoctorAvailibilityResponse> { responseObserver ->
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

    private fun setSelectedDay() {
        if (!weekDayTimingsList.isNullOrEmpty()) {
            setWeekDayData()
            setDataForSelectedDay()
        }

    }

    override fun onSlotItemSelected(position: Int, item: SlotsItem, view: View) {
        if(view.id == R.id.ivOptions){
            Log.d("slot_item_log","option")
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_care_giver_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_delete -> {
                         slotTimingsList.remove(item)
                         slotItemsAdapter.notifyItemRemoved(position)
                    }
                }
                true
            }
            popupMenu.show()
        }else if(view.id == R.id.relStart){
            Log.d("slot_item_log","start")
            showFromTimePickerDialogue(item, position)
        }

        else if(view.id == R.id.relEnd){
            Log.d("slot_item_log","end")
            showToDatePickerDialogue(item, position)
        }

    }

    private fun showFromTimePickerDialogue(item: SlotsItem, position: Int) {
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
                startTimeInMilis = selectedCal.timeInMillis

                startHour = selectedHour
                startMinute = selectedMinute
                Log.d("slot_item_log","sh "+startHour)
                Log.d("slot_item_log","sm "+startMinute)
                Log.d("slot_item_log","eh "+endHour)
                Log.d("slot_item_log","em "+endMinute)

                item.startHour = startHour
                item.startMinutes = startMinute
                slotTimingsList.set(position, item)
                slotItemsAdapter.notifyItemChanged(position)
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time


        mTimePicker.setTitle("Select Start Time")
        mTimePicker.show()

    }

    private fun showToDatePickerDialogue(item: SlotsItem, position: Int) {
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
                endHour = selectedHour
                endMinute = selectedMinute

                Log.d("slot_item_log","sh "+startHour)
                Log.d("slot_item_log","sm "+startMinute)
                Log.d("slot_item_log","eh "+endHour)
                Log.d("slot_item_log","em "+endMinute)

                item.endHour = endHour
                item.endMinutes = endMinute
                slotTimingsList.set(position, item)
                slotItemsAdapter.notifyItemChanged(position)

            },
            hour,
            minute,
            true
        ) //Yes 24 hour time

        mTimePicker.setTitle("Select End Time")
        mTimePicker.show()
    }

    private fun isValidInput(): Boolean {
        if(binding.layoutTimeSLot.switchAvailibility.isChecked){
            if (getTrimmedText(binding.layoutTimeSLot.edDuration).isEmpty()) {
                showToast(FourBaseCareApp.activityFromApp, "Please select consultation duration")
                return false
            } else if (getTrimmedText(binding.layoutTimeSLot.edDuration).toInt() == 0) {
                showToast(FourBaseCareApp.activityFromApp, "Duration time cannot be 0")
                return false
            }else if(slotTimingsList.isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter atleast 1 time slot")
                return false
            }else if(!slotTimingsList.isNullOrEmpty()){
                for(slotItem in slotTimingsList){
                    if((slotItem.startHour == slotItem.endHour) && (slotItem.startMinutes == slotItem.endMinutes)){
                        showToast(FourBaseCareApp.activityFromApp, "Consultation start and end timings cannot be the same")
                        return false
                    }
                }
            }
        }else{
            return  true
        }
        return true
        }

    private fun isValidBankInput(): Boolean {

        if(!IS_BANK_OPTION_SELECTED){
            if(getTrimmedText(binding.layoutBankDetails.edUpi).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter Upi number")
                return false
            }else if(getTrimmedText(binding.layoutBankDetails.edUpiName).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter name")
                return false
            }
            return true
        }else{
            if(getTrimmedText(binding.layoutBankDetails.edAccountNo).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter account number")
                return false
            }
            else if(getTrimmedText(binding.layoutBankDetails.edName).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter name")
                return false
            }
            else if(getTrimmedText(binding.layoutBankDetails.edIFSCCode).isNullOrEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please enter ifsc code")
                return false
            }
            return true
        }
        return true
    }

    private fun validateAndSendData() {
        if (getTrimmedText(binding.layoutBankDetails.edFees).isNullOrEmpty()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter Fees")
        } else {

            //var appUser = AppUser()

            var doctorRegistrationINput = DoctorRegistrationInput()
            doctorRegistrationINput.consultationFee = getTrimmedText(binding.layoutBankDetails.edFees).toDouble()


            profileViewModel.updateProfile(
                getUserAuthToken(),
                null,
                doctorRegistrationINput,
                Constants.ROLE_DOCTOR
            )
            //gotoNextFragment()
            Log.d("update_log", "1")
        }
    }


    private val updateProfileObserver = androidx.lifecycle.Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            userObj.profile.consultationFee = getTrimmedText(binding.layoutBankDetails.edFees).toDouble()
            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            //fragmentManager!!.popBackStack()
            //decideRedirection()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

}