package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentAppointmentsListNewBinding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ChatVMInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.appointments.ParticipantDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.views.adapters.PatientFutureAppointmentsNewAdapter
import com.oncobuddy.app.views.adapters.PastAppointmentsNewAdapter
import kotlinx.android.synthetic.main.layout_list_upper_container.view.*
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Appointments listing fragment new
 * Shows listing of all appointments > Upcoming, Todays, Followup appointments and past appointments are being shown here
 * @constructor Create empty Appointments listing fragment new
 */

class AppointmentsListingFragmentNew : BaseFragment(),
    PastAppointmentsNewAdapter.Interaction,
    PatientFutureAppointmentsNewAdapter.Interaction {

    private lateinit var binding: FragmentAppointmentsListNewBinding
    private lateinit var appointmentViewModel: AppointmentViewModel
    private lateinit var chatViewModel: ChatsViewModel
    private lateinit var apppointmentsList: ArrayList<AppointmentDetails>
    private lateinit var upcomingList: ArrayList<AppointmentDetails>
    private lateinit var todaysList: ArrayList<AppointmentDetails>
    private lateinit var followUpList: ArrayList<AppointmentDetails>
    private lateinit var pastList: ArrayList<AppointmentDetails>
    private lateinit var upcomingAppointmentsAdapter: PatientFutureAppointmentsNewAdapter
    private lateinit var todayAppointmentsAdapter: PatientFutureAppointmentsNewAdapter
    private lateinit var followUpAppointmentsAdapter: PatientFutureAppointmentsNewAdapter
    private lateinit var pastAppointmentsAdapter: PastAppointmentsNewAdapter
    private val CATEGORY_UPCOMING = 0
    private val CATEGORY_PAST = 1
    private lateinit var confirmRemoveMessageDilogue: Dialog
    private lateinit var selectedAPpointment: AppointmentDetails
    private lateinit var rescheduleReqConfirmationDialogue: Dialog
    private lateinit var  cancelAppointmentConfirmationDialog : Dialog
    private var IS_SHOWING_UPCOMING = true


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
            R.layout.fragment_appointments_list_new, container, false
        )

        Log.d("iv_back_lg","0")

        if(arguments!= null){
            Log.d("iv_back_lg","2")
            if(arguments!!.containsKey(Constants.SHOULD_HIDE_BACK)){
                Log.d("iv_back_lg","3 "+arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK))
                if(arguments!!.getBoolean(Constants.SHOULD_HIDE_BACK)) binding.ivBack.visibility = View.GONE
                else binding.ivBack.visibility = View.VISIBLE
            }
        }else{
            Log.d("iv_back_lg","1")
        }


        setupVM()
        //getRecordsFromDB()
        setupObservers()
        setupClickListeners()

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                CommonMethods.showLog("record_log","0")
                getAppointmentsFromServer()
            }
        }else{
            Toast.makeText(context,getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show()
        }




        setupRecordSearch()
    }

    private fun setupRecordSearch() {
        /*binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                medicalRecordsAdapter.getFilter().filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })*/
    }

    private fun setupObservers() {
        appointmentViewModel.appointmentsList.observe(this, responseObserver)
        appointmentViewModel.callBackResponseData.observe(this,callBackResponseObserver)
        appointmentViewModel.requestRescheduleResponseData.observe(this,requestRescheduleResponseObserver)
        appointmentViewModel.cancelAppointmentResponseData.observe(this, cancelAppointmentResponseObserver)
        appointmentViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        appointmentViewModel.onMessageError.observe(this, errorMessageObserver)
        chatViewModel.startCHatResponseData.observe(this, startChatResponseObserver)
        chatViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        chatViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val cancelAppointmentResponseObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()
        if(responseObserver.success){
            showToast(FourBaseCareApp.activityFromApp, "Appointment cancelled successfully!")
            getAppointmentsFromServer()
        }
    }

    private val callBackResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
         Log.d("call_back","reasponse in observer")
        binding.executePendingBindings()
        binding.invalidateAll()
        CommonMethods.showToast(FourBaseCareApp.activityFromApp,""+responseObserver.message)
    }

    private val requestRescheduleResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()
        CommonMethods.showToast(FourBaseCareApp.activityFromApp,""+responseObserver.message)
        if(responseObserver.success){
            getAppointmentsFromServer()
        }

    }

    private val startChatResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()
        CommonMethods.showToast(FourBaseCareApp.activityFromApp,""+responseObserver.message)
        /*if(responseObserver.success){
            getAppointmentsFromServer()
        }*/

    }

    private val responseObserver = androidx.lifecycle.Observer<Response<AppointmentsListResponse?>>{ responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()

        if(responseObserver.isSuccessful){

            apppointmentsList = ArrayList()
            upcomingList = ArrayList()
            todaysList = ArrayList()
            followUpList = ArrayList()
            pastList = ArrayList()

            val arrayList = responseObserver.body()?.payLoad
            Log.d("list_log","total size "+arrayList?.size)
            apppointmentsList = responseObserver.body()?.payLoad as ArrayList<AppointmentDetails>

            apppointmentsList.forEach {appointment->
                Log.d("list_log","today Date "+Date())
                Log.d("list_log","appt Date "+Date(CommonMethods.getDate(appointment.scheduledTime)!!.time.plus(0)))
                if(appointment.scheduledTime != null && !appointment.paymentStatus.equals("Pending",true)){
                    // Getting all upcoming list

                    if(appointment.patientRequestedCallback != null && appointment.patientRequestedCallback){
                        followUpList.add(appointment) // add follow up
                    }else if(DateUtils.isToday(CommonMethods.getDate(appointment.scheduledTime)!!.time) && appointment.appointmentStatus.equals("SCHEDULED")){
                              todaysList.add(appointment) // Add today
                    }else if(Date().before(Date(CommonMethods.getDate(appointment.scheduledTime)!!.time.plus(0)))){
                        //upcomingList.add(appointment)
                        if(appointment.appointmentStatus.equals("SCHEDULED")){
                            upcomingList.add(appointment) // Add upcoming
                        }else{
                            pastList.add(appointment)
                        }
                    }else if(Date().after(Date(CommonMethods.getDate(appointment.scheduledTime)!!.time.plus(0)))){

                        var HOUR = 3600*1000

                        if(Date().before(Date(CommonMethods.getDate(appointment.scheduledTime)!!.time.plus(72 * HOUR))) == true
                            && appointment.appointmentStatus.equals("SCHEDULED")){
                            upcomingList.add(appointment) // keep in upcoming if it is scheduled and 72 hours not passed and status is scheduled
                        }else{
                            pastList.add(appointment) // Add Past if it is scheduled and 72 hours passed
                        }


                    }

                }

            }

            setRecyclerViews()

        }

    }
    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }


    private fun getAppointmentsFromServer() {
        appointmentViewModel.callGetAppointment(getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER),false,getUserAuthToken(), getUserIdd().toString())
    }


    private fun setupClickListeners() {
        binding.ivAdd.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick() && !checkIFDoctor()){
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DoctorListingFragment(), this, false
                )
            }
        })

        binding.linUpcoming.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                setSelectionColor(CATEGORY_UPCOMING)
                IS_SHOWING_UPCOMING = true
                showSectionPerSelection()
            }
        })

        binding.linPast.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                setSelectionColor(CATEGORY_PAST)
                IS_SHOWING_UPCOMING = false
                showSectionPerSelection()
            }

        })

        binding.ivClose.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                binding.linTitleContainer.visibility = View.VISIBLE
                binding.relSearchContainer.visibility = View.GONE
            }

        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick()){
                fragmentManager?.popBackStack()
            }
        })
    }

    private fun setSelectionColor(category : Int){

        if(category == CATEGORY_UPCOMING){
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))

        }else{
            binding.ivUpcomingLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.light_gray))
            binding.tvUpcomingAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

        if(category == CATEGORY_PAST){
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.reports_blue_title))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                ))
        }else{
            binding.ivPastLine.setBackgroundColor(ContextCompat.getColor(FourBaseCareApp.activityFromApp,R.color.light_gray))
            binding.tvPastAppointments.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                ))
        }

    }

    private fun setupVM() {
        appointmentViewModel = ViewModelProvider(
            this,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)

        chatViewModel = ViewModelProvider(
            this,
            ChatVMInjection.provideViewModelFactory()
        ).get(ChatsViewModel::class.java)

    }

    private fun setRecyclerViews(){

            if(upcomingList.isNullOrEmpty()){
                binding.tvUpcomingTitle.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE


            }else{
                binding.tvUpcomingTitle.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.recyclerView.apply {
                    layoutManager = LinearLayoutManager(activity)
                    upcomingAppointmentsAdapter = PatientFutureAppointmentsNewAdapter(
                        upcomingList,
                        this@AppointmentsListingFragmentNew,
                        false
                    )
                    adapter = upcomingAppointmentsAdapter
                    upcomingAppointmentsAdapter.submitList(upcomingList.asReversed())
                }
            }




        if(todaysList.isNullOrEmpty()){
            binding.tvTodayAppointment.visibility = View.GONE
            binding.rvToday.visibility = View.GONE
        }else{
            binding.tvTodayAppointment.visibility = View.VISIBLE
            binding.rvToday.visibility = View.VISIBLE
            binding.rvToday.apply {
            layoutManager = LinearLayoutManager(activity)
            todayAppointmentsAdapter = PatientFutureAppointmentsNewAdapter(
                todaysList,
                this@AppointmentsListingFragmentNew,
                false
            )
            adapter = todayAppointmentsAdapter
            todayAppointmentsAdapter.submitList(todaysList)
        }
        }


        if(followUpList.isNullOrEmpty()){
            binding.tvFollowupCall.visibility = View.GONE
            binding.rvFollowUp.visibility = View.GONE
        }else{
            binding.tvFollowupCall.visibility = View.VISIBLE
            binding.rvFollowUp.visibility = View.VISIBLE
            binding.rvFollowUp.apply {
                layoutManager = LinearLayoutManager(activity)
                followUpAppointmentsAdapter = PatientFutureAppointmentsNewAdapter(
                    upcomingList,
                    this@AppointmentsListingFragmentNew,
                    false
                )
                adapter = followUpAppointmentsAdapter
                followUpAppointmentsAdapter.submitList(followUpList)
            }
        }

        if(pastList.isNullOrEmpty()){
            //binding.tvFollowupCall.visibility = View.GONE
        }else{
           // binding.tvFollowupCall.visibility = View.VISIBLE
            binding.rvPastAppointments.apply {
                layoutManager = LinearLayoutManager(activity)
                pastAppointmentsAdapter = PastAppointmentsNewAdapter(
                    upcomingList,
                    this@AppointmentsListingFragmentNew,
                    true
                )
                adapter = pastAppointmentsAdapter
                pastAppointmentsAdapter.submitList(pastList.asReversed())
            }
        }




    }



    private fun showSectionPerSelection() {

        if(IS_SHOWING_UPCOMING){
            binding.linUpcomingContainer.visibility = View.VISIBLE
            binding.linPastAppts.visibility = View.GONE


        }else {
            binding.linUpcomingContainer.visibility = View.GONE
            binding.linPastAppts.visibility = View.VISIBLE
        }
    }

    override fun onPastItemSelected(position: Int, item: AppointmentDetails, view: View) {

        if(!isDoubleClick()){
            if(view.id == R.id.ivMenu){
                val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                CommonMethods.prepareMenuForIcons(popupMenu)
                popupMenu.menuInflater.inflate(R.menu.menu_appointment, popupMenu.menu)


                popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                    when(menuItem.itemId){
                        R.id.menu_request_callback ->{
                            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                                Log.d("call_back","0")
                                var HOUR = 3600*1000
                                if(Date().after(Date(CommonMethods.getDate(item.scheduledTime)!!.time.plus(72 * HOUR))) == true){
                                    showToast(FourBaseCareApp.activityFromApp, "You cannot request callback after 72 hours!")
                                }else{
                                    appointmentViewModel.requestCallbackCall(getUserAuthToken(),""+item.id)
                                }


                            }
                         }
                        R.id.menu_chat -> {
                            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                                Log.d("call_back","0")
                                var doctorParticipant = ParticipantDetails()

                                for (paritcipant in item.participants) {
                                    if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                                        doctorParticipant = paritcipant
                                    }
                                }
                                //chatViewModel.callStartChat(getUserAuthToken(),""+doctorParticipant.userId)
                                var bundle = Bundle()
                                bundle.putInt("user_id", doctorParticipant.userId)
                                var chatListFragment = ChatListingFragment()
                                chatListFragment.arguments = bundle

                                CommonMethods.addNextFragment(
                                    FourBaseCareApp.activityFromApp,
                                    chatListFragment, this, false
                                )

                            }
                        }
                    }
                    true
                }

                // Showing the popup menu
                popupMenu.show()
            }else if(view.id == R.id.cardContainer){

                if(checkIFDoctor()){
                    selectedAPpointment = item
                    if(askForCameraPermissions()) {
                        //showToast(FourBaseCareApp.activityFromApp,"Nothing is strange")
                        gotoCallingScreen()
                    }
                }else{
                    var bundle  = Bundle()
                    bundle.putParcelable(Constants.APPOINTMENT_OBJ,item)
                    var appointmentDetails  = AppointmentDetailsFragment()
                    appointmentDetails.arguments = bundle
                    CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp,
                        appointmentDetails,this,false)
                }

            }
        }


    }



    private fun gotoCallingScreen() {
        if (::selectedAPpointment.isInitialized){
            var bundle = Bundle()
            bundle.putParcelable(Constants.APPOINTMENT_DETAILS, selectedAPpointment)
            bundle.putParcelableArrayList(Constants.DOCTOR_APPOINTMENTS_LIST, upcomingList)
            var videoCallFragment = VideoQueueByDoctorFragment()
            videoCallFragment.arguments = bundle

            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                videoCallFragment, this, false
            )
        }
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(FourBaseCareApp.activityFromApp, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
              requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.REQUEST_EXTERNAL_STORAGE)
            }
            return false
        }
        return true
    }

    fun askForCameraPermissions(): Boolean {
        if (!isCameraPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.CAMERA)
                || ActivityCompat.shouldShowRequestPermissionRationale(FourBaseCareApp.activityFromApp, Manifest.permission.RECORD_AUDIO)
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO),Constants.REQUEST_CAMERA_AUDIO_STORAGE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gotoCallingScreen()
                } else {
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp,getString(R.string.msg_allow_permission))
                }
                return
            }
            Constants.REQUEST_CAMERA_AUDIO_STORAGE -> {
                   gotoCallingScreen()
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
                    val uri = Uri.fromParts("package", FourBaseCareApp.activityFromApp.getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === RESULT_OK) {

        }

    }



    private fun getDate(strDate: String?): Date {
        val df = SimpleDateFormat("yyyy-MM-dd")
        var date :Date = df.parse(strDate)
        return date
    }

    private fun showHideNoData(shouldShowNoData: Boolean){
        if(shouldShowNoData){
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE

        }else{
            binding.tvNoData.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d("appointments_log","Hidden "+hidden)
        if (!hidden) {
            Log.d("appointments_log","List "+Constants.IS_LIST_UPDATED)
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (Constants.IS_LIST_UPDATED) {
                    getAppointmentsFromServer()
                    Constants.IS_LIST_UPDATED = false
                    Log.d("appointments_log","Api called "+hidden)
                }

            }
        }
    }

    private fun showRRConfirmDialogue(item: AppointmentDetails) {
        //deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        rescheduleReqConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        rescheduleReqConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(rescheduleReqConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = rescheduleReqConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        rescheduleReqConfirmationDialogue.window?.setAttributes(lp)
        rescheduleReqConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = rescheduleReqConfirmationDialogue.findViewById(R.id.btnDelete)
        val tvMsg: TextView = rescheduleReqConfirmationDialogue.findViewById(R.id.tvMsg)


        tvMsg.setText(FourBaseCareApp.activityFromApp.getString(R.string.are_you_sure_you_want_to_send_rr))


        btnDelete.setText("Yes")
        btnDelete.setOnClickListener(View.OnClickListener {
            //deleteRecordFromServer(obj)
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            appointmentViewModel.callRequestReschedule(getUserAuthToken(),""+item.id)
           }
            rescheduleReqConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = rescheduleReqConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("No")

        btnCancel.setOnClickListener(View.OnClickListener {
            rescheduleReqConfirmationDialogue.dismiss()
        })

        rescheduleReqConfirmationDialogue.show()
    }

    override fun onFutureApptItemSelected(position: Int, item: AppointmentDetails, view: View) {
        if(!isDoubleClick()){
            if(view.id == R.id.ivMenu){
                val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)
                CommonMethods.prepareMenuForIcons(popupMenu)
                popupMenu.menuInflater.inflate(R.menu.menu_future_appointment, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                    when(menuItem.itemId){
                        R.id.menu_cancel ->{
                            showcancelAppointmentConfirmDialogue(""+item.id)
                        }
                        R.id.menu_reschedule -> {
                            var addOrEditAppointmentFragment = AddOrEditAppointmentFragment()
                            var bundle = Bundle()
                            bundle.putParcelable(Constants.APPOINTMENT_DETAILS, item)
                            bundle.putString(Constants.APPOINTMENT_ID, "" + item.id)
                            addOrEditAppointmentFragment.arguments = bundle
                            CommonMethods.addNextFragment(
                                FourBaseCareApp.activityFromApp,
                                addOrEditAppointmentFragment, this, false
                            )
                        }
                    }
                    true
                }

                // Showing the popup menu
                popupMenu.show()
            }else if(view.id == R.id.cardContainer){

                if(checkIFDoctor()){
                    selectedAPpointment = item
                    if(askForCameraPermissions()) {
                        //showToast(FourBaseCareApp.activityFromApp,"Nothing is strange")
                        gotoCallingScreen()
                    }
                }else{
                    var bundle  = Bundle()
                    bundle.putParcelable(Constants.APPOINTMENT_OBJ,item)
                    var appointmentDetails  = AppointmentDetailsFragment()
                    appointmentDetails.arguments = bundle
                    CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp,
                        appointmentDetails,this,false)
                }

            }
        }
    }

    private fun showcancelAppointmentConfirmDialogue(appointmentId: String) {
        cancelAppointmentConfirmationDialog = Dialog(FourBaseCareApp.activityFromApp)
        cancelAppointmentConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cancelAppointmentConfirmationDialog.setContentView(R.layout.dialogue_cancel_appointment_new)
        val btnYes: TextView = cancelAppointmentConfirmationDialog.findViewById(R.id.btnYes)
        val btnNo: TextView = cancelAppointmentConfirmationDialog.findViewById(R.id.btnNo)

        btnYes.setOnClickListener(View.OnClickListener {
            cancelAppointmentConfirmationDialog.dismiss()
            //showAppointmentCancelledDialogue()
            cancelAppointment(appointmentId)
        })

        btnNo.setOnClickListener(View.OnClickListener {
            cancelAppointmentConfirmationDialog.dismiss()
        })


        cancelAppointmentConfirmationDialog.show()
    }

    private fun cancelAppointment(appointmentId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            appointmentViewModel.callCancelAppointment(
                getUserAuthToken(),""+appointmentId
            )

        }
    }



}


