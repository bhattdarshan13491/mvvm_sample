package com.oncobuddy.app.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.oncobuddy.app.FourBaseCareApp;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.injectors.AppointmentInjection;
import com.oncobuddy.app.models.pojo.login_response.LoginDetails;
import com.oncobuddy.app.models.pojo.twilio_token.TokenResponse;
import com.oncobuddy.app.services.RingtonePlayingService;
import com.oncobuddy.app.utils.CameraCapturerCompat;
import com.oncobuddy.app.utils.CommonMethods;
import com.oncobuddy.app.utils.Constants;
import com.oncobuddy.app.utils.base_classes.BaseActivity;
import com.oncobuddy.app.view_models.AppointmentViewModel;
import com.twilio.video.AudioCodec;
import com.twilio.video.ConnectOptions;
import com.twilio.video.EncodingParameters;
import com.twilio.video.G722Codec;
import com.twilio.video.H264Codec;
import com.twilio.video.IsacCodec;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.OpusCodec;
import com.twilio.video.PcmaCodec;
import com.twilio.video.PcmuCodec;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import com.twilio.video.VideoCodec;
import com.twilio.video.VideoTrack;
import com.twilio.video.VideoView;
import com.twilio.video.Vp8Codec;
import com.twilio.video.Vp9Codec;

import java.util.Collections;
import java.util.Objects;

import tvi.webrtc.VideoSink;


public class VideoByPatientActivity extends BaseActivity {

    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "VideoByDoctorActivity";
    /*
     * Audio and video tracks can be created with names. This feature is useful for categorizing
     * tracks of participants. For example, if one participant publishes a video track with
     * ScreenCapturer and CameraCapturer with the names "screen" and "camera" respectively then
     * other participants can use RemoteVideoTrack#getName to determine which video track is
     * produced from the other participant's screen or camera.
     */
    private static final String LOCAL_AUDIO_TRACK_NAME = "mic";
    private static final String LOCAL_VIDEO_TRACK_NAME = "camera";
    /*
     * You must provide a Twilio Access Token to connect to the Video service
     */
    private static final String TWILIO_ACCESS_TOKEN = "f2377b35827c02d0f80b3a0ac16258f0";
    private static final String ACCESS_TOKEN_SERVER = "http://172.104.174.175:3000";
    private Context mContext = VideoByPatientActivity.this;
    private TextView tvCallStatus;
    /*
     * Access token used to connect. This field will be set either from the console generated token
     * or the request to the token server.
     */
    private String accessToken = TWILIO_ACCESS_TOKEN;
    private String roomName = "";
    //    private MediaPlayer mMediaPlayer;
//    private Vibrator vibrator;
    /*
     * A Room represents communication between a local participant and one or more participants.
     */
    private Room room;
    private LocalParticipant localParticipant;

    /*
     * AudioCodec and VideoCodec represent the preferred codec for encoding and decoding audio and
     * video.
     */
    private AudioCodec audioCodec;
    private VideoCodec videoCodec;

    /*
     * Encoding parameters represent the sender side bandwidth constraints.
     */
    private EncodingParameters encodingParameters;

    /*
     * A VideoView receives frames from a local or remote video track and renders them
     * to an associated view.
     * to an associated view.
     */
    private VideoView primaryVideoView;
    private VideoView thumbnailVideoView;
    private VideoView guestThumbnailVideoView;

    /*
     * Android shared preferences used for settings
     */
    private SharedPreferences preferences;

    /*
     * Android application UI elements
     */
    private TextView videoStatusTextView;
    private CameraCapturerCompat cameraCapturerCompat;
    private LocalAudioTrack localAudioTrack;
    private LocalVideoTrack localVideoTrack;
    private FloatingActionButton connectActionFab, disconnectActionFab;
    private FloatingActionButton switchCameraActionFab;
    private FloatingActionButton localVideoActionFab;
    private FloatingActionButton muteActionFab;
    private FloatingActionButton volumeActionFab;
    private AlertDialog connectDialog;
    private AudioManager audioManager;
    private String remoteParticipantIdentity;
    private String guestParticipantIdentity;
    private int previousAudioMode;
    private boolean previousMicrophoneMute;
    private VideoSink localVideoView;
    private ProgressBar mProgressBar;
    private boolean canCall = false;
    private String parentClassName;
    private String doctorName = null;
    private String appointmentId = "1";

    private LoginDetails userObj;
    private AppointmentViewModel appointmentViewModel;
    private boolean isCallReceived = false;
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean IS_TOKEN_GENERATED = false;



    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_patient);

        Log.d("call_service_check", "came in patient activity");

        parentClassName = getIntent().getStringExtra("class_name");
        doctorName = getIntent().getStringExtra("doctor_name");
        appointmentId = getIntent().getStringExtra(Constants.APPOINTMENT_ID);
        Log.d("call_service_check", "appointment_id "+appointmentId);
        Log.d("call_service_check", "doctor name "+doctorName);

        primaryVideoView = findViewById(R.id.primary_video_view);
        thumbnailVideoView = findViewById(R.id.thumbnail_video_view);
        guestThumbnailVideoView = findViewById(R.id.guest_thumbnail_video_view);
        videoStatusTextView = findViewById(R.id.video_status_textview);
        tvCallStatus = findViewById(R.id.tvCallStatus);

        tvCallStatus.setText("Doctor is calling");
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        connectActionFab = findViewById(R.id.connect_action_fab);
        connectActionFab.hide();
        disconnectActionFab = findViewById(R.id.disconnect_action_fab);
        switchCameraActionFab = findViewById(R.id.switch_camera_action_fab);
        localVideoActionFab = findViewById(R.id.local_video_action_fab);
        muteActionFab = findViewById(R.id.mute_action_fab);
        volumeActionFab = findViewById(R.id.volume_action_fab);
        videoStatusTextView.setVisibility(View.GONE);

        setupVM();
        setupObservers();



        Gson gson = new Gson();
        String userJson = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "");
        userObj  = gson.fromJson(userJson, LoginDetails.class);

        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();

        // to release screen lock
        KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        }

        /*
         * Get shared preferences to read settings
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*
         * Enable changing the volume using the up/down keys during a conversation
         */
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        /*
         * Needed for setting/abandoning audio focus during call
         */
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        /*
         * Check camera and microphone permissions. Needed in Android M.
         */
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        } else {
            createAudioAndVideoTracks();
            Log.d("connect_room ","get token api called");
            getNewToken();
            //connectToRoom("a");
        }

        /*
         * Set the initial state of the UI
         */
        intializeUI();
        //getNewToken();
        startOneMinWaitForCall();

        startVibration();


    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //ibrator.vibrate(60000);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mediaPlayer = MediaPlayer.create(VideoByPatientActivity.this, notification);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } else {
            //deprecated in API 26
            long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
            vibrator.vibrate(pattern, 0);
        }
    }

    private void setupVM() {
        appointmentViewModel = new ViewModelProvider(
                VideoByPatientActivity.this,
                AppointmentInjection.provideViewModelFactory()
        ).get(AppointmentViewModel.class);
    }

    private void setupObservers() {
        //appointmentViewModel.getTokenResponseData.observe(this, responseObserver);
//        allDBAppointmentsList.observe(this, localDbDataObserver)
        appointmentViewModel.isViewLoading.observe(this, aBoolean -> {
            if(aBoolean){
                mProgressBar.setVisibility(View.VISIBLE);
            }else{
                mProgressBar.setVisibility(View.GONE);
            }

        });

        appointmentViewModel.onMessageError.observe(VideoByPatientActivity.this, s -> CommonMethods.showToast(VideoByPatientActivity.this,s));

        appointmentViewModel.getTokenResponseData.observe(VideoByPatientActivity.this, new Observer<TokenResponse>() {
            @Override
            public void onChanged(TokenResponse tokenResponse) {
                accessToken = tokenResponse.getPayLoad().getToken();
                IS_TOKEN_GENERATED = true;
                //connectToRoom(userObj.getFirstName());
                roomName = tokenResponse.getPayLoad().getRoomName();
                Log.d("call_log","Token Generated "+accessToken);
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
       *//* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_activity, menu);*//*
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }
*/
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            boolean cameraAndMicPermissionGranted = true;

            for (int grantResult : grantResults) {
                cameraAndMicPermissionGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
            }

            if (cameraAndMicPermissionGranted) {
                createAudioAndVideoTracks();
                getNewToken();
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.permissions_needed,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("alert_log","resume called");

        if (!isMyServiceRunning(RingtonePlayingService.class)) {
            Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
            mContext.stopService(stopIntent);

            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            Intent startIntent = new Intent(mContext, RingtonePlayingService.class);
            startIntent.putExtra("ringtone-uri", ringtoneUri.toString());
            mContext.startService(startIntent);
        }

        /*
         * Update preferred audio and video codec in case changed in settings
         */
        audioCodec = getAudioCodecPreference(Constants.PREF_AUDIO_CODEC,
                Constants.PREF_AUDIO_CODEC_DEFAULT);
        videoCodec = getVideoCodecPreference(Constants.PREF_VIDEO_CODEC,
                Constants.PREF_VIDEO_CODEC_DEFAULT);

        /*
         * Get latest encoding parameters
         */
        final EncodingParameters newEncodingParameters = getEncodingParameters();

        /*
         * If the local video track was released when the app was put in the background, recreate.
         */
        Log.d("destroy_log","resume 0");
        if (localVideoTrack == null && checkPermissionForCameraAndMicrophone()) {
            localVideoTrack = LocalVideoTrack.create(VideoByPatientActivity.this,
                    true,
                    cameraCapturerCompat,
                    LOCAL_VIDEO_TRACK_NAME);
            /*Objects.requireNonNull(localVideoTrack).addRenderer(localVideoView);*/
            localVideoTrack.addSink(localVideoView);

            /*
             * If connected to a Room then share the local video track.
             */
            if (localParticipant != null) {
                Log.d("destroy_log","resume 1");
                localParticipant.publishTrack(localVideoTrack);
                /*
                 * Update encoding parameters if they have changed.
                 */
                if (!newEncodingParameters.equals(encodingParameters)) {
                    Log.d("destroy_log","resume 2");

                    localParticipant.setEncodingParameters(newEncodingParameters);
                }
            }
        }

        /*
         * Update encoding parameters
         */
        encodingParameters = newEncodingParameters;
        Log.d("destroy_log","resume 3");

    }

    /*@Override
    protected void onStop() {
        super.onStop();
        Log.d("destroy_log","stop called");
    }

    @Override
    protected void onPause() {
        Log.d("destroy_log","pause called");
        *//*
         * Release the local video track before going in the background. This ensures that the
         * camera can be used by other applications while this app is in the background.
         *//*
        if (localVideoTrack != null) {

            if (localParticipant != null) {
                localParticipant.unpublishTrack(localVideoTrack);
            }

            localVideoTrack.release();
            localVideoTrack = null;
        }

        super.onPause();
    }*/

    @Override
    protected void onDestroy() {
        Log.d("destroy_log","Destroy called");
        Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
        mContext.stopService(stopIntent);

        stopVibration();
        configureAudio(false);
        /*
         * Always disconnect from the room before leaving the Activity to
         * ensure any memory allocated to the Room resource is freed.
         */
        if (room != null && room.getState() != Room.State.DISCONNECTED) {
            room.disconnect();
        }

        /*
         * Release the local audio and video tracks ensuring any memory allocated to audio
         * or video is freed.
         */
        if (localAudioTrack != null) {
            localAudioTrack.release();
            localAudioTrack = null;
        }
        if (localVideoTrack != null) {
            localVideoTrack.release();
            localVideoTrack = null;
        }

        super.onDestroy();
    }

    private void stopVibration() {
        if(vibrator != null )vibrator.cancel();
        if(mediaPlayer != null){
                mediaPlayer.release();
        }
    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(VideoByPatientActivity.this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(VideoByPatientActivity.this, Manifest.permission.RECORD_AUDIO);
        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VideoByPatientActivity.this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(VideoByPatientActivity.this,
                        Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(getApplicationContext(),
                    R.string.permissions_needed,
                    Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                    VideoByPatientActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    protected void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() == Constants.CALL_RECEIVE_ACTION) {
            Log.d("call_service_check", "Came here");
        }
    }

    private void createAudioAndVideoTracks() {
        // Share your microphone
        localAudioTrack = LocalAudioTrack.create(VideoByPatientActivity.this, true, LOCAL_AUDIO_TRACK_NAME);

        // Share your camera
        cameraCapturerCompat = new CameraCapturerCompat(VideoByPatientActivity.this, CameraCapturerCompat.Source.FRONT_CAMERA);
        localVideoTrack = LocalVideoTrack.create(VideoByPatientActivity.this,
                true,
                cameraCapturerCompat,
                LOCAL_VIDEO_TRACK_NAME);
        primaryVideoView.setMirror(true);
        localVideoTrack.addSink(primaryVideoView);
        localVideoView = primaryVideoView;
    }

    private void connectToRoom() {
        stopVibration();
//        if (mMediaPlayer != null) {
//            mMediaPlayer.release();
//        }

        Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
        mContext.stopService(stopIntent);

        switchCameraActionFab.show();
        switchCameraActionFab.setOnClickListener(switchCameraClickListener());
        localVideoActionFab.show();
        localVideoActionFab.setOnClickListener(localVideoClickListener());
        muteActionFab.show();
        muteActionFab.setOnClickListener(muteClickListener());
        volumeActionFab.show();
        volumeActionFab.setOnClickListener(volumeClickListener());

        configureAudio(true);

        ConnectOptions.Builder connectOptionsBuilder = new ConnectOptions
                .Builder(accessToken)
                .roomName(roomName);

        /*
         * Add local audio track to connect options to share with participants.
         */
        if (localAudioTrack != null) {
            connectOptionsBuilder
                    .audioTracks(Collections.singletonList(localAudioTrack));
        }

        /*
         * Add local video track to connect options to share with participants.
         */
        if (localVideoTrack != null) {
            connectOptionsBuilder.videoTracks(Collections.singletonList(localVideoTrack));
        }

        /*
         * Set the preferred audio and video codec for media.
         */
        connectOptionsBuilder.preferAudioCodecs(Collections.singletonList(audioCodec));
        connectOptionsBuilder.preferVideoCodecs(Collections.singletonList(videoCodec));

        /*
         * Set the sender side encoding parameters.
         */
        connectOptionsBuilder.encodingParameters(encodingParameters);

        room = Video.connect(VideoByPatientActivity.this, connectOptionsBuilder.build(), roomListener());
        Log.d("connect_room","Connect room data room name "+roomName);
        Log.d("connect_room","Connect room data token "+accessToken);
        setDisconnectAction();
    }

    /*
     * The initial state when there is no active room.
     */
    private void intializeUI() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.release();
//        }
        Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
        mContext.stopService(stopIntent);

        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Intent startIntent = new Intent(mContext, RingtonePlayingService.class);
        startIntent.putExtra("ringtone-uri", ringtoneUri.toString());
        mContext.startService(startIntent);

        disconnectActionFab.hide();
        connectActionFab.show();
        connectActionFab.setOnClickListener(v -> {
           /* if (canCall) {
                connectToRoom(userObj.getFirstName());
            } else {
                CommonMethods.showToast(VideoByPatientActivity.this,  "No Patient Data Available");
            }*/
            if(IS_TOKEN_GENERATED){
                isCallReceived = true;
                connectToRoom();
            }

            //getNewToken();
        });
 /*       if (parentClassName != null) {
            if (!parentClassName.equalsIgnoreCase("fcm")) {
                try {
                    connectToRoom();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                connectToRoom();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        switchCameraActionFab.hide();
        localVideoActionFab.hide();
        muteActionFab.hide();
        volumeActionFab.hide();
//        try {
//            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer.reset();
//            mMediaPlayer.setDataSource(this, ringtoneUri);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("Video Patient", e.toString());
//        }


    }

    /*
     * Get the preferred audio codec from shared preferences
     */
    private AudioCodec getAudioCodecPreference(String key, String defaultValue) {
        final String audioCodecName = preferences.getString(key, defaultValue);

        switch (audioCodecName) {
            case IsacCodec.NAME:
                return new IsacCodec();
            case OpusCodec.NAME:
                return new OpusCodec();
            case PcmaCodec.NAME:
                return new PcmaCodec();
            case PcmuCodec.NAME:
                return new PcmuCodec();
            case G722Codec.NAME:
                return new G722Codec();
            default:
                return new OpusCodec();
        }
    }

    /*
     * Get the preferred video codec from shared preferences
     */
    private VideoCodec getVideoCodecPreference(String key, String defaultValue) {
        final String videoCodecName = preferences.getString(key, defaultValue);

        switch (videoCodecName) {
            case Vp8Codec.NAME:
                boolean simulcast = preferences.getBoolean(Constants.PREF_VP8_SIMULCAST,
                        Constants.PREF_VP8_SIMULCAST_DEFAULT);
                return new Vp8Codec(simulcast);
            case H264Codec.NAME:
                return new H264Codec();
            case Vp9Codec.NAME:
                return new Vp9Codec();
            default:
                return new Vp8Codec();
        }
    }

    private EncodingParameters getEncodingParameters() {
        final int maxAudioBitrate = Integer.parseInt(
                Objects.requireNonNull(preferences.getString(Constants.PREF_SENDER_MAX_AUDIO_BITRATE,
                        Constants.PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT)));
        final int maxVideoBitrate = Integer.parseInt(
                Objects.requireNonNull(preferences.getString(Constants.PREF_SENDER_MAX_VIDEO_BITRATE,
                        Constants.PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT)));

        return new EncodingParameters(maxAudioBitrate, maxVideoBitrate);
    }

    /*
     * The actions performed during disconnect.
     */
    private void setDisconnectAction() {
        connectActionFab.hide();
        disconnectActionFab.show();
        disconnectActionFab.setOnClickListener(disconnectClickListener());
    }

    /*
     * Called when remote participant joins the room
     */
    private void addRemoteParticipant(RemoteParticipant remoteParticipant) {
        /*
         * This app only displays video for one additional participant per Room
         */
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            guestParticipantIdentity = remoteParticipant.getIdentity();
            Log.d("multiple_connect","second participant "+guestParticipantIdentity);
            videoStatusTextView.setText(guestParticipantIdentity + " joined");
            Log.d("multiple_connect","video tracks size "+remoteParticipant.getRemoteVideoTracks().size());

            if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
                RemoteVideoTrackPublication remoteVideoTrackPublication =
                        remoteParticipant.getRemoteVideoTracks().get(0);

                Log.d("multiple_connect","guest subscribed  "+remoteVideoTrackPublication.isTrackSubscribed());

                remoteParticipant.setListener(guestRemoteParticipantListener());
            }

        }
        if (thumbnailVideoView.getVisibility() != View.VISIBLE) {
            Log.d("multiple_connect","first participant connected");
            remoteParticipantIdentity = remoteParticipant.getIdentity();
            videoStatusTextView.setText(remoteParticipantIdentity + " joined");

            /*
             * Add remote participant renderer
             */
            if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
                RemoteVideoTrackPublication remoteVideoTrackPublication =
                        remoteParticipant.getRemoteVideoTracks().get(0);

                /*
                 * Only render video tracks that are subscribed to
                 */
                Log.d("multiple_connect","patient track subscribed  "+remoteVideoTrackPublication.isTrackSubscribed());
                /*if (remoteVideoTrackPublication.isTrackSubscribed()) {
                    Log.d("multiple_connect","patient track subscribed  Yes");
                    addRemoteParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.getRemoteVideoTrack()));
                }*/
            }

            /*
             * Start listening for participant events
             */
            remoteParticipant.setListener(remoteParticipantListener());
        }
    }

    private RemoteParticipant.Listener guestRemoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i("multiple_connect", String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onAudioTrackPublished");
            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i("multiple_connect", String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onAudioTrackUnpublished");
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i("multiple_connect", String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onDataTrackPublished");
            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i("multiple_connect", String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onDataTrackUnpublished");
            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i("multiple_connect", String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onVideoTrackPublished");
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i("multiple_connect", String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
//                videoStatusTextView.setText("onVideoTrackUnpublished");
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i("multiple_connect", String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
//                videoStatusTextView.setText("onAudioTrackSubscribed");
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i("multiple_connect", String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
//                videoStatusTextView.setText("onAudioTrackUnsubscribed");
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i("multiple_connect", String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
//                videoStatusTextView.setText("onAudioTrackSubscriptionFailed");
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i("multiple_connect", String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
                // videoStatusTextView.setText("onDataTrackSubscribed");
            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i("multiple_connect", String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
//                videoStatusTextView.setText("onDataTrackUnsubscribed");
            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i("multiple_connect", String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
//                videoStatusTextView.setText("onDataTrackSubscriptionFailed");
            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i("multiple_connect", String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                // videoStatusTextView.setText("onVideoTrackSubscribed");
                Log.d("multiple_connect","On patient track subscribed  Yes");
                addGuestRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i("multiple_connect", String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
//                videoStatusTextView.setText("onVideoTrackUnsubscribed");
                removeGuestParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i("multiple_connect", String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
//                videoStatusTextView.setText("onVideoTrackSubscriptionFailed");

            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }

    private void removeGuestParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeSink(guestThumbnailVideoView);
    }

    private void addGuestRemoteParticipantVideo(VideoTrack videoTrack) {
        Log.d("multiple_connect","add guest 01");
        showGuestVideoThumbnail();
        primaryVideoView.setMirror(false);
        videoTrack.addSink(guestThumbnailVideoView);
        Log.d("multiple_connect","add guest 02");
    }

    private void showGuestVideoThumbnail() {
        try {
            if (guestThumbnailVideoView.getVisibility() == View.GONE) {
                guestThumbnailVideoView.setVisibility(View.VISIBLE);

            }
        } catch (Exception e) {
            Log.d("multiple_connect","show guest err "+e.toString());
            e.printStackTrace();
        }
    }

    /*
     * Set primary view as renderer for participant video track
     */
    private void addRemoteParticipantVideo(VideoTrack videoTrack) {
        moveLocalVideoToThumbnailView();
        primaryVideoView.setMirror(false);
        videoTrack.addSink(primaryVideoView);
        tvCallStatus.setText("Doctor "+doctorName+" is connected");
    }

    private void moveLocalVideoToThumbnailView() {
        try {
            if (thumbnailVideoView.getVisibility() == View.GONE) {
                thumbnailVideoView.setVisibility(View.VISIBLE);
                localVideoTrack.removeSink(primaryVideoView);
                localVideoTrack.addSink(thumbnailVideoView);
                localVideoView = thumbnailVideoView;
                thumbnailVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                        CameraCapturerCompat.Source.FRONT_CAMERA);
            }else{
                Log.d("destroy_log","THumbnail is not gone "+thumbnailVideoView.getVisibility());
            }
        } catch (Exception e) {
            Log.d("destroy_log","Err "+e.toString());
            e.printStackTrace();
        }
    }

    /*
     * Called when remote participant leaves the room
     */
    private void removeRemoteParticipant(RemoteParticipant remoteParticipant) {
        videoStatusTextView.setText(remoteParticipant.getIdentity() +
                " left.");
        if (!remoteParticipant.getIdentity().equals(remoteParticipantIdentity)) {
            return;
        }

        configureAudio(false);
//        intializeUI();

        /*
         * Remove remote participant renderer
         */
        if (!remoteParticipant.getRemoteVideoTracks().isEmpty()) {
            RemoteVideoTrackPublication remoteVideoTrackPublication =
                    remoteParticipant.getRemoteVideoTracks().get(0);

            /*
             * Remove video only if subscribed to participant track
             */
            if (remoteVideoTrackPublication.isTrackSubscribed()) {
                removeParticipantVideo(Objects.requireNonNull(remoteVideoTrackPublication.getRemoteVideoTrack()));
            }
        }
        moveLocalVideoToPrimaryView();
    }

    private void removeParticipantVideo(VideoTrack videoTrack) {
        videoTrack.removeSink(primaryVideoView);
    }

    private void moveLocalVideoToPrimaryView() {
        if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
            thumbnailVideoView.setVisibility(View.GONE);
            guestThumbnailVideoView.setVisibility(View.GONE);
            if (localVideoTrack != null) {
                localVideoTrack.removeSink(thumbnailVideoView);
                localVideoTrack.addSink(primaryVideoView);
            }
            localVideoView = primaryVideoView;
            primaryVideoView.setMirror(cameraCapturerCompat.getCameraSource() ==
                    CameraCapturerCompat.Source.FRONT_CAMERA);
        }
    }

    /*
     * Room events listener
     */
    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                setDisconnectAction();
                localParticipant = room.getLocalParticipant();
                setTitle(room.getName());
                //tvCallStatus.setText(String.format("Dr. %s is connected", doctorName));
                  tvCallStatus.setText("Connecting...");
                Log.d("multiple_connect","total participants "+room.getRemoteParticipants().size());


                if(guestThumbnailVideoView.getVisibility() == View.GONE && room.getRemoteParticipants().size() > 1){
                    RemoteParticipant remoteParticipant = room.getRemoteParticipants().get(1);
                    guestParticipantIdentity = remoteParticipant.getIdentity();
                    Log.d("multiple_connect","second participant "+guestParticipantIdentity);
                    videoStatusTextView.setText("Guest joined");
                    Log.d("multiple_connect","video tracks size "+remoteParticipant.getRemoteVideoTracks().size());

                    if (remoteParticipant.getRemoteVideoTracks().size() > 0) {
                        RemoteVideoTrackPublication remoteVideoTrackPublication =
                                remoteParticipant.getRemoteVideoTracks().get(0);

                        Log.d("multiple_connect","guest subscribed  "+remoteVideoTrackPublication.isTrackSubscribed());

                        remoteParticipant.setListener(guestRemoteParticipantListener());
                    }


                }
                    for (RemoteParticipant remoteParticipant : room.getRemoteParticipants()) {
                        addRemoteParticipant(remoteParticipant);
                        break;
                    }


            }

            @Override
            public void onConnectFailure(Room room, TwilioException e) {
                configureAudio(false);
                Log.d("connect_room",""+e.getExplanation().toString());
                Log.d("connect_room","Details "+e.toString());
                //Log.d("call_connect_Err ",""+e.getCause().toString());
                Log.d("connect_room",""+e.getMessage());
                Log.d("connect_room",""+e.getCode());
                Toast.makeText(VideoByPatientActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                intializeUI();
            }

            @Override
            public void onReconnecting(@NonNull Room room, @NonNull TwilioException twilioException) {
                Log.d("connect_room","Reconnect "+twilioException.getMessage());
                setDisconnectAction();
            }

            @Override
            public void onReconnected(@NonNull Room room) {
                Log.d("connect_room","Reconnected ");
                setDisconnectAction();
            }

            @Override
            public void onDisconnected(Room room, TwilioException e) {
                Log.d("connect_room","Disconnected");
                localParticipant = null;
//                if (mMediaPlayer != null) {
//                    mMediaPlayer.release();
//                }
                room = null;
                configureAudio(false);
//                intializeUI();
                moveLocalVideoToPrimaryView();
            }

            @Override
            public void onParticipantConnected(Room room, RemoteParticipant remoteParticipant) {
                addRemoteParticipant(remoteParticipant);
                setDisconnectAction();
                Log.d("multiple_connect","participant connected");
            }

            @Override
            public void onParticipantDisconnected(Room room, RemoteParticipant remoteParticipant) {
                removeRemoteParticipant(remoteParticipant);
                Log.d("connect_room","P Disconnected");
                Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
                mContext.stopService(stopIntent);

                CommonMethods.showToast(VideoByPatientActivity.this, "Doctor Left the Call");
                finish();
            }

            @Override
            public void onRecordingStarted(Room room) {
                /*
                 * Indicates when media shared to a Room is being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("connect_room", "onRecordingStarted");
            }

            @Override
            public void onRecordingStopped(Room room) {
                /*
                 * Indicates when media shared to a Room is no longer being recorded. Note that
                 * recording is only available in our Group Rooms developer preview.
                 */
                Log.d("connect_room", "onRecordingStopped");
            }
        };
    }

    private RemoteParticipant.Listener remoteParticipantListener() {
        return new RemoteParticipant.Listener() {
            @Override
            public void onAudioTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
            }

            @Override
            public void onAudioTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteAudioTrackPublication remoteAudioTrackPublication) {
                Log.i(TAG, String.format("onAudioTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.isTrackEnabled(),
                        remoteAudioTrackPublication.isTrackSubscribed(),
                        remoteAudioTrackPublication.getTrackName()));
            }

            @Override
            public void onDataTrackPublished(RemoteParticipant remoteParticipant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
            }

            @Override
            public void onDataTrackUnpublished(RemoteParticipant remoteParticipant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {
                Log.i(TAG, String.format("onDataTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.isTrackEnabled(),
                        remoteDataTrackPublication.isTrackSubscribed(),
                        remoteDataTrackPublication.getTrackName()));
            }

            @Override
            public void onVideoTrackPublished(RemoteParticipant remoteParticipant,
                                              RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackPublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
            }

            @Override
            public void onVideoTrackUnpublished(RemoteParticipant remoteParticipant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {
                Log.i(TAG, String.format("onVideoTrackUnpublished: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%s, enabled=%b, " +
                                "subscribed=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.isTrackEnabled(),
                        remoteVideoTrackPublication.isTrackSubscribed(),
                        remoteVideoTrackPublication.getTrackName()));
            }

            @Override
            public void onAudioTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
            }

            @Override
            public void onAudioTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                 RemoteAudioTrack remoteAudioTrack) {
                Log.i(TAG, String.format("onAudioTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrack: enabled=%b, playbackEnabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrack.isEnabled(),
                        remoteAudioTrack.isPlaybackEnabled(),
                        remoteAudioTrack.getName()));
            }

            @Override
            public void onAudioTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onAudioTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteAudioTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteAudioTrackPublication.getTrackSid(),
                        remoteAudioTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
            }

            @Override
            public void onDataTrackSubscribed(RemoteParticipant remoteParticipant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
            }

            @Override
            public void onDataTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                RemoteDataTrackPublication remoteDataTrackPublication,
                                                RemoteDataTrack remoteDataTrack) {
                Log.i(TAG, String.format("onDataTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrack.isEnabled(),
                        remoteDataTrack.getName()));
            }

            @Override
            public void onDataTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                      RemoteDataTrackPublication remoteDataTrackPublication,
                                                      TwilioException twilioException) {
                Log.i(TAG, String.format("onDataTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteDataTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteDataTrackPublication.getTrackSid(),
                        remoteDataTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
            }

            @Override
            public void onVideoTrackSubscribed(RemoteParticipant remoteParticipant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackSubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                addRemoteParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackUnsubscribed(RemoteParticipant remoteParticipant,
                                                 RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                 RemoteVideoTrack remoteVideoTrack) {
                Log.i(TAG, String.format("onVideoTrackUnsubscribed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrack: enabled=%b, name=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrack.isEnabled(),
                        remoteVideoTrack.getName()));
                removeParticipantVideo(remoteVideoTrack);
            }

            @Override
            public void onVideoTrackSubscriptionFailed(RemoteParticipant remoteParticipant,
                                                       RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                       TwilioException twilioException) {
                Log.i(TAG, String.format("onVideoTrackSubscriptionFailed: " +
                                "[RemoteParticipant: identity=%s], " +
                                "[RemoteVideoTrackPublication: sid=%b, name=%s]" +
                                "[TwilioException: code=%d, message=%s]",
                        remoteParticipant.getIdentity(),
                        remoteVideoTrackPublication.getTrackSid(),
                        remoteVideoTrackPublication.getTrackName(),
                        twilioException.getCode(),
                        twilioException.getMessage()));
                Snackbar.make(disconnectActionFab,
                        String.format("Failed to subscribe to %s video track",
                                remoteParticipant.getIdentity()),
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onAudioTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onAudioTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {

            }

            @Override
            public void onVideoTrackEnabled(RemoteParticipant remoteParticipant,
                                            RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }

            @Override
            public void onVideoTrackDisabled(RemoteParticipant remoteParticipant,
                                             RemoteVideoTrackPublication remoteVideoTrackPublication) {

            }
        };
    }



    private View.OnClickListener disconnectClickListener() {
        return v -> {
            if (room != null) {
                room.disconnect();
            }
            stopVibration();
            Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
            mContext.stopService(stopIntent);
            appointmentViewModel.callEndCall(userObj.getAuthToken(),""+appointmentId);
//            intializeUI();
            finish();
        };
    }

    private void startOneMinWaitForCall(){
        Log.d("timer_log","1");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("timer_log","2 6 seconds completed "+isCallReceived);
                if(!isCallReceived){
                    if (room != null) {
                        room.disconnect();
                    }
                    stopVibration();
                    Intent stopIntent = new Intent(mContext, RingtonePlayingService.class);
                    mContext.stopService(stopIntent);
                    finish();

                }
            }
        }, Constants.RING_WAITING_TIME * 1000);//your time
    }



    private View.OnClickListener switchCameraClickListener() {
        return v -> {
            if (cameraCapturerCompat != null) {
                if (cameraCapturerCompat != null) {
                    CameraCapturerCompat.Source cameraSource = cameraCapturerCompat.getCameraSource();
                    cameraCapturerCompat.switchCamera();
                    if (thumbnailVideoView.getVisibility() == View.VISIBLE) {
                        thumbnailVideoView.setMirror(cameraSource == CameraCapturerCompat.Source.BACK_CAMERA);
                    } else {
                        primaryVideoView.setMirror(cameraSource == CameraCapturerCompat.Source.BACK_CAMERA);
                    }
                }
            }

        };
    }

    private View.OnClickListener localVideoClickListener() {
        return v -> {
            /*
             * Enable/disable the local video track
             */
            if (localVideoTrack != null) {
                boolean enable = !localVideoTrack.isEnabled();
                localVideoTrack.enable(enable);
                int icon;
                if (enable) {
                    icon = R.drawable.ic_videocam_on;
                    switchCameraActionFab.show();
                } else {
                    icon = R.drawable.ic_videocam_off;
                    switchCameraActionFab.hide();
                }
                localVideoActionFab.setImageDrawable(
                        ContextCompat.getDrawable(VideoByPatientActivity.this, icon));
            }
        };
    }

    private View.OnClickListener muteClickListener() {
        return v -> {
            /*
             * Enable/disable the local audio track. The results of this operation are
             * signaled to other Participants in the same Room. When an audio track is
             * disabled, the audio is muted.
             */
            if (localAudioTrack != null) {
                boolean enable = !localAudioTrack.isEnabled();
                localAudioTrack.enable(enable);
                int icon = enable ?
                        R.drawable.ic_mic_on : R.drawable.ic_mic_off;
                muteActionFab.setImageDrawable(ContextCompat.getDrawable(
                        VideoByPatientActivity.this, icon));
            }
        };
    }

    private View.OnClickListener volumeClickListener() {
        return v -> {
            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(false);
                volumeActionFab.setImageDrawable(ContextCompat.getDrawable(
                        VideoByPatientActivity.this, R.drawable.ic_phonelink_ring_white_24dp));
            } else {
                audioManager.setSpeakerphoneOn(true);
                volumeActionFab.setImageDrawable(ContextCompat.getDrawable(
                        VideoByPatientActivity.this, R.drawable.ic_volume_up_white_24dp));
            }
        };
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(VideoByPatientActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Ending call...")
                .setMessage("Are you sure you want to End Call?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void configureAudio(boolean enable) {
        if (enable) {
            previousAudioMode = audioManager.getMode();
            // Request audio focus before making any device switch
            requestAudioFocus();
            /*
             * Use MODE_IN_COMMUNICATION as the default audio mode. It is required
             * to be in this mode when playout and/or recording starts for the best
             * possible VoIP performance. Some devices have difficulties with
             * speaker mode if this is not set.
             */
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            /*
             * Always disable microphone mute during a WebRTC call.
             */
            previousMicrophoneMute = audioManager.isMicrophoneMute();
            audioManager.setMicrophoneMute(false);
        } else {
            audioManager.setMode(previousAudioMode);
            audioManager.abandonAudioFocus(null);
            audioManager.setMicrophoneMute(previousMicrophoneMute);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            AudioFocusRequest focusRequest =
                    new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(
                                    i -> {
                                    })
                            .build();
            audioManager.requestAudioFocus(focusRequest);
        } else {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void getNewToken(){
        if (checkInterNetConnection(VideoByPatientActivity.this)) {
            appointmentViewModel.callGetToken(userObj.getAuthToken(),appointmentId);
            //retrieveAccessTokenfromServer();
        }
    }

    @Override
    public void init() {

    }

    /*private void retrieveAccessTokenfromServer() {
        Ion.with(this)
                .load(String.format("%s?identity=%s", ACCESS_TOKEN_SERVER,
                        UUID.randomUUID().toString()))
                .asString()
                .setCallback((e, token) -> {
                    if (e == null) {
                        accessToken = token;
                        Log.d("connect_room ","access token "+accessToken);
                    } else {
                        Log.d("connect_room ","error "+e.toString());
                        CommonMethods.showToast(VideoByPatientActivity.this,"Error in retriving token");
                    }
                });
    }*/
}
