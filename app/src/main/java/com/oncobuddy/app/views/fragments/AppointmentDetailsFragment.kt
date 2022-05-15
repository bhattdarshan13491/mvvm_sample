package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAppointmentDetailsBinding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ChatVMInjection
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.GuestUserId
import com.oncobuddy.app.models.pojo.appointments.AddAppointmentResponse
import com.oncobuddy.app.models.pojo.appointments.ParticipantDetails
import com.oncobuddy.app.models.pojo.appointments.ParticipantInput
import com.oncobuddy.app.models.pojo.appointments.input.AddAppointmentInput
import com.oncobuddy.app.models.pojo.appointments.input.AddGuestInput
import com.oncobuddy.app.models.pojo.appointments.input.PaymentInput
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverDetails
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.guest_details.GuestDetailsResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.activities.PaymentActivity
import com.oncobuddy.app.views.adapters.CareGiversAdapter
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

/**
 * Appointment details fragment
 * SHows the appointment related data and handles the addition of patient/care giver after appointment is created
 * @constructor Create empty Appointment details fragment
 */

class AppointmentDetailsFragment : BaseFragment(), CareGiversAdapter.Interaction {

    private lateinit var binding: FragmentAppointmentDetailsBinding
    private lateinit var addAppointmentViewModel: AppointmentViewModel
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var chatViewModel: ChatsViewModel
    private lateinit var cancelAppointmentConfirmationDialog: Dialog
    private lateinit var appointmentCancelledDialogue: Dialog
    private lateinit var appointmentDetails: AppointmentDetails
    private lateinit var bottomAddFamilyMemberInputDIalogue: FragmentModalBottomSheet
    private lateinit var addFamilyMemberDialogue: Dialog
    private var ALL_CARE_GIVERS_ADDED = false
    private var HAS_ADDED_CARE_GIVER = false
    private var HAS_CARE_GIVER_DETAILS = false
    private var FILENAME = ""
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    private var careGiverName = "Add Care Giver"
    private lateinit var profileViewModel: ProfileViewModel
    private var IS_UPDATE_MODE = false
    private lateinit var addAppointmentInput: AddAppointmentInput
    private lateinit var doctorParticipantInput: ParticipantInput
    private var notes = ""
    private var appointmentId = ""
    private var totalFees = 1.0
    private lateinit var paymentId: String
    private var consultationFees = 1.0
    private var discountedFees = 1.0
    private lateinit var doctor: Doctor
    private var IS_DOCTOR = false
    private var patientParticipant = ParticipantDetails()
    private lateinit var careGiverList: ArrayList<CareGiverDetails>
    private lateinit var careGiverAdapter: CareGiversAdapter
    private lateinit var participantInputList: ArrayList<ParticipantInput>
    private var IS_PATIENT_SELECTED = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            R.layout.fragment_appointment_details, container, false
        )
        setupVM()
        setupObservers()
        setupClickListeners()
        getArgumentData()
        // getGuestDetails()
        settitle()
        paymentId = getRandomString()
        showPatient()

    }


    private fun showPatient() {
        if (getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER) && IS_UPDATE_MODE) {
            Log.d("add_patient", "1")
            if (getPatientObjectByCG() != null) {
                Log.d("add_patient", "2")
                binding.linAddPatient.visibility = View.VISIBLE
                binding.tvPatientName.setText(getPatientObjectByCG()?.firstName ?: "")
                Glide.with(FourBaseCareApp.activityFromApp)
                    .load(getPatientObjectByCG()?.dpLink)
                    .placeholder(R.drawable.ic_user_image_blue)
                    .circleCrop()
                    .into(binding.ivPatientImage)
                binding.tvPatientRelation.setText("")
                binding.linAdd.setOnClickListener(View.OnClickListener {
                    if (!isDoubleClick()) {
                        if (!::participantInputList.isInitialized) {
                            participantInputList = ArrayList<ParticipantInput>()
                        }
                        if (!IS_PATIENT_SELECTED) {
                            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                                IS_PATIENT_SELECTED = true
                                var participantInput = ParticipantInput()
                                participantInput.userId = getPatientObjectByCG()!!.id
                                participantInput.name = getPatientObjectByCG()!!.firstName
                                participantInput.role = Constants.ROLE_PATIENT
                                participantInputList.add(participantInput)
                                binding.tvAdd.text = "Added"
                                binding.tvAdd.setTextColor(
                                    ContextCompat.getColor(
                                        FourBaseCareApp.activityFromApp,
                                        R.color.green_color_button
                                    )
                                )
                                binding.ivAddPatientIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        FourBaseCareApp.activityFromApp,
                                        R.drawable.ic_cicrular_green_tick
                                    )
                                )

                                var addGuestInput = AddGuestInput()
                                var guestList = ArrayList<GuestUserId>()
                                guestList.add(GuestUserId(getPatientObjectByCG()!!.id))
                                addGuestInput.guestUsers = guestList
                                appointmentViewModel.callAddGuest(
                                    addGuestInput,
                                    getUserAuthToken(),
                                    "" + appointmentDetails.id
                                )
                            }
                        } /*else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                participantInputList.removeIf { participant -> participant.userId === getPatientObjectByCG()!!.id }
                            }
                            binding.tvAdd.text = "Add"
                            binding.tvAdd.setTextColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp, R.color.font_light_blue))
                            binding.ivAdd.setImageDrawable(ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, R.drawable.ic_add_box_blue))

                        }
                        IS_PATIENT_SELECTED = !IS_PATIENT_SELECTED*/
                    }
                })
            } else {
                Log.d("add_patient", "3")
            }
        }
    }


    private fun settitle() {
        binding.layoutHeader.tvTitle.setText("Consultation Details")
    }

    private fun getArgumentData() {
        if (arguments != null) {
            if (arguments!!.containsKey(Constants.SOURCE)) {
                IS_DOCTOR = arguments!!.getString(Constants.SOURCE).equals("doctor_appointments")

                if (arguments!!.getString(Constants.SOURCE).equals("add_appointment")) {
                    binding.tvJoinRoom.visibility = View.GONE
                }
            }

            if (IS_DOCTOR) {
                Log.d("new_apt_log", "0")
                binding.linCancelCOntainer.visibility = View.GONE
                Log.d("new_apt_log", "visi ogff")
                binding.cardCOnsultationFees.visibility = View.GONE
                binding.layoutCOntinue.linContinue.visibility = View.GONE
                //binding.tvJoinRoom.visibility = View.VISIBLE
                binding.tvDetailsTitle.text = "Patient Details"
                binding.ivVerified.visibility = View.GONE
                binding.cardDownloadInvoice.visibility = View.GONE
                if (arguments!!.containsKey(Constants.APPOINTMENT_OBJ)) {
                    appointmentDetails = arguments!!.getParcelable(Constants.APPOINTMENT_OBJ)!!
                    patientParticipant = ParticipantDetails()

                    for (paritcipant in appointmentDetails.participants) {
                        if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {

                        } else if (paritcipant.role.equals(Constants.ROLE_PATIENT, true)) {
                            patientParticipant = paritcipant
                        } else {
                            HAS_ADDED_CARE_GIVER = true
                            Log.d("care_giver_added", "true from doctor")
                        }
                    }

                    if (!patientParticipant.dpLink.isNullOrEmpty()) {
                        Glide.with(FourBaseCareApp.activityFromApp).load(patientParticipant.dpLink)
                            .placeholder(R.drawable.gray_circle).circleCrop()
                            .into(binding.ivDoctorImage)
                    }


                    binding.tvAptIdVal.text = "" + appointmentDetails.id
                    binding.tvDoctorName.text = patientParticipant.name


                    if (patientParticipant.headline != null) {
                        binding.tvDoctorHeadline.text = patientParticipant.headline
                    }
                    binding.tvNotes.setText("" + appointmentDetails.notes)
                    binding.tvDate.text =
                        "" + changeDateTimeFormat(appointmentDetails.scheduledTime)

                    if (!appointmentDetails.invoiceLink.isNullOrEmpty() && !IS_DOCTOR) {
                        val seperated: Array<String> =
                            appointmentDetails.invoiceLink.split("/".toRegex()).toTypedArray()
                        FILENAME = seperated[seperated.size - 1]
                        binding.cardDownloadInvoice.visibility = View.VISIBLE
                    } else {
                        binding.cardDownloadInvoice.visibility = View.GONE
                    }

                    if (appointmentDetails.appointmentStatus.equals("SCHEDULED")) {
                        binding.tvJoinRoom.visibility = View.VISIBLE
                    } else {
                        //binding.tvJoinRoom.visibility = View.GONE
                    }


                }


            } else {
                //binding.tvJoinRoom.visibility = View.GONE

                if (arguments!!.containsKey(Constants.APPOINTMENT_OBJ)) {
                    IS_UPDATE_MODE = true
                    binding.layoutCOntinue.linContinue.visibility = View.GONE
                    appointmentDetails = arguments!!.getParcelable(Constants.APPOINTMENT_OBJ)!!
                    binding.cardCOnsultationFees.visibility =View.GONE
                    Log.d("appt_status", "status " + appointmentDetails.appointmentStatus)
                    /*if(Date().before(Date(CommonMethods.getDate(appointmentDetails.scheduledTime)!!.time.plus(0)))
                        && appointmentDetails.appointmentStatus.equals("SCHEDULED")){
                        binding.linCallBackContainer.visibility = View.GONE
                        binding.linCancelCOntainer.visibility = View.VISIBLE
                        binding.tvJoinRoom.visibility = View.VISIBLE
                        //getCareTakersFromServer()
                    }else{
                        binding.linCallBackContainer.visibility = View.VISIBLE
                        binding.linCancelCOntainer.visibility = View.GONE
                        binding.tvJoinRoom.visibility = View.GONE
                    }*/

                    var doctorParticipant = ParticipantDetails()

                    if (!::participantInputList.isInitialized) {
                        participantInputList = ArrayList<ParticipantInput>()
                    }

                    for (paritcipant in appointmentDetails.participants) {

                        var participantInput = ParticipantInput()
                        participantInput.userId = paritcipant.userId
                        participantInput.name = paritcipant.name
                        participantInput.role = paritcipant.role
                        participantInputList.add(participantInput)

                        if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                            doctorParticipant = paritcipant
                        } else if (paritcipant.role.equals(Constants.ROLE_PATIENT, true)) {
                            IS_PATIENT_SELECTED = true
                            binding.tvAdd.text = "Added"
                            binding.tvAdd.setTextColor(
                                ContextCompat.getColor(
                                    FourBaseCareApp.activityFromApp,
                                    R.color.green_color_button
                                )
                            )
                            binding.ivAddPatientIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    FourBaseCareApp.activityFromApp,
                                    R.drawable.ic_cicrular_green_tick
                                )
                            )

                        } else {
                            HAS_ADDED_CARE_GIVER = true
                            //ALL_CARE_GIVERS_ADDED = true
                            Log.d("care_giver_added", "true from update")
                        }
                    }


                    if (!doctorParticipant.dpLink.isNullOrEmpty())
                        Glide.with(FourBaseCareApp.activityFromApp).load(doctorParticipant.dpLink)
                            .placeholder(R.drawable.gray_circle).circleCrop()
                            .into(binding.ivDoctorImage)

                    binding.tvAptIdVal.text = "" + appointmentDetails.id
                    binding.tvDoctorName.text = doctorParticipant.name

                    if (doctorParticipant.headline != null) {
                        binding.tvDoctorHeadline.text = doctorParticipant.headline
                    }

                    binding.tvNotes.setText("" + appointmentDetails.notes)

                    binding.tvDate.text =
                        "" + changeDateTimeFormat(appointmentDetails.scheduledTime)


                    Log.d("cg_log", "0 " + appointmentDetails.appointmentStatus)
                    if (appointmentDetails.appointmentStatus.equals("COMPLETED")) {
                        //binding.cardDownloadInvoice.visibility = View.VISIBLE
                        binding.cardAddCareGiver.visibility = View.GONE
                        binding.linCallBackContainer.visibility = View.VISIBLE
                        binding.linCancelCOntainer.visibility = View.GONE
                       // binding.tvJoinRoom.visibility = View.GONE

                    } else if (appointmentDetails.appointmentStatus.equals("CANCELLED")) {
                        binding.cardAddCareGiver.visibility = View.GONE
                        binding.linCallBackContainer.visibility = View.GONE
                        binding.linCancelCOntainer.visibility = View.GONE
                        binding.tvJoinRoom.visibility = View.GONE
                    } else {
                        binding.cardDownloadInvoice.visibility = View.GONE
                        // binding.cardAddCareGiver.visibility = View.VISIBLE
                        binding.linCallBackContainer.visibility = View.GONE
                        binding.linCancelCOntainer.visibility = View.VISIBLE
                        binding.tvJoinRoom.visibility = View.VISIBLE
                        if (getUserObject().role.equals(Constants.ROLE_PATIENT)) getCareTakersFromServer()
                    }

                    if (!appointmentDetails.invoiceLink.isNullOrEmpty()) {
                        val seperated: Array<String> =
                            appointmentDetails.invoiceLink.split("/".toRegex()).toTypedArray()
                        FILENAME = seperated[seperated.size - 1]
                        binding.cardDownloadInvoice.visibility = View.VISIBLE
                    } else {
                        binding.cardDownloadInvoice.visibility = View.GONE
                    }
                } else {
                    Log.d("new_apt_log", "2")
                    IS_UPDATE_MODE = false
                    binding.linCancelCOntainer.visibility = View.GONE
                    binding.cardDownloadInvoice.visibility = View.GONE
                    binding.cardCOnsultationFees.visibility =View.VISIBLE
                    binding.linApptId.visibility = View.GONE
                    var strDateTime = arguments?.getString("DATE_TIME")
                    notes = arguments?.getString("NOTES").toString()
                    addAppointmentInput = arguments?.getParcelable("APPOINTMENT_INPUT")!!
                    //binding.tvDate.setText(strDateTime?.let { CommonMethods.changeRecordDateFormat(it) })
                    binding.tvDate.text = "" + strDateTime?.let { changeDateTimeFormat(it) }
                    //binding.tvJoinRoom.visibility = View.GONE
                    binding.tvTime.text = changeTimeFormat("" + strDateTime)
                    consultationFees = arguments!!.getDouble("FEES")
                    Log.d("payment_log", "consultation fees " + consultationFees)
                    totalFees = consultationFees
                    binding.tvFees.setText("" + consultationFees)
                    binding.tvDiscountValue.setText("NA")
                    binding.tvTotalValue.setText("" + consultationFees)
                    doctor = arguments!!.getParcelable("DOCTOR")!!
                    binding.tvDoctorName.setText(doctor.firstName)
                    if (doctor.specialization != null) {
                        binding.tvDoctorHeadline.text = doctor.specialization
                    }

                    if (::appointmentDetails.isInitialized) {
                        binding.tvNotes.setText("" + appointmentDetails.notes)
                    } else {
                        binding.tvNotes.setText("" + addAppointmentInput.notes)
                    }

                    if (doctor.verified) {
                        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_verified)
                            .into(binding.ivVerified)
                    } else {
                        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_not_verified)
                            .into(binding.ivVerified)
                    }
                    if (!doctor.displayPicUrl.isNullOrEmpty()) {
                        Glide.with(FourBaseCareApp.activityFromApp).load(doctor.displayPicUrl)
                            .placeholder(R.drawable.ic_doctor)
                            .transform(CircleCrop()).into(binding.ivDoctorImage)
                    } else {
                        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_doctor)
                            .placeholder(R.drawable.ic_doctor).transform(
                            CircleCrop()
                        ).into(binding.ivDoctorImage)
                    }
                    doctorParticipantInput = ParticipantInput()
                    doctorParticipantInput.userId = doctor.doctorId
                    doctorParticipantInput.role = "DOCTOR"
                    doctorParticipantInput.name = doctor.firstName
                }
            }

        }
    }

    private fun getCareTakersFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                profileViewModel.getCareTakerDetails(getUserAuthToken())

            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun setupClickListeners() {

        /*binding.relPatient.setOnClickListener(View.OnClickListener {
            if(IS_DOCTOR){
                var patientDetails = PatientDetails()
                patientDetails.id = patientParticipant.id
                patientDetails.firstName


                var bundle  = Bundle()
                bundle.putString(Constants.PATIENT_ID,""+patientParticipant.id)
                bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
                bundle.putSerializable(Constants.PATIENT_DATA,patientDetails)
                Log.d("record_back","from doctor")
                var medicalRecordFragment = NovPatientDetails()
                medicalRecordFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    medicalRecordFragment, this, false
                )
            }
        })*/

        binding.tvJoinRoom.setOnClickListener(View.OnClickListener {
            if (askForCameraPermissions()) {
                //showToast(FourBaseCareApp.activityFromApp,"Nothing is strange")
                var bundle = Bundle()
                bundle.putParcelable(Constants.APPOINTMENT_DETAILS, appointmentDetails)
                var videoCallFragment = VideoQueueByDoctorFragment()
                videoCallFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    videoCallFragment, this, false
                )
            }
        })



        binding.layoutCOntinue.linContinue.setOnClickListener(View.OnClickListener {

            if (!isDoubleClick()) {
                if (totalFees > 1.0) {
                    openPaymentScreen()
                } else {
                    Log.d("add_appt_log", "button clicked")
                    addAppointmentToTheServer()
                }
            }


        })

        binding.cardAddCareGiver.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    var addGuestInput = AddGuestInput()

                    var guestList = ArrayList<GuestUserId>()

                    for (participantObj in participantInputList) {
                        guestList.add(GuestUserId(participantObj.userId))
                    }

                    addGuestInput.guestUsers = guestList


                    appointmentViewModel.callAddGuest(
                        addGuestInput,
                        getUserAuthToken(),
                        "" + appointmentDetails.id
                    )
                }
            }

        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.tvCancelAppointment.setOnClickListener(View.OnClickListener {
            if (Date().after(CommonMethods.getDate(appointmentDetails.scheduledTime))) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "You cannot cancel past appointment!"
                )
            } else {
                showcancelAppointmentConfirmDialogue()
            }

        })

        binding.tvCallBack.setOnClickListener(View.OnClickListener {
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                Log.d("call_back", "0")
                var HOUR = 3600 * 1000
                if (Date().after(
                        Date(
                            CommonMethods.getDate(appointmentDetails.scheduledTime)!!.time.plus(
                                72 * HOUR
                            )
                        )
                    ) == true
                ) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        "You cannot request callback after 72 hours!"
                    )
                } else {
                    appointmentViewModel.requestCallbackCall(
                        getUserAuthToken(),
                        "" + appointmentDetails.id
                    )
                }
            }
        })

        binding.tvChat.setOnClickListener(View.OnClickListener {
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                Log.d("call_back", "0")
                var doctorParticipant = ParticipantDetails()

                for (paritcipant in appointmentDetails.participants) {
                    if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                        doctorParticipant = paritcipant
                    }
                }

                var bundle = Bundle()
                bundle.putInt("user_id", doctorParticipant.userId)
                var chatListFragment = ChatListingFragment()
                chatListFragment.arguments = bundle

                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    chatListFragment, this, false
                )

            }
        })

        binding.tvReschedule.setOnClickListener(View.OnClickListener {

            if (Date().after(CommonMethods.getDate(appointmentDetails.scheduledTime))) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "You cannot reschedule past appointment!"
                )
            } else {
                processRescheduling()
            }
        })

        binding.ivDownloadInvoice.setOnClickListener(View.OnClickListener {
            if (!appointmentDetails.invoiceLink.isNullOrEmpty()) {
                if (checkPermission(FourBaseCareApp.activityFromApp)) downloadFile()
            } else {
                showToast(FourBaseCareApp.activityFromApp, "Download link not available")
            }
        })

    }

    fun checkPermission(context: Context?): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (context as Activity?)!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder = AlertDialog.Builder(
                        context
                    )
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_necessary)
                    alertBuilder.setMessage(R.string.external_storage_permission_needed)
                    alertBuilder.setPositiveButton(
                        android.R.string.yes
                    ) { dialog, which ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    val alert = alertBuilder.create()
                    alert.setCancelable(false)
                    alert.show()
                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    private fun downloadFile() {
        try {
            val downloadmanager =
                FourBaseCareApp.activityFromApp.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri = Uri.parse(appointmentDetails.invoiceLink)
            val request = DownloadManager.Request(uri)
            request.setTitle("Invoice File $FILENAME")
            request.setDescription("Downloading...")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "OncoDocs/Invoice $FILENAME"
            )
            downloadmanager.enqueue(request)
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                "Invoice Downloading started! Please check status bar for progress.",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                "Downloading failed! With err $e",
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }


    private fun processRescheduling() {
        var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
        var bundle = Bundle()
        bundle.putParcelable(Constants.APPOINTMENT_DETAILS, appointmentDetails)
        bundle.putString(Constants.APPOINTMENT_ID, "" + appointmentDetails.id)
        addOrEditAppointmentFragment.arguments = bundle
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            addOrEditAppointmentFragment, this, false
        )
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

        appointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)

        chatViewModel = ViewModelProvider(
            this,
            ChatVMInjection.provideViewModelFactory()
        ).get(ChatsViewModel::class.java)

    }

    private fun changeDateTimeFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.OUTPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }


    private fun changeTimeFormat(inputTime: String): String? {
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


    private fun showcancelAppointmentConfirmDialogue() {
        cancelAppointmentConfirmationDialog = Dialog(FourBaseCareApp.activityFromApp)
        cancelAppointmentConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cancelAppointmentConfirmationDialog.setContentView(R.layout.dialogue_cancel_appointment_new)
        val btnYes: TextView = cancelAppointmentConfirmationDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = cancelAppointmentConfirmationDialog.findViewById(R.id.btnNo)

        btnYes.setOnClickListener(View.OnClickListener {
            cancelAppointmentConfirmationDialog.dismiss()
            //showAppointmentCancelledDialogue()
            cancelAppointment()
        })

        btnNo.setOnClickListener(View.OnClickListener {
            cancelAppointmentConfirmationDialog.dismiss()
        })


        cancelAppointmentConfirmationDialog.show()
    }


    private fun showAppointmentCancelledDialogue(msg: String) {
        appointmentCancelledDialogue = Dialog(FourBaseCareApp.activityFromApp)
        appointmentCancelledDialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        appointmentCancelledDialogue.setContentView(R.layout.dialogue_appointment_cancelled)

        val btnAppointments: TextView =
            appointmentCancelledDialogue.findViewById(R.id.btnAppointments)
        val tvMsg: TextView = appointmentCancelledDialogue.findViewById(R.id.tvTitle)

        tvMsg.setText(msg)

        btnAppointments.setOnClickListener(View.OnClickListener {
            appointmentCancelledDialogue.dismiss()
            fragmentManager!!.popBackStack()
        })

        appointmentCancelledDialogue.setCancelable(false)
        appointmentCancelledDialogue.show()
    }


    private fun cancelAppointment() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            addAppointmentViewModel.callCancelAppointment(
                getUserAuthToken(), "" + appointmentDetails.id
            )

        }
    }


    private fun setupObservers() {
        profileViewModel.careGiverResonseData.observe(this, getCareTakerObserver)

        addAppointmentViewModel.guestDetailsResponseData.observe(
            this,
            getGuestDetailsResponseObserver
        )
        addAppointmentViewModel.baseResponseData.observe(this, addGuestResponseObserver)
        addAppointmentViewModel.cancelAppointmentResponseData.observe(
            this,
            cancelAppointmentResponseObserver
        )
        addAppointmentViewModel.responseData.observe(this, responseObserver)
        addAppointmentViewModel.paymentResponseData.observe(this, paymentResponseObserver)

        addAppointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        addAppointmentViewModel.onMessageError.observe(this, errorMessageObserver)

        appointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(this, errorMessageObserver)

        chatViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        chatViewModel.onMessageError.observe(this, errorMessageObserver)

        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val paymentResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            gotoCOnfirmationScreen()
        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding appointment"
            )
        }


    }

    private val getCareTakerObserver =
        androidx.lifecycle.Observer<CareGiverResponse> { responseObserver ->
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

    private val cancelAppointmentResponseObserver = Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            Constants.IS_LIST_UPDATED = true
            showAppointmentCancelledDialogue(responseObserver.message)

        }

    }

    private val getGuestDetailsResponseObserver =
        Observer<GuestDetailsResponse> { responseObserver ->

            binding.executePendingBindings()
            binding.invalidateAll()

            if (responseObserver.isSuccess) {
                binding.ivAdd.visibility = View.GONE
                //binding.tvCareGiverName.setText("Guest : "+responseObserver.payLoad.name)
                HAS_ADDED_CARE_GIVER = true
                Log.d("care_giver_added", "true from api")
            }

        }

    private val isViewLoadingObserver = Observer<Boolean> { isLoading ->
        Log.d("appointment_log", "is_loading is " + isLoading)
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String> { message ->
        Log.d("appointment_log", "Error " + message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }


    private fun addGuestToAppointment(emailId: String, phoneNumber: String, name: String) {
        if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            if (!ALL_CARE_GIVERS_ADDED) {
                careGiverName = name

                if (name.isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please enter name")
                    return
                } else if (phoneNumber.isNullOrEmpty()) {
                    showToast(FourBaseCareApp.activityFromApp, "Please enter email")
                    return
                } else if (!phoneNumber.isNullOrEmpty() && phoneNumber.length != 10) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.validation_invalid_mobile_number)
                    )
                    return
                } else if (getUserObject() != null && !getUserObject().phoneNumber.isNullOrEmpty()
                    && phoneNumber.equals(getUserObject().phoneNumber)
                ) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        "Your mobile number cannot be added as a care giver number"
                    )
                    return
                } else if (emailId.isNullOrEmpty()) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.validation_enter_email)
                    )
                    return
                } else if (!CommonMethods.isValidEmail(getTrimmedText(etEmail))) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.validation_enter_valid_email)
                    )
                    return
                } else {
                    var addGuestInput = AddGuestInput()
                    /* addGuestInput.emailAddress = emailId
                     addGuestInput.phoneNumber = phoneNumber
                     addGuestInput.isAddCareGiver = IS_CARE_GIVER_ADDED
                     addGuestInput.fullName = name
                     addGuestInput.patientName = FourBaseCareApp.sharedPreferences.getString(
                         Constants.PREF_USER_NAME,
                         ""
                     )*/
                    addAppointmentViewModel.callAddGuest(
                        addGuestInput,
                        getUserAuthToken(), "" + appointmentDetails.id
                    )
                    bottomAddFamilyMemberInputDIalogue.dismiss()
                }
            } else {
                var addGuestInput = AddGuestInput()
                /*addGuestInput.emailAddress = emailId
                addGuestInput.phoneNumber = phoneNumber
                addGuestInput.isAddCareGiver = IS_CARE_GIVER_ADDED
                addGuestInput.fullName = name
                addGuestInput.patientName = FourBaseCareApp.sharedPreferences.getString(
                    Constants.PREF_USER_NAME,
                    ""
                )
*/
                addAppointmentViewModel.callAddGuest(
                    addGuestInput,
                    getUserAuthToken(), "" + appointmentDetails.id
                )
                bottomAddFamilyMemberInputDIalogue.dismiss()
            }

        }
    }

    private val addGuestResponseObserver = Observer<BaseResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            showFamilymemberAdded()
            Constants.IS_LIST_UPDATED = true

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding appointment"
            )
        }


    }

    private val responseObserver = Observer<AddAppointmentResponse> { responseObserver ->
        //binding.loginModel = loginResponseData
        binding.executePendingBindings()
        binding.invalidateAll()

        if (responseObserver.success) {
            appointmentId = "" + responseObserver.appointmentDetails.id
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
        tvTitleText.text =
            "An SMS has been sent to " + careGiverName + " with the appointment link. Please be on time."

        val btnNo: TextView = addFamilyMemberDialogue.findViewById(R.id.btnNo)
        //btnNo.setText(getString(R.string.no))

        val btnYes: TextView = addFamilyMemberDialogue.findViewById(R.id.btnYes)
        btnYes.text = "Okay"

        btnYes.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                Constants.IS_LIST_UPDATED = true
                addFamilyMemberDialogue.dismiss()
                fragmentManager?.popBackStack()

            }
        })

        addFamilyMemberDialogue.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                downloadFile()

            }
        }

    }

    private fun addAppointmentToTheServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d(
                "partcipant_log",
                "final participants size " + addAppointmentInput.participantInputList.size
            )
            addAppointmentInput.notes = notes

            addAppointmentViewModel.callAddAppointment(
                addAppointmentInput, getUserObject().authToken
            )
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
            paymentInput.voucherUsed = false
            paymentInput.platformCharges = 0
            /*if (VOUCHER_USED) {
                paymentInput.voucherId = voucherId
                paymentInput.discountAmount = discount.toInt()
            }*/

            addAppointmentViewModel.updatePaymentStatus(
                getUserAuthToken(),
                appointmentId,
                paymentInput
            )

        }

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
            if (requestCode == Constants.FRAGMENT_RESULT) {
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


            }
        }
    }

    private fun gotoCOnfirmationScreen() {
        var bundle = Bundle()
        bundle.putString("APPOINTMENT_ID", appointmentId)
        bundle.putString("DOCTOR_NAME", doctor.firstName)
        bundle.putString("DATE_TIME", addAppointmentInput.scheduledTime)
        bundle.putString("NOTES", addAppointmentInput.notes)
        bundle.putParcelable("DOCTOR", doctor)
        var appointmentFragment = AppointmentConfirmedFragment()
        appointmentFragment.arguments = bundle
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            appointmentFragment, this, false
        )
    }


    fun askForCameraPermissions(): Boolean {
        if (!isCameraPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.CAMERA
                )
                || ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    ), Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }

    fun isCameraPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDeniedDialog() {
        android.app.AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle(getString(R.string.msg_permission_denied))
            .setMessage(getString(R.string.msg_permission_from_settings))
            .setPositiveButton(getString(R.string.title_app_settings),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        FourBaseCareApp.activityFromApp.getPackageName(),
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setCGRecyclerView(list: List<CareGiverDetails>?) {

        val arrayList = list?.let { ArrayList<CareGiverDetails>(it) }

        if (::participantInputList.isInitialized && !list.isNullOrEmpty()) {

            for (participantObj in participantInputList) {
                for (cgObj in list) {
                    Log.d("cg_obj_log", "cgobj id " + cgObj.id)
                    Log.d("cg_obj_log", "cgobj id " + cgObj.appUserId)
                    if (participantObj.userId == cgObj.appUserId) {
                        cgObj.isSelected = true
                        Log.d("cg_obj_log", "TRUE " + cgObj.id)
                    }
                }
            }

        }
        if (arrayList != null && arrayList.size > 0) {
            Log.d("cg_size", "size " + arrayList.size)
            binding.linCareGivers.visibility = View.VISIBLE
            binding.rvCaregivers.apply {
                layoutManager = LinearLayoutManager(activity)
                careGiverAdapter = CareGiversAdapter(arrayList!!, this@AppointmentDetailsFragment)
                adapter = careGiverAdapter
                careGiverAdapter.submitList(arrayList)
            }
        } else {
            binding.linCareGivers.visibility = View.GONE
            Log.d("cg_size", "size 0")
        }
    }

    override fun onCGSelected(position: Int, item: CareGiverDetails, view: View) {
        Log.d("interaction_lg", "1")
        if (!item.isSelected) {
            var careGiverDetails = careGiverList.get(position)
            careGiverDetails.isSelected = !careGiverDetails.isSelected
            careGiverList.set(position, careGiverDetails)
            //setCGRecyclerView(careGiverList)

            if (!::participantInputList.isInitialized) {
                participantInputList = ArrayList<ParticipantInput>()
            }

            if (careGiverDetails.isSelected) {
                var participantInput = ParticipantInput()
                participantInput.userId = careGiverDetails.appUserId
                participantInput.name = careGiverDetails.name
                participantInput.role = Constants.ROLE_PATIENT_CARE_GIVER
                participantInputList.add(participantInput)
                Log.d("partcipant_log", "cg added. Id " + careGiverDetails.appUserId)
            } else {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    participantInputList.removeIf { participant -> participant.userId === careGiverDetails.appUserId }
                    Log.d("partcipant_log","cg removed. Id "+careGiverDetails.appUserId)
                }*/
            }
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                var addGuestInput = AddGuestInput()
                var guestList = ArrayList<GuestUserId>()
                guestList.add(GuestUserId(item.appUserId))
                addGuestInput.guestUsers = guestList
                appointmentViewModel.callAddGuest(
                    addGuestInput,
                    getUserAuthToken(),
                    "" + appointmentDetails.id
                )
            }
        }
    }

}