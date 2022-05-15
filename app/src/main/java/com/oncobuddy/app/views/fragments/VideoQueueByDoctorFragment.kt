package com.oncobuddy.app.views.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oncobuddy.app.BuildConfig
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.ActivityDoctorAppointmentsQueue2Binding
import com.oncobuddy.app.models.injectors.AppointmentInjection
import com.oncobuddy.app.models.injectors.ChatVMInjection
import com.oncobuddy.app.models.injectors.ForumsInjection
import com.oncobuddy.app.models.injectors.RecordsInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.appointments.ParticipantDetails
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentDetails
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.utils.CameraCapturerCompat
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.utils.custom_views.FragmentModalBottomSheet
import com.oncobuddy.app.view_models.AppointmentViewModel
import com.oncobuddy.app.view_models.ChatsViewModel
import com.oncobuddy.app.view_models.ForumsViewModel
import com.oncobuddy.app.view_models.RecordsViewModel
import com.oncobuddy.app.views.adapters.MedicalRecordsAdapterNew
import com.oncobuddy.app.views.adapters.UpcomingAppointmentsAdapter
import com.theartofdev.edmodo.cropper.CropImage
import com.twilio.video.*
import tvi.webrtc.VideoSink
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Video queue by doctor fragment
 * Handles video calling for all types of users. Doctors, patients and care givers
 * Also, doctor can view patients documents also. And once the appointment is created, We will have created document
 * @constructor Create empty Video queue by doctor fragment
 */

class VideoQueueByDoctorFragment : BaseFragment(), UpcomingAppointmentsAdapter.Interaction,
    MedicalRecordsAdapterNew.Interaction {
    private lateinit var binding: ActivityDoctorAppointmentsQueue2Binding
    private var currentAppointmentCount = 0
    private var totalAppointments = 0
    private val QUEUE_TAG = "call_queue_log"
    private val mContext: Context = FourBaseCareApp.activityFromApp
    private var isViewingReport = false
    private var IS_CALL_RUNNING = false

    private var accessToken = TWILIO_ACCESS_TOKEN
    private var roomName = ""
    var mCurrentPhotoPath: String? = null
    private var videoRoom: Room? = null
    private var localParticipant: LocalParticipant? = null

    private var remoteParticipantIdentity: String? = null
    private var guestParticipantIdentity: String? = null
    private var audioCodec: AudioCodec? = null
    private var videoCodec: VideoCodec? = null
    private var localAudioTrack: LocalAudioTrack? = null
    private var localVideoTrack: LocalVideoTrack? = null
    private var localVideoView: VideoSink? = null
    private var previousAudioMode = 0
    private var previousMicrophoneMute = false
    private var encodingParameters: EncodingParameters? = null
    private var audioManager: AudioManager? = null
    private var cameraCapturerCompat: CameraCapturerCompat? = null
    private var isFullScreen = false
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var recordsViewModel: RecordsViewModel? = null
    private lateinit var forumsViewModel: ForumsViewModel
    private lateinit var chatViewModel: ChatsViewModel
    private var medicalRecordsAdapter: MedicalRecordsAdapterNew? = null
    private lateinit var appointmentDetails: AppointmentDetails
    private lateinit var upcomingAppointments: ArrayList<AppointmentDetails>
    private var userObj: LoginDetails? = null
    private var bottomCameraOrGalleryDIalogue: FragmentModalBottomSheet? = null
    private var isCallReceived = false
    private var HAS_MANUALLY_DONE = false
    private var IS_VIEWING_DOCUMENT = false
    private var IS_DOCTOR = false
    private var IS_SHOWING_DETAILS = true
    private lateinit var uploadPrescriptionsDialogue: Dialog



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
            R.layout.activity_doctor_appointments_queue_2,
            container,
            false
        )
        if (getUserObject().role.equals(Constants.ROLE_DOCTOR)) {
            IS_DOCTOR = true
        }
        getAppointmentDetails()
        setupVM()
        setupObservers()
        setupVideoView()
        intializeUI()
    }

    fun handleBackPress() {
        if (!isDoubleClick()) {
            if (IS_VIEWING_DOCUMENT) {
                binding.relFragmentContainer.visibility = View.GONE
                IS_VIEWING_DOCUMENT = false
            } else {
                if (IS_CALL_RUNNING) showEndVideoConfirmation()
                else {
                    fragmentManager?.popBackStack()
                }

            }
        }
    }

    private fun getAppointmentDetails() {
        appointmentDetails =
            arguments!!.getParcelable<AppointmentDetails>(Constants.APPOINTMENT_DETAILS)!!
        Constants.PATIENT_ID_FOR_RECORDS = "" + appointmentDetails.userId
        Log.d("doctor_u_r_log", "patient id found " + Constants.PATIENT_ID_FOR_RECORDS)
        if (arguments!!.containsKey(Constants.DOCTOR_APPOINTMENTS_LIST)) {
            upcomingAppointments = ArrayList()
            arguments!!.getParcelableArrayList<AppointmentDetails>(Constants.DOCTOR_APPOINTMENTS_LIST)
                ?.let { upcomingAppointments.addAll(it) }
            totalAppointments = upcomingAppointments.size
            Log.d("appointment_id", "Get Appointment details $totalAppointments")
        }


        Log.d("appointment_id", "Get Appointment details " + appointmentDetails.getId())
    }

    private fun setupVM() {
        appointmentViewModel = ViewModelProvider(
            FourBaseCareApp.activityFromApp,
            AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel::class.java)
        recordsViewModel = ViewModelProvider(
            FourBaseCareApp.activityFromApp,
            RecordsInjection.provideViewModelFactory()
        ).get(RecordsViewModel::class.java)
        forumsViewModel = ViewModelProvider(
            this,
            ForumsInjection.provideViewModelFactory()
        ).get(ForumsViewModel::class.java)
        chatViewModel = ViewModelProvider(
            this,
            ChatVMInjection.provideViewModelFactory()
        ).get(ChatsViewModel::class.java)
    }

    private fun setupObservers() {
        appointmentViewModel.isViewLoading.observe(this, Observer { aBoolean: Boolean ->
            if (aBoolean) {
                binding.layoutProgress.frProgress.visibility = View.VISIBLE
            } else {
                binding.layoutProgress.frProgress.visibility = View.GONE
            }
            binding.executePendingBindings()
            binding.invalidateAll()
        })
        appointmentViewModel.startCallResponseData.observe(this, Observer { baseResponse ->
            if (HAS_MANUALLY_DONE) {
                if (baseResponse.success) {
                    showToast(FourBaseCareApp.activityFromApp, "Call Connected")
                }
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        })
        appointmentViewModel.endCallResponseData.observe(this, Observer { baseResponse ->
            if (HAS_MANUALLY_DONE) {
                if (baseResponse.success) {
                    showToast(FourBaseCareApp.activityFromApp, "Call Ended")
                    //intializeUI()
                    fragmentManager?.popBackStack()
                    /*val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
                    val count = fm.backStackEntryCount
                    for (i in 0 until count) {
                        fm.popBackStackImmediate()
                        Log.d("connect_room", "remove i " + count)
                        Log.d("connect_room", "remove COunt " + count)
                    }*/
                }
            }
            binding.executePendingBindings()
            binding.invalidateAll()

        })
        appointmentViewModel.onMessageError.observe(
            this,
            Observer { s: String? -> showToast(FourBaseCareApp.activityFromApp, s!!) })
        appointmentViewModel.getTokenResponseData.observe(this, Observer { tokenResponse ->
            accessToken = tokenResponse.payLoad.token
            roomName = tokenResponse.payLoad.roomName
            Log.d("connect_room", "Token Generated $accessToken")
            Log.d("connect_room", "Room name generated $roomName")
            binding.executePendingBindings()
            binding.invalidateAll()
            connectToRoom()
        })

        chatViewModel.startCHatResponseData.observe(this, startChatResponseObserver)
        chatViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        chatViewModel.onMessageError.observe(this, errorMessageObserver)


    }

    private val isViewLoadingObserver = Observer<Boolean>{isLoading ->
        Log.d("appointment_log", "is_loading is "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }
    private val errorMessageObserver = Observer<String>{message ->
        Log.d("appointment_log", "Error "+message)
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private val startChatResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->

        binding.executePendingBindings()
        binding.invalidateAll()
        //CommonMethods.showToast(FourBaseCareApp.activityFromApp,""+responseObserver.message)
        fragmentManager?.popBackStack()
        /*if(responseObserver.success){
            getAppointmentsFromServer()
        }*/

    }


    private fun intializeUI() {
        setupClickListeners()
        var participant = ParticipantDetails()
        for (paritcipantObj in appointmentDetails.participants) {
            // If doctor login, We are showing Patient details
            if (IS_DOCTOR) {
                if (paritcipantObj.role.equals(Constants.ROLE_PATIENT, true)) {
                    participant = paritcipantObj
                }

                binding.ivReports.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_reports
                    )
                )
            }
            // If doctor login, We are showing Doctor details
            else {
                if (paritcipantObj.role.equals(Constants.ROLE_DOCTOR, true)) {
                    participant = paritcipantObj
                }
           /*     binding.ivReports.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_video_blue
                    )*/
                binding.ivReports.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_reports
                    )

                )
            }

        }
        if (!participant.dpLink.isNullOrEmpty())
            Glide.with(FourBaseCareApp.activityFromApp).load(participant.dpLink).placeholder(R.drawable.gray_circle).circleCrop().into(binding.ivProfile)

    }

    private fun setupClickListeners() {
        ///binding.switchUploadRecord.setOnClickListener { showUploadRecordDialogue() }

        binding.ivVideo.setOnClickListener(View.OnClickListener {
            if (localVideoTrack != null) {
                val enable = !localVideoTrack!!.isEnabled
                localVideoTrack!!.enable(enable)
                val icon: Int
                if (enable) {
                    icon = R.drawable.video_on
                    binding.ivSwitch.visibility = View.VISIBLE
                } else {
                    icon = R.drawable.ic_video_off
                    binding.ivSwitch.visibility = View.GONE
                }
                binding.ivVideo.setImageDrawable(
                    ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, icon)
                )
            }
        })

        binding.ivVolume.setOnClickListener(View.OnClickListener {
            if (localAudioTrack != null) {
                val enable = !localAudioTrack!!.isEnabled
                localAudioTrack!!.enable(enable)
                val icon = if (enable) R.drawable.volume_on else R.drawable.ic_volume_off
                binding.ivVolume.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp, icon
                    )
                )
            }
        })

        binding.ivSwitch.setOnClickListener(View.OnClickListener {
            if (cameraCapturerCompat != null) {
                if (cameraCapturerCompat != null) {
                    val cameraSource = cameraCapturerCompat!!.cameraSource
                    cameraCapturerCompat!!.switchCamera()
                    if (binding.linThumbnail.visibility == View.VISIBLE) {
                        binding.thumbnailVideoView.mirror =
                            cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
                    } else {
                        binding.primaryVideoView.mirror =
                            cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
                    }
                }
            }
        })

        binding.ivReports.setOnClickListener(View.OnClickListener {
            var bundle = Bundle()
            Log.d("patient_id_log","Sent patient id "+appointmentDetails.userId)
            var patientId = ""
            for (paritcipant in appointmentDetails.participants) {
                if (paritcipant.role.equals(Constants.ROLE_PATIENT, true)) {
                    patientId = ""+paritcipant.userId
                }
            }
            //Log.d("patient_id_log","Sent patient id "+appointmentDetails.)
            bundle.putString(Constants.PATIENT_ID, ""+patientId)
            bundle.putBoolean(Constants.SHOULD_HIDE_BACK, false)
            Log.d("record_back", "from doctor")
            var medicalRecordFragment = PatientRecordsListingByDrFragment()
            medicalRecordFragment.arguments = bundle
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                medicalRecordFragment, this, false
            )
        })

        binding.ivBack.setOnClickListener {
            if (!isDoubleClick()) {
                if (IS_CALL_RUNNING) showEndVideoConfirmation()
                else {
                    fragmentManager?.popBackStack()
                }
                //showEndVideoConfirmation()
            }
        }
        binding.linThumbnail.setOnClickListener { moveThumbNailToPrimaryView() }
        binding.linGuestThumbnail.setOnClickListener { moveGuestViewToPrimaryView() }
        binding.ivCall.setOnClickListener { v: View? ->
            IS_CALL_RUNNING = true
            Log.d("connect_room", "DISCONNECT")
            if (videoRoom != null) {
                videoRoom!!.disconnect()
            }
            appointmentViewModel.callEndCall(
                getUserObject().authToken,
                "" + appointmentDetails!!.id
            )
            //isCallReQ ceived = true
            exitVideoCall()
        }
        binding.ivCloseContainer.setOnClickListener {
            binding.relFragmentContainer.visibility = View.GONE
            IS_VIEWING_DOCUMENT = false
        }

    }

    private fun connectToRoom() {
        try {
            Log.d("connect_room ", "1")
            configureAudio(true)
            val connectOptionsBuilder = ConnectOptions.Builder(accessToken)
                .roomName(roomName)
            Log.d("connect_room", "Room name $roomName")
            Log.d("connect_room", "token $accessToken")

            if (localAudioTrack != null) {
                connectOptionsBuilder
                    .audioTracks(listOf(localAudioTrack))
            }

            /*
             * Add local video track to connect options to share with participants.
             */Log.d("connect_room ", "2")
            if (localVideoTrack != null) {
                connectOptionsBuilder.videoTracks(listOf(localVideoTrack))
            }

            /*
             * Set the preferred audio and video codec for media.
             */connectOptionsBuilder.preferAudioCodecs(listOf(audioCodec))
            connectOptionsBuilder.preferVideoCodecs(listOf(videoCodec))
            Log.d("connect_room ", "3")
            /*
             * Set the sender side encoding parameters.
             */connectOptionsBuilder.encodingParameters(encodingParameters!!)
            Log.d("v_call_log", "Video connect room clicked")
            videoRoom = Video.connect(
                FourBaseCareApp.activityFromApp.applicationContext,
                connectOptionsBuilder.build(),
                roomListener()
            )
            Log.d("connect_room ", "4")
            setDisconnectAction()
            Log.d("connect_room ", "5")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("connect_room ", "error $e")
        }
    }

    private fun setupVideoView() {
        audioManager =
            FourBaseCareApp.activityFromApp.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager!!.isSpeakerphoneOn = true
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone()
        } else {
            createAudioAndVideoTracks()
            newToken
        }
    }

    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val playbackAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(playbackAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { i: Int -> }
                .build()
            audioManager!!.requestAudioFocus(focusRequest)
        } else {
            audioManager!!.requestAudioFocus(
                null, AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
        }
    }

    private fun sendFCM(isConnected: Boolean) {
        Log.d("v_call_log", "FCM sent " + isConnected)
        //binding.layoutProgress.frProgress.visibility = View.VISIBLE
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Log.d("v_call_log", "FCM API called " + isConnected)
            if (isConnected) {
                HAS_MANUALLY_DONE = true
                appointmentViewModel.callStartCall(
                    getUserObject().authToken,
                    "" + appointmentDetails!!.id
                )

                startOneMinWaitForCall()
            } else {
                Log.d("v_call_log", "disconnect fcm called")
                HAS_MANUALLY_DONE = true
                /*appointmentViewModel.callEndCall(
                    userObj!!.authToken,
                    "" + appointmentDetails!!.id
                )*/
            }
        }
    }

    //retrieveAccessTokenfromServer();
    private val newToken: Unit
        private get() {
            if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                appointmentViewModel.callGetToken(
                    getUserObject().authToken,
                    "" + appointmentDetails!!.id
                )
                //retrieveAccessTokenfromServer();
            }
        }


    private fun createAudioAndVideoTracks() {
        try {
            // Share your microphone
            localAudioTrack = LocalAudioTrack.create(
                FourBaseCareApp.activityFromApp,
                true,
                LOCAL_AUDIO_TRACK_NAME
            )

            // Share your camera
            cameraCapturerCompat =
                CameraCapturerCompat(
                    FourBaseCareApp.activityFromApp,
                    CameraCapturerCompat.Source.FRONT_CAMERA
                )
            localVideoTrack = LocalVideoTrack.create(
                FourBaseCareApp.activityFromApp,
                true,
                cameraCapturerCompat!!,
                LOCAL_VIDEO_TRACK_NAME
            )
            binding.primaryVideoView.mirror = true
            localVideoTrack!!.addSink(binding.primaryVideoView)
            localVideoView = binding.primaryVideoView
        } catch (e: Exception) {
            Log.d(QUEUE_TAG, "create a/v track err $e")
            e.printStackTrace()
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.CAMERA
        )
        val resultMic = ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.RECORD_AUDIO
        )
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                FourBaseCareApp.activityFromApp,
                Manifest.permission.CAMERA
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                FourBaseCareApp.activityFromApp,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            Toast.makeText(
                FourBaseCareApp.activityFromApp,
                R.string.permissions_needed,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                FourBaseCareApp.activityFromApp,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_MIC_PERMISSION_REQUEST_CODE
            )
        }
    }

    //
    /*
     * Get the preferred audio codec from shared preferences
     */
    private fun getAudioCodecPreference(key: String, defaultValue: String): AudioCodec {
        val audioCodecName = FourBaseCareApp.sharedPreferences.getString(key, defaultValue)
        return when (audioCodecName) {
            IsacCodec.NAME -> IsacCodec()
            OpusCodec.NAME -> OpusCodec()
            PcmaCodec.NAME -> PcmaCodec()
            PcmuCodec.NAME -> PcmuCodec()
            G722Codec.NAME -> G722Codec()
            else -> OpusCodec()
        }
    }

    /*
     * Get the preferred video codec from shared preferences
     */
    private fun getVideoCodecPreference(key: String, defaultValue: String): VideoCodec {
        val videoCodecName = FourBaseCareApp.sharedPreferences.getString(key, defaultValue)
        return when (videoCodecName) {
            Vp8Codec.NAME -> Vp8Codec(false)
            H264Codec.NAME -> H264Codec()
            Vp9Codec.NAME -> Vp9Codec()
            else -> Vp8Codec()
        }
    }

    private fun getEncodingParameters(): EncodingParameters {
        val maxAudioBitrate =
            Objects.requireNonNull(
                FourBaseCareApp.sharedPreferences.getString(
                    Constants.PREF_SENDER_MAX_AUDIO_BITRATE,
                    Constants.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT
                )
            )!!.toInt()
        val maxVideoBitrate =
            Objects.requireNonNull(
                FourBaseCareApp.sharedPreferences.getString(
                    Constants.PREF_SENDER_MAX_VIDEO_BITRATE,
                    Constants.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT
                )
            )!!.toInt()
        return EncodingParameters(maxAudioBitrate, maxVideoBitrate)
    }

    /*
     * The actions performed during disconnect.
     */
    private fun setDisconnectAction() {
        Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_call_disconnect)
            .into(binding.ivCall)

    }

    /*
     * Called when remote participant joins the room
     */
    private fun addRemoteParticipant(remoteParticipant: RemoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (binding.linThumbnail.visibility == View.VISIBLE) {
            /*Snackbar.make(binding.disconnectActionFab,
                    "Multiple participants are not currently support in this UI",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;*/
            guestParticipantIdentity = remoteParticipant.identity
            Log.d("multiple_connect", "second participant $guestParticipantIdentity")
            binding.videoStatusTextview.text = "Guest connecting..."
            Log.d(
                "multiple_connect",
                "video tracks size " + remoteParticipant.remoteVideoTracks.size
            )
            //Log.d("multiple_connect","video tracks subscribed "+remoteVideoTrackPublication.isTrackSubscribed());
            if (remoteParticipant.remoteVideoTracks.size > 0) {
                val remoteVideoTrackPublication = remoteParticipant.remoteVideoTracks[0]
                Log.d(
                    "multiple_connect",
                    "guest subscribed  " + remoteVideoTrackPublication.isTrackSubscribed
                )
                remoteParticipant.setListener(guestRemoteParticipantListener())
            }
        } else {
            Log.d("multiple_connect", "first participant connected " + remoteParticipant.identity)
            remoteParticipantIdentity = remoteParticipant.identity
            binding.videoStatusTextview.text = "Patient connecting..."

            /*
             * Add remote participant renderer
             */if (remoteParticipant.remoteVideoTracks.size > 0) {
                val remoteVideoTrackPublication = remoteParticipant.remoteVideoTracks[0]

                /*
                 * Only render video tracks that are subscribed to
                 */Log.d(
                    "multiple_connect",
                    "patient track subscribed  " + remoteVideoTrackPublication.isTrackSubscribed
                )
                /*if (remoteVideoTrackPublication.isTrackSubscribed()) {
                    Log.d("multiple_connect","patient track subscribed  Yes");
                    addRemoteParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.getRemoteVideoTrack()));
                }*/
            }

            /*
             * Start listening for participant events
             */remoteParticipant.setListener(remoteParticipantListener())
        }
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private fun addRemoteParticipantVideo(videoTrack: VideoTrack) {
        moveLocalVideoToThumbnailView()
        binding.primaryVideoView.mirror = false
        videoTrack.addSink(binding.primaryVideoView)
        binding.videoStatusTextview.text = "Patient Connected"

    }

    private fun addGuestRemoteParticipantVideo(videoTrack: VideoTrack) {
        Log.d("multiple_connect", "add guest 01")
        showGuestVideoThumbnail()
        binding.primaryVideoView.mirror = false
        videoTrack.addSink(binding.guestThumbnailVideoView)
        Log.d("multiple_connect", "add guest 02")
    }

    private fun showGuestVideoThumbnail() {
        try {
            if (binding.linGuestThumbnail.visibility == View.GONE) {
                binding.linGuestThumbnail.visibility = View.VISIBLE

                //localVideoTrack.addSink(binding.guestThumbnailVideoView);
            }
        } catch (e: Exception) {
            Log.d("multiple_connect", "show guest err $e")
            e.printStackTrace()
        }
    }

    private fun moveLocalVideoToThumbnailView() {
        try {
            if (binding.linThumbnail.visibility == View.GONE) {
                binding.linThumbnail.visibility = View.VISIBLE
                localVideoTrack!!.removeSink(binding.primaryVideoView)
                localVideoTrack!!.addSink(binding.thumbnailVideoView)
                localVideoView = binding.thumbnailVideoView
                binding.thumbnailVideoView.mirror = cameraCapturerCompat!!.cameraSource ==
                        CameraCapturerCompat.Source.FRONT_CAMERA
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     * Called when remote participant leaves the room
     */
    private fun removeRemoteParticipant(remoteParticipant: RemoteParticipant) {
        binding.videoStatusTextview.text = "Patient left call"
        if (remoteParticipant.identity != remoteParticipantIdentity) {
            return
        }

        /*
         * Remove remote participant renderer
         */if (!remoteParticipant.remoteVideoTracks.isEmpty()) {
            val remoteVideoTrackPublication = remoteParticipant.remoteVideoTracks[0]

            /*
             * Remove video only if subscribed to participant track
             */if (remoteVideoTrackPublication.isTrackSubscribed) {
                removeParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.remoteVideoTrack)!!)
            }
        }
        moveLocalVideoToPrimaryView()
    }

    private fun removeParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeSink(binding.primaryVideoView)
    }

    private fun removeGuestParticipantVideo(videoTrack: VideoTrack) {
        videoTrack.removeSink(binding.guestThumbnailVideoView)
    }

    private fun moveLocalVideoToPrimaryView() {
        try {
            if (binding.linThumbnail.visibility == View.VISIBLE) {
                binding.linThumbnail.visibility = View.GONE
                binding.linGuestThumbnail.visibility = View.GONE
                if (localVideoTrack != null) {
                    localVideoTrack!!.removeSink(binding.thumbnailVideoView)
                    localVideoTrack!!.removeSink(binding.guestThumbnailVideoView)
                    localVideoTrack!!.addSink(binding.primaryVideoView)
                }
                localVideoView = binding.primaryVideoView
                binding.primaryVideoView.mirror = cameraCapturerCompat!!.cameraSource ==
                        CameraCapturerCompat.Source.FRONT_CAMERA
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveThumbNailToPrimaryView() {
        try {
            try {
                if (binding.linThumbnail.visibility == View.VISIBLE) {
                    if (localVideoTrack != null) {
                        localVideoTrack!!.removeSink(binding.primaryVideoView)
                        localVideoTrack!!.addSink(binding.thumbnailVideoView)
                    }
                    localVideoView = binding.thumbnailVideoView
                    binding.thumbnailVideoView.mirror = cameraCapturerCompat!!.cameraSource ==
                            CameraCapturerCompat.Source.FRONT_CAMERA
                    binding.thumbnailVideoView.mirror = true
                 //   showToast(FourBaseCareApp.activityFromApp, "CHanged")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showToast(FourBaseCareApp.activityFromApp, "Thumbnail err 0  $e")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(FourBaseCareApp.activityFromApp, "Thumbnail err $e")
        }
    }

    private fun moveGuestViewToPrimaryView() {
        try {
            localVideoTrack!!.removeSink(binding.primaryVideoView)
            localVideoTrack!!.addSink(binding.guestThumbnailVideoView)
            localVideoView = binding.guestThumbnailVideoView
            binding.guestThumbnailVideoView.mirror = cameraCapturerCompat!!.cameraSource ==
                    CameraCapturerCompat.Source.FRONT_CAMERA
            showToast(FourBaseCareApp.activityFromApp, "Guest Thumbnail transfered")
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(FourBaseCareApp.activityFromApp, " Guest Thumbnail err $e")
        }
    }

    /*
     * Room events listener
     */
    private fun roomListener(): Room.Listener {
        return object : Room.Listener {
            override fun onConnected(room: Room) {
                Log.d("connect_room", "Connected")
                binding.linCOnnect.visibility = View.GONE
                showToast(FourBaseCareApp.activityFromApp, "Connected")
                binding.videoStatusTextview.text = "Connected"
                IS_CALL_RUNNING = true
                binding.layoutProgress.frProgress.visibility = View.GONE
                localParticipant = room.localParticipant
                FourBaseCareApp.activityFromApp.title = room.name
                for (remoteParticipant in room.remoteParticipants) {
                    addRemoteParticipant(remoteParticipant)
                    break
                }
                //sendFCM(true)
                Constants.IS_LIST_UPDATED = true
                currentAppointmentCount++
                setDisconnectAction()
                //getCurrentPatientDetails();
            }

            override fun onConnectFailure(room: Room, e: TwilioException) {
                binding.linCOnnect.visibility = View.VISIBLE
                showToast(FourBaseCareApp.activityFromApp, "Failed to connect " + roomName)
                binding.videoStatusTextview.text = "Failed to connect"
                IS_CALL_RUNNING = false
                Log.d("connect_room", "Failure")
                configureAudio(false)
                Log.d("connect_room", "" + e.explanation.toString())
                //Log.d("call_connect_Err ",""+e.getCause().toString());
                Log.d("connect_room", "" + e.message)
                Log.d("connect_room", "" + e.code)
                intializeUI()
            }

            override fun onReconnecting(room: Room, twilioException: TwilioException) {
                Log.d("call_connect_log ", "Reconnecting")
                Log.d("connect_room", "Reconnect")
                binding.linCOnnect.visibility = View.VISIBLE
                binding.videoStatusTextview.text = "Reconnecting..."
                IS_CALL_RUNNING = false
                setDisconnectAction()
            }

            override fun onReconnected(room: Room) {
                Log.d("connect_room ", "Reconnected")
                binding.videoStatusTextview.text = "Reconnected"
                IS_CALL_RUNNING = true
                setDisconnectAction()
                binding.linCOnnect.visibility = View.GONE
            }

            override fun onDisconnected(room: Room, e: TwilioException?) {
                //getCurrentPatientDetails();
                binding.linCOnnect.visibility = View.VISIBLE
                IS_CALL_RUNNING = false
                localParticipant = null
                binding.videoStatusTextview.text = "Disconnected from call"
                configureAudio(false)
                intializeUI()
                moveLocalVideoToPrimaryView()
                //                }
                Log.d("v_call_log", "FCM sent from disconnect")
                sendFCM(false)
                Log.d("dis_connect_room", "called from ondisconnected")
                try {
                    Log.i(QUEUE_TAG, "Came here")
                    Log.i(QUEUE_TAG, "total appointments " + totalAppointments)
                    Log.i(QUEUE_TAG, "current appointment " + currentAppointmentCount)
                    if (getUserObject().role.equals(Constants.ROLE_DOCTOR)) {
                        showUploadPrescriptionDialogue()
                    } else {
                        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                            Log.d("call_back", "0")
                            var doctorParticipant = ParticipantDetails()

                            for (paritcipant in appointmentDetails.participants) {
                                if (paritcipant.role.equals(Constants.ROLE_DOCTOR, true)) {
                                    doctorParticipant = paritcipant
                                }
                            }
                            chatViewModel.callStartChat(
                                getUserAuthToken(),
                                "" + doctorParticipant.userId
                            )

                        }
                    }

                    /*if(currentAppointmentCount < totalAppointments){
                        Log.i(QUEUE_TAG,"Call confirmation dialogue 1");

                        showNextCallConfirmationMsg();
                    }*/
                } catch (e1: Exception) {
                    Log.d("connect_room ", "Discconnected err $e1")
                    e1.printStackTrace()
                }
            }

            override fun onParticipantConnected(room: Room, remoteParticipant: RemoteParticipant) {
                Log.d("connect_room ", "Participant Connected")
                IS_CALL_RUNNING = true
                isCallReceived = true
                addRemoteParticipant(remoteParticipant)

            }

            override fun onParticipantDisconnected(
                room: Room,
                remoteParticipant: RemoteParticipant
            ) {
                IS_CALL_RUNNING = true
                Log.d("connect_room ", "Participant disconnected")
                removeRemoteParticipant(remoteParticipant)
                showToast(FourBaseCareApp.activityFromApp, "Others left the call")
                if (room != null) {
                    room.disconnect()
                }
                if (videoRoom != null && videoRoom!!.state != Room.State.DISCONNECTED) {
                    videoRoom!!.disconnect()
                    Log.d("v_call_log", " Room not null, disconnected manually")
                    //            disconnectedFromOnDestroy = true;
                }
            }

            override fun onRecordingStarted(room: Room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("connect_room", "onRecordingStarted")
            }

            override fun onRecordingStopped(room: Room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("connect_room", "onRecordingStopped")
            }
        }
    }

    private fun remoteParticipantListener(): RemoteParticipant.Listener {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.isTrackEnabled,
                        remoteAudioTrackPublication.isTrackSubscribed,
                        remoteAudioTrackPublication.trackName
                    )
                )
            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.isTrackEnabled,
                        remoteAudioTrackPublication.isTrackSubscribed,
                        remoteAudioTrackPublication.trackName
                    )
                )
            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.isTrackEnabled,
                        remoteDataTrackPublication.isTrackSubscribed,
                        remoteDataTrackPublication.trackName
                    )
                )

            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.isTrackEnabled,
                        remoteDataTrackPublication.isTrackSubscribed,
                        remoteDataTrackPublication.trackName
                    )
                )

            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.isTrackEnabled,
                        remoteVideoTrackPublication.isTrackSubscribed,
                        remoteVideoTrackPublication.trackName
                    )
                )

            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.isTrackEnabled,
                        remoteVideoTrackPublication.isTrackSubscribed,
                        remoteVideoTrackPublication.trackName
                    )
                )

            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrack.isEnabled,
                        remoteAudioTrack.isPlaybackEnabled,
                        remoteAudioTrack.name
                    )
                )

            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrack.isEnabled,
                        remoteAudioTrack.isPlaybackEnabled,
                        remoteAudioTrack.name
                    )
                )

            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )

            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrack.isEnabled,
                        remoteDataTrack.name
                    )
                )

            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrack.isEnabled,
                        remoteDataTrack.name
                    )
                )

            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )

            }

            override fun onVideoTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrack.isEnabled,
                        remoteVideoTrack.name
                    )
                )
                Log.d("multiple_connect", "On patient track subscribed  Yes")
                addRemoteParticipantVideo(remoteVideoTrack)
            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrack.isEnabled,
                        remoteVideoTrack.name
                    )
                )
                removeParticipantVideo(remoteVideoTrack)
            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    QUEUE_TAG, String.format(
                        "onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )

            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }
        }
    }

    private fun guestRemoteParticipantListener(): RemoteParticipant.Listener {
        return object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.isTrackEnabled,
                        remoteAudioTrackPublication.isTrackSubscribed,
                        remoteAudioTrackPublication.trackName
                    )
                )
            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.isTrackEnabled,
                        remoteAudioTrackPublication.isTrackSubscribed,
                        remoteAudioTrackPublication.trackName
                    )
                )

            }

            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.isTrackEnabled,
                        remoteDataTrackPublication.isTrackSubscribed,
                        remoteDataTrackPublication.trackName
                    )
                )

            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.isTrackEnabled,
                        remoteDataTrackPublication.isTrackSubscribed,
                        remoteDataTrackPublication.trackName
                    )
                )

            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.isTrackEnabled,
                        remoteVideoTrackPublication.isTrackSubscribed,
                        remoteVideoTrackPublication.trackName
                    )
                )

            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.isTrackEnabled,
                        remoteVideoTrackPublication.isTrackSubscribed,
                        remoteVideoTrackPublication.trackName
                    )
                )

            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrack.isEnabled,
                        remoteAudioTrack.isPlaybackEnabled,
                        remoteAudioTrack.name
                    )
                )

            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrack.isEnabled,
                        remoteAudioTrack.isPlaybackEnabled,
                        remoteAudioTrack.name
                    )
                )

            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteAudioTrackPublication.trackSid,
                        remoteAudioTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )

            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrack.isEnabled,
                        remoteDataTrack.name
                    )
                )

            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteDataTrack.isEnabled,
                        remoteDataTrack.name
                    )
                )

            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteDataTrackPublication.trackSid,
                        remoteDataTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )

            }

            override fun onVideoTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrack.isEnabled,
                        remoteVideoTrack.name
                    )
                )
                Log.d("multiple_connect", "On patient track subscribed  Yes")
                addGuestRemoteParticipantVideo(remoteVideoTrack)
            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrack.isEnabled,
                        remoteVideoTrack.name
                    )
                )
                removeGuestParticipantVideo(remoteVideoTrack)
            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {
                Log.i(
                    "multiple_connect", String.format(
                        "onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.identity,
                        remoteVideoTrackPublication.trackSid,
                        remoteVideoTrackPublication.trackName,
                        twilioException.code,
                        twilioException.message
                    )
                )


            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {
            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {
            }
        }
    }

    private fun connectClickListener(roomEditText: EditText): DialogInterface.OnClickListener {
        return DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> }
    }

    private fun disconnectClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            /*
             * Disconnect from room
             */if (videoRoom != null) {
            videoRoom!!.disconnect()
        }
            appointmentViewModel.callEndCall(
                getUserObject().authToken,
                "" + appointmentDetails!!.id
            )
            //isCallReQ ceived = true
            exitVideoCall()
            //intializeUI()
        }
    }

    //    private View.OnClickListener connectActionClickListener() {
    //        return v -> showConnectDialog();
    //    }
    //    private DialogInterface.OnClickListener cancelConnectDialogClickListener() {
    //        return (image_page_dialog, which) -> {
    //            intializeUI();
    //            connectDialog.dismiss();
    //        };
    //    }
    private fun switchCameraClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (cameraCapturerCompat != null) {
                if (cameraCapturerCompat != null) {
                    val cameraSource = cameraCapturerCompat!!.cameraSource
                    cameraCapturerCompat!!.switchCamera()
                    if (binding.linThumbnail.visibility == View.VISIBLE) {
                        binding.thumbnailVideoView.mirror =
                            cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
                    } else {
                        binding.primaryVideoView.mirror =
                            cameraSource == CameraCapturerCompat.Source.BACK_CAMERA
                    }
                }
            }
        }
    }

    private fun localVideoClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            /*
             * Enable/disable the local video track
             */if (localVideoTrack != null) {
            val enable = !localVideoTrack!!.isEnabled
            localVideoTrack!!.enable(enable)
            val icon: Int
            if (enable) {
                icon = R.drawable.ic_videocam_on
                //binding.switchCameraActionFab.show()
            } else {
                icon = R.drawable.ic_videocam_off
                //binding.switchCameraActionFab.hide()
            }
            /*binding.localVideoActionFab.setImageDrawable(
                ContextCompat.getDrawable(FourBaseCareApp.activityFromApp, icon)
            )*/
        }
        }
    }

    private fun muteClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            /*
             * Enable/disable the local audio track. The results of this operation are
             * signaled to other Participants in the same Room. When an audio track is
             * disabled, the audio is muted.
             */if (localAudioTrack != null) {
            val enable = !localAudioTrack!!.isEnabled
            localAudioTrack!!.enable(enable)
            val icon = if (enable) R.drawable.ic_mic_on else R.drawable.ic_mic_off
            /*  binding.muteActionFab.setImageDrawable(
                  ContextCompat.getDrawable(
                      FourBaseCareApp.activityFromApp, icon
                  )
              )*/
        }
        }
    }

    private fun volumeClickListener(): View.OnClickListener {
        return View.OnClickListener { v: View? ->
            if (audioManager!!.isSpeakerphoneOn) {
                audioManager!!.isSpeakerphoneOn = false
                /*binding.volumeActionFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp, R.drawable.ic_phonelink_ring_white_24dp
                    )
                )*/
            } else {
                audioManager!!.isSpeakerphoneOn = true
                /*binding.volumeActionFab.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp, R.drawable.ic_volume_up_white_24dp
                    )
                )*/
            }
        }
    }

    private fun configureAudio(enable: Boolean) {
        if (enable) {
            previousAudioMode = audioManager!!.mode
            // Request audio focus before making any device switch
            if (audioManager == null) {
                Log.i(QUEUE_TAG, "Audio manager null")
            }
            requestAudioFocus()
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */audioManager!!.mode = AudioManager.MODE_IN_COMMUNICATION
            /*
             * Always disable microphone mute during a WebRTC call.
             */previousMicrophoneMute = audioManager!!.isMicrophoneMute
            audioManager!!.isMicrophoneMute = false
        } else {
            if (audioManager == null) {
                Log.i(QUEUE_TAG, "Audio manager null")
            }
            if (previousAudioMode == 0) {
                Log.i(QUEUE_TAG, "Prev audio manager 0")
            }
            if (audioManager != null) {
                audioManager!!.mode = previousAudioMode
                audioManager!!.abandonAudioFocus(null)
                audioManager!!.isMicrophoneMute = previousMicrophoneMute
            }
        }
    }

    //Activity events
    override fun onResume() {
        super.onResume()

        /*
         * Update preferred audio and video codec in case changed in settings
         */
        audioCodec = getAudioCodecPreference(
            Constants.PREF_AUDIO_CODEC,
            Constants.PREF_AUDIO_CODEC_DEFAULT
        )
        videoCodec = getVideoCodecPreference(
            Constants.PREF_VIDEO_CODEC,
            Constants.PREF_VIDEO_CODEC_DEFAULT
        )

/*        Log.i(QUEUE_TAG,"Resume audio codec "+audioCodec );
        Log.i(QUEUE_TAG,"Resume video codec "+videoCodec );*/
        /*
         * Get latest encoding parameters
         */
        val newEncodingParameters = getEncodingParameters()

        /*
         * If the local video track was released when the app was put in the background, recreate.
         */try {
            if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
                localVideoTrack = LocalVideoTrack.create(
                    FourBaseCareApp.activityFromApp,
                    true,
                    cameraCapturerCompat!!,
                    LOCAL_VIDEO_TRACK_NAME
                )
                Objects.requireNonNull(localVideoTrack)?.addSink(localVideoView!!)

                /*
                 * If connected to a Room then share the local video track.
                 */if (localParticipant != null) {
                    localParticipant!!.publishTrack(localVideoTrack!!)

                    /*
                     * Update encoding parameters if they have changed.
                     */if (newEncodingParameters != encodingParameters) {
                        localParticipant!!.setEncodingParameters(newEncodingParameters)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /*
         * Update encoding parameters
         */encodingParameters = newEncodingParameters
    }

    override fun onPause() {
        Log.d("v_call_log", "Paused called")
        /*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         */
        if (localVideoTrack != null) {
            /*
             * If this local video track is being shared in a Room, unpublish from room before
             * releasing the video track. Participants will be notified that the track has been
             * unpublished.
             */
            if (localParticipant != null) {
                localParticipant!!.unpublishTrack(localVideoTrack!!)
            }
            localVideoTrack!!.release()
            localVideoTrack = null
        }
        super.onPause()
    }

    private fun showEndVideoConfirmation() {
        AlertDialog.Builder(mContext)
            .setIcon(android.R.drawable.ic_delete)
            .setTitle("Ending consultation")
            .setMessage("Are you sure you want to end this consultation?")
            .setPositiveButton("Yes") { dialog: DialogInterface?, which: Int -> exitVideoCall() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showUploadRecordDialogue() {
        val li = LayoutInflater.from(FourBaseCareApp.activityFromApp)
        val myView = li.inflate(R.layout.bottom_dialogue_camera_or_gallery, null)
        bottomCameraOrGalleryDIalogue = FragmentModalBottomSheet(myView)
        bottomCameraOrGalleryDIalogue!!.show(
            FourBaseCareApp.activityFromApp.supportFragmentManager,
            "BottomSheet Fragment"
        )

        val tvDialogueTitle = myView.findViewById<TextView>(R.id.tvDialogueTitle)
        val linGallery = myView.findViewById<LinearLayout>(R.id.linGallery)
        val linCamera = myView.findViewById<LinearLayout>(R.id.linCamera)

        tvDialogueTitle.setText("Upload prescription using")

        linGallery.setOnClickListener {
            if (!isDoubleClick()) {
                openGalleryForImage()
                Log.d("upload_record_log", "1")
                Constants.IS_FROM_CONSULTATION_SCREEN = true
            }
            bottomCameraOrGalleryDIalogue!!.dismiss()
        }
        linCamera.setOnClickListener {
            if (!isDoubleClick())
                Constants.IS_FROM_CONSULTATION_SCREEN = true
            /*CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                ScanReportFragment(), this, false
            )*/
            openCameraIntent()
            bottomCameraOrGalleryDIalogue!!.dismiss()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = FourBaseCareApp.activityFromApp.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun openCameraIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        Log.d("bottom_click_log","0")
        if (takePictureIntent.resolveActivity(FourBaseCareApp.activityFromApp.getPackageManager()) != null) {
            Log.d("bottom_click_log","1")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    Log.d("bottom_click_log","2")
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ignored: IOException) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            FourBaseCareApp.activityFromApp,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile
                        )

                        /*FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                            BuildConfig.APPLICATION_ID + ".provider", file);
                        */
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(takePictureIntent, Constants.START_CAMERA)
                        Log.d("open_cam_log","Cam opened ")
                    }
                } catch (e: Exception) {
                    Log.d("bottom_click_log","Err "+e.toString())
                    e.printStackTrace()
                    Log.d("open_cam_log","Err "+e.toString())
                }
            } else {
                Log.d("bottom_click_log","else called")
                startActivityForResult(takePictureIntent, Constants.START_CAMERA)
            }
        }
    }

    private fun openGalleryForImage() {
        Log.d("activity_log", "0")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        Log.d("activity_log", "0.1")
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
        Log.d("activity_log", "0.2")
    }

    private fun exitVideoCall() {
        Log.d("connect_room", "0 diconnect")
        if (videoRoom != null && videoRoom!!.state != Room.State.DISCONNECTED) {
            videoRoom!!.disconnect()
            Log.d("connect_room", "1")
            //            disconnectedFromOnDestroy = true;
        }

        if (localAudioTrack != null) {
            localAudioTrack!!.release()
            localAudioTrack = null
        }
        if (localVideoTrack != null) {
            localVideoTrack!!.release()
            localVideoTrack = null
        }

        val fm: FragmentManager = FourBaseCareApp.activityFromApp.getSupportFragmentManager()
        val count = fm.backStackEntryCount

        Log.d("connect_room", "disconnect 1")

        /*for (i in 0 until count) {
            fm.popBackStackImmediate()
            Log.d("connect_room", "remove i " + count)
            Log.d("connect_room", "remove COunt " + count)
        }*/
    }

    override fun onStop() {
        super.onStop()
        Log.d("connect_room", "Stop called")
    }


    override fun onDestroy() {
        Log.d("v_call_log", "Destroy called")
        configureAudio(false)
        intializeUI()

        if (videoRoom != null) {
            Log.d("v_call_log", "Video room state " + videoRoom!!.state)
        } else {
            Log.d("v_call_log", "Video is null")
        }
        if (videoRoom != null && videoRoom!!.state != Room.State.DISCONNECTED) {
            videoRoom!!.disconnect()
            Log.d("v_call_log", "Video room disconnected")
            //            disconnectedFromOnDestroy = true;
        }
        if (localAudioTrack != null) {
            localAudioTrack!!.release()
            localAudioTrack = null
        }
        if (localVideoTrack != null) {
            localVideoTrack!!.release()
            localVideoTrack = null
        }
        super.onDestroy()
    }

    private fun minimize() {
/*
        try {
            Log.d("pip_mode","0");
            mPictureInPictureParamsBuilder.setAspectRatio(new Rational(500, 250));
            Log.d("pip_mode","0.1");
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
            Log.d("pip_mode","0.2");
        } catch (Exception e) {
            Log.d("pip_mode","0.3 error "+e.toString());
            e.printStackTrace();
        }
*/
    }


    override fun onItemSelected(position: Int, item: AppointmentDetails, view: View) {}

    private fun startOneMinWaitForCall() {
        Log.d("timer_log", "1")
    }

    override fun onItemSelected(
        position: Int,
        item: Record,
        view: View,
        medicalRecordsAdapterNew: MedicalRecordsAdapterNew
    ) {
        showPDFFragment(item)
    }

    override fun onItemLongPress(
        position: Int,
        item: Record,
        view: View,
        medicalRecordsAdapterNew: MedicalRecordsAdapterNew
    ) {
        TODO("Not yet implemented")
    }

    private fun showPDFFragment(item: Record) {
        val bundle = Bundle()
        bundle.putString(Constants.SOURCE, Constants.EDIT_RECORD_FRAGMENT)
        bundle.putBoolean(Constants.SHOULD_SHOW_TITLE, false)
        bundle.putString(Constants.SERVER_FILE_URL, item.link)
        bundle.putString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_PRESCRIPTION)
        val fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
        fullScreenPDFViewFragment.arguments = bundle
        val transaction = FourBaseCareApp.activityFromApp.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frAppointment, fullScreenPDFViewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.relFragmentContainer.visibility = View.VISIBLE
        IS_VIEWING_DOCUMENT = true
    }

    companion object {
        //Video related declarations
        private const val LOCAL_AUDIO_TRACK_NAME = "mic"
        private const val LOCAL_VIDEO_TRACK_NAME = "camera"

        /*
     * You must provide a Twilio Access Token to connect to the Video service
     */
        private const val TWILIO_ACCESS_TOKEN = "f2377b35827c02d0f80b3a0ac16258f0"
        private const val ACCESS_TOKEN_SERVER = "http://172.104.174.175:3001"
        private const val CAMERA_MIC_PERMISSION_REQUEST_CODE = 1
    }

    private fun uploadPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        startActivityForResult(intent, 112)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("activity_log", "1 "+resultCode)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.START_CAMERA) {
                try {
                    Log.d("camera_img_log","captured image")
                    var imgBitmap: Bitmap?
                    imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BitmapFactory.decodeFile(mCurrentPhotoPath)
                    } else {
                        val extras = data!!.extras
                        extras!!["data"] as Bitmap?
                    }
                    try {
                        if (imgBitmap != null) {
                            while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                                imgBitmap = getResizedBitmap(imgBitmap)
                                Log.d("camera_img_log","Got resized img")
                            }
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                    // start cropping activity for pre-acquired image saved on the device
                    CropImage.activity(mImageUri)
                        .start(FourBaseCareApp.activityFromApp, this)



                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }else if(requestCode == Constants.PICK_GALLERY_IMAGE){
                val uri = data!!.data
                mCurrentPhotoPath = FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                var imgBitmap: Bitmap?
                imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { BitmapFactory.decodeFile(mCurrentPhotoPath)
                } else {
                    val extras = data!!.extras
                    extras!!["data"] as Bitmap?
                }
                try {
                    if (imgBitmap != null) {
                        while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                            imgBitmap = getResizedBitmap(imgBitmap)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(mImageUri)
                    .start(FourBaseCareApp.activityFromApp, this)


            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if(data != null) {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        Log.d("camera_img_log","cropped img")
                        val resultUri: Uri = result.getUri()
                        Log.d("camera_img_log","on result path "+resultUri.path)
                        var bundle  = Bundle()
                        bundle.putString(Constants.IMAGE_PATH,resultUri.toString())
                        bundle.putInt(Constants.DOC_MODE,Constants.PHOTO_MODE)
                        bundle.putString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_PRESCRIPTION)
                        var fullScreenPDFViewFragment  = FullScreenPDFViewFragmentKt()
                        fullScreenPDFViewFragment.arguments = bundle
                        CommonMethods.addNextFragment(FourBaseCareApp.activityFromApp, fullScreenPDFViewFragment,this,false)
                        Log.d("camera_img_log","Got crop uri")


                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error: java.lang.Exception = result.getError()
                        CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                    }
                }
            }else{
                val uri = data!!.data
                try {
                    Log.d("extract_time", "File scelected")
                    val fullPath1: String =
                        FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
                    Log.d("file_path", "Full path $fullPath1")

                    var bundle = Bundle()
                    bundle.putString(Constants.PDF_PATH,fullPath1)
                    bundle.putInt(Constants.DOC_MODE,Constants.PDF_MODE)
                    bundle.putString(Constants.RECORD_TYPE, Constants.RECORD_TYPE_PRESCRIPTION)
                    var fullScreenPDFViewFragment = FullScreenPDFViewFragmentKt()
                    fullScreenPDFViewFragment.arguments = bundle

                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        fullScreenPDFViewFragment, this, false
                    )


                } catch (e: Exception) {
                    /*  Toast.makeText(
                          FourBaseCareApp.activityFromApp,
                          "There was an error getting data",
                          Toast.LENGTH_SHORT
                      ).show()*/

                }
            }

        }

    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        return try {
            Log.d("camera_img_log","Resizing started")
            val width = image.width / 2
            val height = image.height / 2
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: NullPointerException) {
            Log.d("camera_img_log","Resize error")
            image
        }
    }

    fun getImageUri(
        inContext: Context?,
        inImage: Bitmap?
    ): Uri? {
        Log.d("img_crash_log","0")
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        Log.d("img_crash_log","1")
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        Log.d("img_crash_log","Path "+path)
        return Uri.parse(path)
    }

    private fun showUploadPrescriptionDialogue() {

        Log.d("upload_record_log", "0")
        if (::uploadPrescriptionsDialogue.isInitialized && uploadPrescriptionsDialogue.isShowing) {
            return // DOnt open dialogue if its alreaddy open
        }


        uploadPrescriptionsDialogue = Dialog(FourBaseCareApp.activityFromApp)
        uploadPrescriptionsDialogue.setContentView(R.layout.tags_confirm_dialogue)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(uploadPrescriptionsDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = uploadPrescriptionsDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        uploadPrescriptionsDialogue.window?.setAttributes(lp)
        uploadPrescriptionsDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnNo: TextView = uploadPrescriptionsDialogue.findViewById(R.id.btnNo)
        val tvTitleText: TextView = uploadPrescriptionsDialogue.findViewById(R.id.tvTitleText)
        val btnYes: TextView = uploadPrescriptionsDialogue.findViewById(R.id.btnYes)

        tvTitleText.setText("Would you like to upload prescription?")
        btnNo.setText("No")

        btnYes.setOnClickListener(View.OnClickListener {
            uploadPrescriptionsDialogue.dismiss()
            showUploadRecordDialogue()
            Log.d("upload_record_log", "0")


        })

        btnNo.setOnClickListener(View.OnClickListener {
            uploadPrescriptionsDialogue.dismiss()
            fragmentManager?.popBackStack()
        })
        uploadPrescriptionsDialogue.show()

    }
}