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
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAddOrUpdateAppointment2Binding
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Add or edit appointment fragment
 * Handles the Addition and Rescheduling of the appointment
 * @constructor Create empty Add or edit appointment fragment
 */
class AddOrEditAppointmentFragment : BaseFragment(), DoctorListingAdapter.Interaction,
    TimeSlotItemClickListener, DatePickerListener, CareGiversAdapter.Interaction {

    private lateinit var binding: FragmentAddOrUpdateAppointment2Binding
    private lateinit var addAppointmentViewModel: AppointmentViewModel
    private lateinit var root_view: View
    private lateinit var addFamilyMemberDialogue: Dialog
    private lateinit var rescheduleAppointmentConfirmationDialog: Dialog
    private lateinit var bottomAddFamilyMemberInputDIalogue: FragmentModalBottomSheet
    private lateinit var bottomAddFamilyMemberInputCOnfirmDIalogue: FragmentModalBottomSheet
    private lateinit var selectBottomDoctorDialog: FragmentModalBottomSheet
    private lateinit var inputDoctorNameDialog: Dialog
    private lateinit var doctorListingAdapter: DoctorListingAdapter
    private lateinit var hospitalList: ArrayList<HospitalDetails>
    private lateinit var doctorList: ArrayList<Doctor>
    private lateinit var voucherList: ArrayList<VoucherDetails>
    private lateinit var timeSlotGridAdapter: TimeSlotGridAdapter
    private lateinit var appointmentId: String
    private lateinit var strSelectedDate: String
    private lateinit var strSelectedDateTime: String
    private lateinit var addAppointmentInput: AddAppointmentInput
    private lateinit var doctorParticipantInput: ParticipantInput
    private lateinit var participantInputList: ArrayList<ParticipantInput>
    private lateinit var paymentId: String
    private var IS_UPDATE_MODE = false
    private var IS_CARE_GIVER_ADDED = true
    private var selectedDate: DateTime = DateTime()
    private var plusDays = 0
    private var minusDays = 0
    private var counter = 0
    private lateinit var selectedDoctor: Doctor
    private var consultationFees = 1.0 // Default value
    private var totalFees = 1.0
    private var discount = 1.0
    private var voucherId = 0
    private var VOUCHER_USED = false
    private var HAS_CARE_GIVER_DETAILS = false
    private var HAS_LOADED_DOCTORS = false
    private var careGiverName = "Add Care Giver"
    private var selectedDateTime = DateTime()
    private lateinit var careGiverList: ArrayList<CareGiverDetails>
    private lateinit var careGiverAdapter: CareGiversAdapter
    private lateinit var profileViewModel: ProfileViewModel

    private var IS_PATIENT_SELECTED = true

    private val purpose_list = arrayOf(
        "General Consultation",
        "Record Verification",
        "Surgery", "Lab", "Feedback", "Radiation", "Chemo Therapy"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        root_view = binding.root
        return binding.root
    }

    override fun init(inflater: LayoutInflater, container: ViewGroup?) {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_or_update_appointment_2, container, false
        )

        selectedDateTime = DateTime()

        binding.gvSelectTimeSlot.isExpanded = true

        setupVM()

        initAddAppointmentObj()

        setupObservers()

        setupSpinners()

        getCareTakersFromServer()

        setupClickListeners()

        //showHideBottomButton()

        paymentId = getRandomString()

    }

    private fun initAddAppointmentObj() {
        addAppointmentInput = AddAppointmentInput()
        addAppointmentInput.guestId = 0
        addAppointmentInput.patientId = getUserIdd()
        doctorList = ArrayList()

        if(arguments != null && arguments!!.containsKey(Constants.DOCTOR_DATA)){
            IS_UPDATE_MODE = false
            selectedDoctor = arguments!!.getParcelable(Constants.DOCTOR_DATA)!!
            Log.d("selected_dr_log","id "+selectedDoctor.doctorId)
            Log.d("selected_dr_log","fees "+selectedDoctor.consultationFee)
            addAppointmentInput.doctorId = selectedDoctor.doctorId
            binding.etDoctor.setText(selectedDoctor.firstName)
            var strDate = DateFormat.format("dd/MM/yyyy", Date())
            binding.tvDate.setText(strDate)
            processSelectedDoctor()

        } else if (arguments != null && arguments!!.containsKey(Constants.APPOINTMENT_DETAILS)) {
            Log.d("selected_dr_log","Doctor not found")
            showHideProgress(true, binding.layoutProgress.frProgress)
            binding.tvContinue.setText("Reschedule")
            binding.tvTitle.setText("Reschedule appointment")
            var strDate = DateFormat.format("dd/MM/yyyy", Date())
            binding.tvDate.setText(strDate)
            var appointmentDetails: AppointmentDetails =
                arguments!!.getParcelable(Constants.APPOINTMENT_DETAILS)!!
            appointmentId = arguments!!.getString(Constants.APPOINTMENT_ID).toString()

            if(!::participantInputList.isInitialized){
                participantInputList = ArrayList<ParticipantInput>()
            }
            var doctorId = 0
            var patientId = 1

            for (paritcipant in appointmentDetails.participants) {
                var participantInput = ParticipantInput()
                participantInput.userId = paritcipant.userId
                participantInput.name = paritcipant.name
                participantInput.role = paritcipant.role
                participantInputList.add(participantInput)

                Log.d("reschedule_log", "id " + paritcipant.userId)
                if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                    doctorId = paritcipant.userId
                    //addAppointmentInput.doctorId = doctorId
                    Log.d("reschedule_log", "doctor id " + paritcipant.userId)
                    Log.d("reschedule_log", "doctor name " + paritcipant.name)
                } else if (paritcipant.role.equals(Constants.ROLE_PATIENT, true)) {
                    patientId = paritcipant.userId
                    Log.d("reschedule_log", "patient id " + paritcipant.userId)
                    Log.d("reschedule_log", "patient name " + paritcipant.name)
                }
            }


            IS_UPDATE_MODE = true

            addAppointmentInput.participantInputList = participantInputList

            addAppointmentInput.patientId = patientId
            Log.d("reschedule_log", "patient id " + addAppointmentInput.patientId)


            addAppointmentInput.doctorId = doctorId
            Log.d("reschedule_log", "doctor id " + addAppointmentInput.doctorId)

            addAppointmentInput.guestId = 0
            Log.d("reschedule_log", "guest id " + addAppointmentInput.guestId)

            addAppointmentInput.scheduledTime = appointmentDetails.scheduledTime
            Log.d("reschedule_log", "schedule time " + addAppointmentInput.scheduledTime)

            addAppointmentInput.notes = appointmentDetails.notes
            binding.etNotes.setText(addAppointmentInput.notes)

        } else {
            IS_UPDATE_MODE = false
        }

        showPatient()
    }

    private fun showPatient() {
        if (getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER)) {
            Log.d("add_patient", "1")
            if (getPatientObjectByCG() != null) {
                Log.d("add_patient", "2")
                var IS_FOUND = false
                if(IS_UPDATE_MODE && !participantInputList.isNullOrEmpty()){
                    IS_FOUND = false
                    Log.d("added_patient", "0")
                    for(participantObj in participantInputList){
                        Log.d("added_patient", ""+participantObj.userId)
                        Log.d("added_patient", "Patient id "+getPatientObjectByCG()?.id)
                        if(participantObj.userId == getPatientObjectByCG()?.id){
                            Log.d("added_patient", "FOund")
                            IS_FOUND = true
                            binding.tvAdd.text = "Added"
                            binding.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.green_color_button))
                            binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_cicrular_green_tick))
                        }
                    }
                }

                if(IS_UPDATE_MODE && !IS_FOUND) binding.linAddPatient.visibility = View.GONE // This is because patient side is not updated
                else binding.linAddPatient.visibility = View.VISIBLE

                binding.tvPatientName.setText(getPatientObjectByCG()?.firstName ?: "")
                Glide.with(FourBaseCareApp.activityFromApp)
                    .load(getPatientObjectByCG()?.dpLink)
                    .placeholder(R.drawable.ic_user_image)
                    .circleCrop()
                    .into(binding.ivPatientImage)
                binding.tvPatientRelation.setText("")
                binding.linAdd.setOnClickListener(View.OnClickListener {
                    if (!isDoubleClick() && !IS_UPDATE_MODE) {
                        if (!::participantInputList.isInitialized) {
                            participantInputList = ArrayList<ParticipantInput>()
                        }
                        if (IS_PATIENT_SELECTED) {
                            var participantInput = ParticipantInput()
                            participantInput.userId = getPatientObjectByCG()!!.id
                            participantInput.name = getPatientObjectByCG()!!.firstName
                            participantInput.role = Constants.ROLE_PATIENT
                            participantInputList.add(participantInput)
                            binding.tvAdd.text = "Added"
                            binding.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.green_color_button))
                            binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_cicrular_green_tick))
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                participantInputList.removeIf { participant -> participant.userId === getPatientObjectByCG()!!.id }
                            }
                            binding.tvAdd.text = "Add"
                            binding.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.font_light_blue))
                            binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_add_box_blue))

                        }
                        IS_PATIENT_SELECTED = !IS_PATIENT_SELECTED
                    }
                })
            } else {
                Log.d("add_patient", "3")
            }
        }
    }

    private fun isValidInput(): Boolean {
        if (addAppointmentInput.doctorId == null || addAppointmentInput.doctorId == 0) {
            showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_select_doctor))
            return false
        } else if (addAppointmentInput.scheduledTime.isNullOrBlank()) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_select_appointment_time)
            )
            return false
        }
        return true
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
                    var strDate = DateFormat.format("dd/MM/yyyy", dtDob)
                    val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
                    val strFormattedDate: String = fmt.print(dtDob)
                    getTimeSLotsListingFromServer(strFormattedDate)
                    binding.tvDate.setText(strDate)

                }, yy, mm, dd
            )

        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun setupClickListeners() {

        binding.linDate.setOnClickListener(View.OnClickListener {
            showDatePickerDialogue()
        })
        binding.ivBack.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) fragmentManager?.popBackStack()
        })

        binding.linContinue.setOnClickListener(View.OnClickListener {

            if (!isDoubleClick() && isValidInput()) {
                val oneHourBeforeDate = Date(System.currentTimeMillis() + 3600 * 1000)
                Log.d("one_hr_log","date 1 hr "+oneHourBeforeDate)
                Log.d("one_hr_log", "schedule date "+CommonMethods.getDate(addAppointmentInput.scheduledTime))
                if (Date().after(CommonMethods.getDate(addAppointmentInput.scheduledTime))) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        "You cannot book an appointment as time has already been passed"
                    )
                }else if(oneHourBeforeDate.after(CommonMethods.getDate(addAppointmentInput.scheduledTime))) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        "You can book appointment atleast one hour before"
                    )
                } else{
                    if (IS_UPDATE_MODE) {
                        addAppointmentInput.notes = getTrimmedText(binding.etNotes)
                        showReschedulingConfirmDialogue()
                    } else {
                        openAppointmentDetails()
                    }
                }


            }
        })

        /*binding.tvAddNewRecord.setOnClickListener(View.OnClickListener {
            if (isPermissionsAllowed()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    MedicalRecordsCameraOrGalleryFragment(), this, false
                )
            } else askForPermissions()
        })*/
    }

    private fun getCareTakersFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                getDoctorListingFromServer()
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun addAppointmentToTheServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("partcipant_log","addAppointmenttoServer")
            if(::participantInputList.isInitialized){
                participantInputList = ArrayList<ParticipantInput>()
            }

            var participantInputObj = ParticipantInput()
            participantInputObj.userId = getUserObject().userIdd
            participantInputObj.name = getUserObject().firstName
            participantInputObj.role = getUserObject().role

            participantInputList.add(participantInputObj)
            participantInputList.add(doctorParticipantInput)

            addAppointmentInput.participantInputList = participantInputList
            addAppointmentInput.notes = getTrimmedText(binding.etNotes)

            /*addAppointmentViewModel.callAddAppointment(
                addAppointmentInput, getUserObject().authToken
            )*/
            Log.d("partcipant_log","appointment added with "+participantInputList.size+" members")
        }
    }

    private fun rescheduleAppointmentToTheServer() {
        if (Date().after(CommonMethods.getDate(addAppointmentInput.scheduledTime))) {
            showToast(
                FourBaseCareApp.activityFromApp,
                "You cannot reschedule an appointment to this time as time has already been passed"
            )
        } else {
            addAppointmentViewModel.callRescheduleAppointment(
                addAppointmentInput,
                getUserAuthToken(), appointmentId
            )
        }
    }

    private fun addGuestToAppointment(emailId: String, phoneNumber: String, name: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            if(!IS_CARE_GIVER_ADDED){
                         careGiverName = name

                     /*    if(name.isNullOrEmpty()){
                             showToast(FourBaseCareApp.activityFromApp,"Please enter name")
                             return
                         }else if(phoneNumber.isNullOrEmpty()){
                             showToast(FourBaseCareApp.activityFromApp,"Please enter email")
                             return
                         }
                         else if(!phoneNumber.isNullOrEmpty() && phoneNumber.length != 10){
                             showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_invalid_mobile_number))
                             return
                         }else if(getUserObject() !=null && !getUserObject().phoneNumber.isNullOrEmpty()
                             && phoneNumber.equals(getUserObject().phoneNumber)){
                             showToast(FourBaseCareApp.activityFromApp,"Your mobile number cannot be added as a care giver number")
                             return
                         }
                         else if(emailId.isNullOrEmpty()){
                             showToast(FourBaseCareApp.activityFromApp,getString(R.string.validation_enter_email))
                             return
                         }else if (!CommonMethods.isValidEmail(getTrimmedText(etEmail))) {
                             showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_enter_valid_email))
                             return
                         }else{
                             var addGuestInput = AddGuestInput()
                             addGuestInput.emailAddress = emailId
                             addGuestInput.phoneNumber = phoneNumber
                             addGuestInput.isAddCareGiver = IS_CARE_GIVER_ADDED
                             addGuestInput.fullName = name
                             addGuestInput.patientName = FourBaseCareApp.sharedPreferences.getString(
                                 Constants.PREF_USER_NAME,
                                 ""
                             )*/
/*
                             addAppointmentViewModel.callAddGuest(
                                 addGuestInput,
                                 getUserAuthToken(), appointmentId
                             )
*/
                             bottomAddFamilyMemberInputDIalogue.dismiss()
                         }
            }else{
                /*var addGuestInput = AddGuestInput()
                addGuestInput.emailAddress = emailId
                addGuestInput.phoneNumber = phoneNumber
                addGuestInput.isAddCareGiver = IS_CARE_GIVER_ADDED
                addGuestInput.fullName = name
                addGuestInput.patientName = FourBaseCareApp.sharedPreferences.getString(
                    Constants.PREF_USER_NAME,
                    ""
                )*/

                /*addAppointmentViewModel.callAddGuest(
                    addGuestInput,
                    getUserAuthToken(), appointmentId
                )*/
                bottomAddFamilyMemberInputDIalogue.dismiss()
            }



    }

    private fun getDoctorListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetMyDoctorListing(getUserObject().role,false,
                getUserAuthToken(), "" + getUserObject().userIdd
            )

           // addAppointmentViewModel.callVoucherList(getUserObject().authToken)

            profileViewModel.getCareTakerDetails(getUserAuthToken())
        }
    }

    private fun getTimeSLotsListingFromServer(selectedDate: String) {
        Log.d("selected_date", "Date to server " + selectedDate)
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            if (addAppointmentInput != null && addAppointmentInput.doctorId != null) {
                strSelectedDate = selectedDate
                var timeSlotsInput = TimeSlotsInput()
                timeSlotsInput.setSelectedDate(selectedDate)

                addAppointmentViewModel.callGetTimeSlots(
                    getUserAuthToken(), "" + addAppointmentInput.doctorId, timeSlotsInput
                )
            } else {
                CommonMethods.run {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.select_doctor_msg)
                    )
                }
            }

        }
    }

    private fun updatePaymentStatus() {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            var paymentInput = PaymentInput()
            paymentInput.amount = totalFees.toInt()
            paymentInput.currency = "INR"
            paymentInput.paymentMode = "CASH"
            paymentInput.paymentTime =
                CommonMethods.getDateFromTimeStamp(System.currentTimeMillis())
            paymentInput.transactionId = paymentId
            paymentInput.paymentDone = true
            paymentInput.voucherUsed = VOUCHER_USED
            paymentInput.platformCharges = 0
            if (VOUCHER_USED) {
                paymentInput.voucherId = voucherId
                paymentInput.discountAmount = discount.toInt()
            }

            addAppointmentViewModel.updatePaymentStatus(
                getUserAuthToken(),
                appointmentId,
                paymentInput
            )

        }

    }


    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

        addAppointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)
    }

    private fun setupSpinners() {

    /*    binding.etDoctor.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (IS_UPDATE_MODE) {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        "You can not change doctor in reschedule mode!"
                    )
                } else {
                    if(HAS_LOADED_DOCTORS)
                    openDoctorListScreen()
                    else
                    showToast(FourBaseCareApp.activityFromApp, "Please wait for few seconds while we are loading doctors!")
                    //showSelectDoctorDialogue()
                }

            }

        })*/

        val spinner_purpose = ArrayAdapter(
            FourBaseCareApp.activityFromApp,
            android.R.layout.simple_spinner_dropdown_item,
            purpose_list
        )


    }

    private fun openDoctorListScreen() {
        var bundle = Bundle()
        bundle.putParcelableArrayList(Constants.DOCTOR_LIST, doctorList)
        var doctorListingFragment = DoctorListingFragment()
        doctorListingFragment.arguments = bundle

        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp,
            doctorListingFragment,
            this,
            false,
            Constants.FRAGMENT_SELECT_DOCTOR_RESULT
        )
    }

    private fun openVoucherScreen() {
        Log.d("voucher_log", "opended voucher screen")
        var bundle = Bundle()
        if (!::voucherList.isInitialized){
           voucherList = ArrayList()
        }
        bundle.putParcelableArrayList(Constants.VOUCHER_LIST, voucherList)
        bundle.putDouble("FEES", selectedDoctor.consultationFee)
        bundle.putString("DATE_TIME",strSelectedDateTime)
        bundle.putParcelable("DOCTOR",selectedDoctor)
        bundle.putString("NOTES",getTrimmedText(binding.etNotes))
     /*   var voucherListFragment = VoucherListFragment()
        voucherListFragment.arguments = bundle

        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            voucherListFragment, this, false, Constants.FRAGMENT_SELECT_VOUCHER_RESULT
        )*/
    }

    private fun openAppointmentDetails() {
        addAppointmentInput.notes = getTrimmedText(binding.etNotes)

        if(!::participantInputList.isInitialized){
            participantInputList = ArrayList()
        }
        var participantInputObj = ParticipantInput()
        participantInputObj.userId = getUserObject().userIdd
        participantInputObj.name = getUserObject().firstName
        participantInputObj.role = getUserObject().role

        participantInputList.add(participantInputObj)
        participantInputList.add(doctorParticipantInput)

        addAppointmentInput.participantInputList = participantInputList

        var bundle = Bundle()
        bundle.putDouble("FEES", selectedDoctor.consultationFee)
        bundle.putString("DATE_TIME",strSelectedDateTime)
        bundle.putString("NOTES",getTrimmedText(binding.etNotes))
        bundle.putParcelable("DOCTOR",selectedDoctor)
        bundle.putParcelable("APPOINTMENT_INPUT", addAppointmentInput)
        bundle.putString(Constants.SOURCE,"add_appointment")

        Log.d("partcipant_log","participants size "+addAppointmentInput.participantInputList.size)

        var appointmentDetailsFragment = AppointmentDetailsFragment()
        appointmentDetailsFragment.arguments = bundle

        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            appointmentDetailsFragment, this, false, Constants.FRAGMENT_SELECT_VOUCHER_RESULT
        )
    }


    private fun showBottomAddMemberCOnfirmDialogue() {

        /*val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_add_member_confirmation, null)

        bottomAddFamilyMemberInputCOnfirmDIalogue = FragmentModalBottomSheet(myView)
        bottomAddFamilyMemberInputCOnfirmDIalogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val ivNo: ImageView = myView.findViewById(R.id.ivNo)
        val tvNo: TextView = myView.findViewById(R.id.tvNo)

        val ivYes: ImageView = myView.findViewById(R.id.ivYes)
        val tvYes: TextView = myView.findViewById(R.id.tvYes)

        ivYes.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                showBottomAddMemberInputDialogue()
                bottomAddFamilyMemberInputCOnfirmDIalogue.dismiss()
            }

        })

        tvYes.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                showBottomAddMemberInputDialogue()
                bottomAddFamilyMemberInputCOnfirmDIalogue.dismiss()
            }

        })

        ivNo.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                gotoCOnfirmationScreen()
                bottomAddFamilyMemberInputCOnfirmDIalogue.dismiss()
            }

        })

        tvNo.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                gotoCOnfirmationScreen()
                bottomAddFamilyMemberInputCOnfirmDIalogue.dismiss()
            }

        })*/
    }

    private fun gotoCOnfirmationScreen() {
        var bundle = Bundle()
        bundle.putString("APPOINTMENT_ID", appointmentId)
        bundle.putString("DOCTOR_NAME", getTrimmedText(binding.etDoctor))
        bundle.putString("DATE_TIME", addAppointmentInput.scheduledTime)
        bundle.putParcelable("DOCTOR",selectedDoctor)
        bundle.putString("NOTES", addAppointmentInput.notes)
        var appointmentFragment = AppointmentConfirmedFragment()
        appointmentFragment.arguments = bundle
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            appointmentFragment, this, false
        )
    }


    private fun showBottomAddMemberInputDialogue() {
        /*val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView: View = li.inflate(R.layout.bottom_dialogue_add_member, null)

        bottomAddFamilyMemberInputDIalogue = FragmentModalBottomSheet(myView)
        bottomAddFamilyMemberInputDIalogue.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val linAddCareGiver: LinearLayout = myView.findViewById(R.id.linAddCareGiver)
        val linAddOthers: LinearLayout = myView.findViewById(R.id.linAddOthers)
        val tvCancel: TextView = myView.findViewById(R.id.tvCancel)
        val ivAddCareGiver: ImageView = myView.findViewById(R.id.ivAddCareTaker)
        val ivAddOther: ImageView = myView.findViewById(R.id.ivAddOthers)
        val linAddNewMemberInput: LinearLayout = myView.findViewById(R.id.linAddNewMemberInput)
        val relContinue: RelativeLayout = myView.findViewById(R.id.relContinue)
        val tvContinue: TextView = myView.findViewById(R.id.tvCOntinue)
        val edName: EditText = myView.findViewById(R.id.edName)
        val edMobile: EditText = myView.findViewById(R.id.edMobileNumber)
        val edEmail: EditText = myView.findViewById(R.id.edEmail)
        val tvAddCareGiver: TextView = myView.findViewById(R.id.tvAddCareGIver)
        val linRadioCOntainer:LinearLayout = myView.findViewById(R.id.linRadioCOntainer)

        if(HAS_CARE_GIVER_DETAILS && !careGiverName.isNullOrEmpty()){
            tvAddCareGiver.setText("Add "+careGiverName)
        }else{
            linRadioCOntainer.visibility = View.GONE
            linAddNewMemberInput.visibility = View.VISIBLE
            IS_CARE_GIVER_ADDED = false
        }

        relContinue.setOnClickListener(View.OnClickListener {
            addGuestToAppointment(
                getTrimmedText(edEmail),
                getTrimmedText(edMobile),
                getTrimmedText(edName)
            )
            //bottomAddFamilyMemberInputDIalogue.dismiss()


        })

        tvContinue.setOnClickListener(View.OnClickListener {
            addGuestToAppointment(
                getTrimmedText(edEmail),
                getTrimmedText(edMobile),
                getTrimmedText(edName)
            )
            //bottomAddFamilyMemberInputDIalogue.dismiss()

        })


        linAddCareGiver.setOnClickListener(View.OnClickListener {
            ivAddCareGiver.setImageDrawable(
                ContextCompat.getDrawable(
                    FourBaseCareApp.activityFromApp,
                    R.drawable.ic_radio_checked_purple
                )
            )
            ivAddOther.setImageDrawable(
                ContextCompat.getDrawable(
                    FourBaseCareApp.activityFromApp,
                    R.drawable.ic_radio_unchecked
                )
            )
            linAddNewMemberInput.visibility = View.GONE
            IS_CARE_GIVER_ADDED = true
        })

        linAddOthers.setOnClickListener(View.OnClickListener {
            ivAddCareGiver.setImageDrawable(
                ContextCompat.getDrawable(
                    FourBaseCareApp.activityFromApp,
                    R.drawable.ic_radio_unchecked
                )
            )
            ivAddOther.setImageDrawable(
                ContextCompat.getDrawable(
                    FourBaseCareApp.activityFromApp,
                    R.drawable.ic_radio_checked_purple
                )
            )
            linAddNewMemberInput.visibility = View.VISIBLE
            IS_CARE_GIVER_ADDED = false
        })

        tvCancel.setOnClickListener(View.OnClickListener {
            gotoCOnfirmationScreen()
            bottomAddFamilyMemberInputDIalogue.dismiss()
        })
*/
    }


    private fun showFamilymemberAdded() {
        addFamilyMemberDialogue = Dialog(FourBaseCareApp.activityFromApp)
        addFamilyMemberDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        addFamilyMemberDialogue.setContentView(R.layout.dialogue_cancel_appointment)
        addFamilyMemberDialogue.setCancelable(false)

        val linNo: LinearLayout = addFamilyMemberDialogue.findViewById(R.id.linNo)
        linNo.visibility = View.GONE

        val ivLogo: ImageView = addFamilyMemberDialogue.findViewById(R.id.ivLogo)
        ivLogo.setImageDrawable(FourBaseCareApp.activityFromApp.getDrawable(R.drawable.logo_email_sent))

        val tvTitleText: TextView = addFamilyMemberDialogue.findViewById(R.id.tvTitleText)
        tvTitleText.text = "An SMS has been sent to "+careGiverName+" with the appointment link. Please be on time."

        val btnNo: TextView = addFamilyMemberDialogue.findViewById(R.id.btnNo)
        //btnNo.setText(getString(R.string.no))

        val btnYes: TextView = addFamilyMemberDialogue.findViewById(R.id.btnYes)
        btnYes.text = "Okay"

        btnYes.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                gotoCOnfirmationScreen()
                addFamilyMemberDialogue.dismiss()

            }
        })

        addFamilyMemberDialogue.show()
    }

    private fun showAddDoctorInputDialogue() {
        inputDoctorNameDialog = Dialog(FourBaseCareApp.activityFromApp)
        inputDoctorNameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        inputDoctorNameDialog.setContentView(R.layout.dialogue_add_doctor_name)
        inputDoctorNameDialog.setCancelable(false)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(inputDoctorNameDialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = inputDoctorNameDialog.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        inputDoctorNameDialog.window?.attributes = lp
        //deleteConfirmationDialogue.window?.setGravity(Gravity.BOTTOM)
        inputDoctorNameDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        inputDoctorNameDialog.show()
    }

    override fun onItemSelected(position: Int, doctor: Doctor, view: View) {
        selectBottomDoctorDialog.dismiss()
        binding.etDoctor.setText(doctor.firstName)
        addAppointmentInput.doctorId = doctor.doctorId
        doctorParticipantInput = ParticipantInput()
        doctorParticipantInput.userId = doctor.doctorId
        doctorParticipantInput.role = "DOCTOR"
        doctorParticipantInput.name = doctor.firstName


        val sdf = SimpleDateFormat("yyyy-MM-dd")
        getTimeSLotsListingFromServer(sdf.format(Date()))

    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Constants.IS_FROM_ADD_APPOINTMENT = true

                    /*CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        MedicalRecordsCameraOrGalleryFragment(), this, false
                    )*/
                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle(getString(R.string.msg_permission_denied))
            .setMessage(getString(R.string.msg_permission_from_settings))
            .setPositiveButton(getString(R.string.title_app_settings),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        FourBaseCareApp.activityFromApp.packageName,
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onTimeSlotItemClick(timeSlotData: TimeSlot?) {
        addAppointmentInput.duration = 30

        binding.tvTImeSlot.setText(timeSlotData!!.getTime())

        /*2021-02-26T11:11:30.73*/
        val split: Array<String> = timeSlotData!!.getTime().split(" : ".toRegex()).toTypedArray()
        addAppointmentInput.scheduledTime = strSelectedDate + "T" + split.get(0) + ":00.000Z"
        strSelectedDateTime = strSelectedDate + "T" + split.get(0)+":00"


        Log.d("time_slot_log", addAppointmentInput.scheduledTime)
        /*CommonMethods.showToast(
            FourBaseCareApp.activityFromApp,
            "Selected " + addAppointmentInput.scheduledTime
        )*/
    }

    private fun showHideBottomButton() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "dsdsdsd " + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden dsdws")
                        binding.linContinue.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible hfssd")
                        binding.linContinue.setVisibility(View.VISIBLE)
                    }
                }
            })
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("payment_log", "6.1")
        Log.d("payment_log", "result code " + resultCode)
        Log.d("payment_log", "request code " + requestCode)

        if (data == null) {
            Log.d("payment_log", "data is null")
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.FRAGMENT_SELECT_VOUCHER_RESULT) {
                if (data != null) {
                    totalFees = data.getDoubleExtra("FEES", 0.0)
                    voucherId = data.getIntExtra("VOUCHER_ID", 0)
                    discount = data.getDoubleExtra("DISCOUNT_AMOUNT", 1.0)
                    VOUCHER_USED =  true

                }
                if(totalFees < 1.0){
                    paymentId = getRandomString()
                    CommonMethods.showLog("payment_log", "payment id " + paymentId)
                    addAppointmentToTheServer()
                }else{
                    openPaymentScreen()
                }


            } else if (requestCode == Constants.FRAGMENT_RESULT) {
                Log.d("payment_log", "6")
                CommonMethods.showLog("payment_log", "onactivity result catch")
                if (!data?.getStringExtra(Constants.TRANSACTION_ID).isNullOrEmpty()) {
                    var transactionId: String = data?.getStringExtra(Constants.TRANSACTION_ID)!!

                    if (transactionId != null) {
                        paymentId = transactionId

                        CommonMethods.showLog("payment_log", "payment id " + paymentId)
                        addAppointmentToTheServer()
                    }
                } else {
                    Toast.makeText(
                        FourBaseCareApp.activityFromApp,
                        "Payment Error!",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            } else if (requestCode == Constants.FRAGMENT_SELECT_DOCTOR_RESULT) {
                var doctor: Doctor = data?.getParcelableExtra(Constants.DOCTOR_DATA)!!

                if (doctor != null) {
                    selectedDoctor = doctor
                    processSelectedDoctor()
                }
            }
        }


    }

    private fun processSelectedDoctor() {
        binding.etDoctor.setText(selectedDoctor.firstName)
        addAppointmentInput.doctorId = selectedDoctor.doctorId
        doctorParticipantInput = ParticipantInput()
        doctorParticipantInput.userId = selectedDoctor.doctorId
        doctorParticipantInput.role = Constants.ROLE_DOCTOR
        doctorParticipantInput.name = selectedDoctor.firstName
        consultationFees = selectedDoctor.consultationFee
        Log.d("payment_log", "0.0.1 " + consultationFees)


        val sdf = SimpleDateFormat("yyyy-MM-dd")
        getTimeSLotsListingFromServer(sdf.format(Date()))
        addAppointmentViewModel.callVoucherList(
            getUserObject().authToken,
            "" + selectedDoctor.doctorId
        )
    }


    //observers

    private fun setupObservers() {
        addAppointmentViewModel.rescheduleResponseData.observe(
            this,
            rescheduleResponseResponseObserver
        )
        profileViewModel.careGiverResonseData.observe(this, getCareTakerObserver)
        addAppointmentViewModel.baseResponseData.observe(this, addGuestResponseObserver)
        addAppointmentViewModel.paymentResponseData.observe(this, paymentResponseObserver)
        profileViewModel.doctorListResponse.observe(this, doctorListResponseObserver)
        addAppointmentViewModel.voucherListResponseData.observe(this, voucherListResponseObserver)
        addAppointmentViewModel.timeSlotsListResponse.observe(this, timeSlotsResponnseObserver)
        addAppointmentViewModel.responseData.observe(this, responseObserver)
        addAppointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        addAppointmentViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val getCareTakerObserver = androidx.lifecycle.Observer<CareGiverResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if (responseObserver != null) {
            if (responseObserver.isSuccess) {
                HAS_CARE_GIVER_DETAILS = true
                //careGiverName = responseObserver.careGiverDetails.get(0).name
                if (responseObserver.isSuccess) {
                    careGiverList = ArrayList()
                    careGiverList.addAll(responseObserver.careGiverDetails)
                    setCGRecyclerView(careGiverList)

                }
            }
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val voucherListResponseObserver = Observer<VoucherListResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        Log.d("api_log", "doctor list Result " + responseObserver.isSuccess)
        if (responseObserver.payLoad == null) {
            Log.d("api_log", "doctor list null ")
        } else {
            Log.d("api_log", "doctor list size " + responseObserver.payLoad.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.isSuccess) {
            voucherList = ArrayList()
            voucherList.addAll(responseObserver.payLoad)
        }


    }


    private val doctorListResponseObserver = Observer<DoctorsListingResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        Log.d("api_log", "doctor list Result " + responseObserver.isSuccess)
        if (responseObserver.doctorList == null) {
            Log.d("api_log", "doctor list null ")
        } else {
            Log.d("api_log", "doctor list size " + responseObserver.doctorList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()

        HAS_LOADED_DOCTORS = true
        if (responseObserver.isSuccess) {
            doctorList = ArrayList()
            doctorList.addAll(responseObserver.doctorList)
        }

        if (IS_UPDATE_MODE) {
            Log.d("reschedule_log", "doctor list obtained")
            Log.d("reschedule_log", "current doctor id " + addAppointmentInput.doctorId)
            for (doctor in doctorList) {
                Log.d("reschedule_log", "doctor id  " + doctor.doctorId)
                Log.d("reschedule_log", "doctor name  " + doctor.firstName)

                if (addAppointmentInput.doctorId == doctor.doctorId) {
                    selectedDoctor = doctor
                    Log.d("reschedule_log", "doctor name matched " + doctor.firstName)
                    binding.etDoctor.setText(doctor.firstName)
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    getTimeSLotsListingFromServer(sdf.format(Date()))
                }
            }
        }

    }

    private val timeSlotsResponnseObserver =
        Observer<TimeSlotsListingResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            Log.d("api_log", "Time slots Result " + responseObserver.isSuccess)
            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver.isSuccess) {
                //binding.linDateTimeContainer.visibility = View.VISIBLE
                binding.gvSelectTimeSlot.visibility = View.VISIBLE
                //binding.tvTimeSlotsTitle.visibility = View.VISIBLE
                binding.gvSelectTimeSlot.apply {
                    //layoutManager = LinearLayoutManager(activity)

                    timeSlotGridAdapter = TimeSlotGridAdapter(
                        FourBaseCareApp.activityFromApp,
                        this@AddOrEditAppointmentFragment,
                        responseObserver.timeSlotList
                    )
                    adapter = timeSlotGridAdapter
                    //timeSlotGridAdapter.submitList(sampleDataList)
                }
            } else {
                //binding.linDateTimeContainer.visibility = View.GONE
                binding.gvSelectTimeSlot.visibility = View.GONE
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "No timeslots available for this day!"
                )
            }


        }

    private val responseObserver = Observer<AddAppointmentResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            appointmentId = "" + responseObserver.appointmentDetails.id
            Log.d("payment_log", "0.1 " + consultationFees)
            /*if(totalFees < 1.0){
                updatePaymentStatus()
            }else{
                openPaymentScreen()
            }*/
            updatePaymentStatus()


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding appointment"
            )
        }
    }

    private fun openPaymentScreen() {
        val intent = Intent(FourBaseCareApp.activityFromApp, PaymentActivity::class.java)
        intent.putExtra("FEES", totalFees)
        startActivityForResult(intent, Constants.FRAGMENT_RESULT)
    }


    private val addGuestResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            showFamilymemberAdded()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding appointment"
            )
        }


    }

    private val paymentResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            //CommonMethods.showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
            /*CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                AppointmentConfirmedFragment(), this, false
            )*/

            showBottomAddMemberCOnfirmDialogue()


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding appointment"
            )
        }


    }

    private val rescheduleResponseResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, responseObserver.message)
            gotoCOnfirmationScreen()
            //openAppointmentDetails()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                responseObserver.message
            )
        }


    }

    private val isViewLoadingObserver = Observer<Boolean> { isLoading ->
        Log.d("login_log", "is_loading is " + isLoading)
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    override fun onDateSelected(dateSelected: DateTime?) {

        /*val sdf = DateTimeFormat("yyyy-MM-dd")
        val selectedDateStr: String = sdf("yyyy-MM-dd", dateSelected).toString()*/
        if (dateSelected != null) {
            selectedDate = dateSelected
            selectedDateTime = dateSelected
            Log.d("counter_log","on selected "+dateSelected)
            val fmt: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
            val strFormattedDate: String = fmt.print(dateSelected)
            getTimeSLotsListingFromServer(strFormattedDate)
            addAppointmentInput.scheduledTime = ""
        }

    }

    private fun showReschedulingConfirmDialogue() {
        rescheduleAppointmentConfirmationDialog = Dialog(FourBaseCareApp.activityFromApp)
        rescheduleAppointmentConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        rescheduleAppointmentConfirmationDialog.setContentView(R.layout.dialogue_cancel_appointment_new)
        val btnYes: TextView = rescheduleAppointmentConfirmationDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = rescheduleAppointmentConfirmationDialog.findViewById(R.id.btnNo)
        val tvTitleText: TextView =
            rescheduleAppointmentConfirmationDialog.findViewById(R.id.tvTitleText)

        tvTitleText.setText(
            "Are you sure you want to reschedule an appointment at\n" + CommonMethods.changeCOmmentDateTimeFormat(
                addAppointmentInput.scheduledTime
            )
        )

        btnYes.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                rescheduleAppointmentConfirmationDialog.dismiss()
                //showAppointmentCancelledDialogue()
                rescheduleAppointmentToTheServer()

            }
        })

        btnNo.setOnClickListener(View.OnClickListener {
            rescheduleAppointmentConfirmationDialog.dismiss()
        })


        rescheduleAppointmentConfirmationDialog.show()
    }

    private fun setCGRecyclerView(list: List<CareGiverDetails>?) {

        val arrayList = list?.let { ArrayList<CareGiverDetails>(it) }


        if(arrayList != null && arrayList.size > 0 && !IS_UPDATE_MODE ) {
            Log.d("cg_size","size "+arrayList.size)
            binding.tvAddCareGiver.visibility = View.VISIBLE
            binding.rvCaregivers.apply {
                layoutManager = LinearLayoutManager(activity)
                careGiverAdapter = CareGiversAdapter(arrayList!!, this@AddOrEditAppointmentFragment)
                adapter = careGiverAdapter
                careGiverAdapter.submitList(arrayList)
            }
        }else{
            binding.tvAddCareGiver.visibility = View.GONE
            Log.d("cg_size","size 0")
        }
    }

    override fun onCGSelected(position: Int, item: CareGiverDetails, view: View) {
        Log.d("interaction_lg","1")
        var careGiverDetails = careGiverList.get(position)
        careGiverDetails.isSelected = !careGiverDetails.isSelected
        careGiverList.set(position,careGiverDetails)
        setCGRecyclerView(careGiverList)

        if(!::participantInputList.isInitialized){
            participantInputList = ArrayList<ParticipantInput>()
        }

        if(careGiverDetails.isSelected){
            var participantInput = ParticipantInput()
            participantInput.userId = careGiverDetails.appUserId
            participantInput.name = careGiverDetails.name
            participantInput.role = Constants.ROLE_PATIENT_CARE_GIVER
            participantInputList.add(participantInput)
            Log.d("partcipant_log","cg added. Id "+careGiverDetails.appUserId)
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                participantInputList.removeIf { participant -> participant.userId === careGiverDetails.appUserId }
                Log.d("partcipant_log","cg removed. Id "+careGiverDetails.appUserId)
            }

        }

    }


}