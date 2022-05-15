package com.oncobuddy.app.utils.services;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.NotificationObj;
import com.oncobuddy.app.utils.Constants;
import com.oncobuddy.app.utils.broadcast_receivers.CallNotificationActionReceiver;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HeadsUpNotificationService extends Service implements MediaPlayer.OnPreparedListener {
    private String CHANNEL_ID = "four_base_care_notifications_foreground";
    private String CHANNEL_NAME = "Oncobuddy consultations";
    Vibrator mvibrator;
    AudioManager audioManager;
    AudioAttributes playbackAttributes;
    private Handler handler;
    AudioManager.OnAudioFocusChangeListener afChangeListener;
    private boolean status = false;
    private boolean vstatus = false;
    MediaPlayer mediaPlayer;
    NotificationObj notificationObj;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("call_notif_log","1 service");
        Bundle data = null;
        String messageBody = "";
        int NOTIFICATION_ID = 120;
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.d("call_notif_log","Ringing normal");
                        status = true;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.d("call_notif_log","Silent");
                        status = false;
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.d("call_notif_log","Mode vibrate");
                        status = false;
                        vstatus = true;
                        Log.e("Service!!", "vibrate mode");
                        break;
                }
            }

            Log.d("call_notif_log","status "+status);
            if (status) {
                Runnable delayedStopRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d("call_notif_log","Stopped after 30 seconds");
                        releaseMediaPlayer();
                    }
                };

                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    public void onAudioFocusChange(int focusChange) {
                        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                            // Permanent loss of audio focus
                            // Pause playback immediately
                            //mediaController.getTransportControls().pause();
                            if (mediaPlayer != null) {
                                if (mediaPlayer.isPlaying()) {
                                    mediaPlayer.pause();
                                }
                            }
                            // Wait 30 seconds before stopping playback
                            handler.postDelayed(delayedStopRunnable,
                                    TimeUnit.SECONDS.toMillis(30));
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                            // Pause playback
                        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                            // Lower the volume, keep playing
                        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                            // Your app has been granted audio focus again
                            // Raise volume to normal, restart playback if necessary
                        }
                    }
                };
                KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);


                mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
                mediaPlayer.setLooping(true);
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d("call_notif_log","Greater then O");
                    handler = new Handler();


                    playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();

                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(afChangeListener, handler)
                            .build();
                    int res = audioManager.requestAudioFocus(focusRequest);
                    if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {
                            Log.d("call_notif_log","Device locked rining");
                            mediaPlayer.start();
                        }

                    }
                    mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Start without a delay
                    // Each element then alternates between vibrate, sleep, vibrate, sleep...
                    long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                            150, 75, 150};

                    // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                    mvibrator.vibrate(pattern, 0);
                    Log.e("Service!!", "vibrate mode start");

                } else {
                    Log.d("call_notif_log","Less then O");
                    // Request audio focus for playback
                    int result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        if (!keyguardManager.isDeviceLocked()) {
                            // Start playback
                            mediaPlayer.start();
                        }
                    }
                    mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Start without a delay
                    // Each element then alternates between vibrate, sleep, vibrate, sleep...
                    long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                            150, 75, 150};

                    // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                    mvibrator.vibrate(pattern, 0);
                    Log.e("Service!!", "vibrate mode start");

                }

            } else if (vstatus) {
                Log.d("call_notif_log","service vstatus");
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                        150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                mvibrator.vibrate(pattern, 0);
                Log.e("Service!!", "vibrate mode start");

            }else{
                Log.d("call_notif_log","service else");
                mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Each element then alternates between vibrate, sleep, vibrate, sleep...
                long[] pattern = {0, 250, 200, 250, 150, 150, 75,
                        150, 75, 150};

                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                mvibrator.vibrate(pattern, 0);
                Log.e("Service!!", "vibrate mode start");
            }

        } catch (Exception e) {
            Log.d("call_notif_log","Error e1 "+e.toString());
            e.printStackTrace();
        }

        if (intent != null && intent.getExtras() != null) {

            data = intent.getExtras();
            messageBody = data.getString("message_body");
            Gson gson = new Gson();
            notificationObj  = gson.fromJson(messageBody, NotificationObj.class);
        /*if(AppController.getInstance().getCall_type().equalsIgnoreCase(ApplicationRef.Constants.AUDIO_CALL)){
            callType ="Audio";
        }
        else {
            callType ="Video";
        }*/

        }
        try {
            Intent receiveCallAction = new Intent(getApplicationContext(), CallNotificationActionReceiver.class);
            receiveCallAction.putExtra("CALL_RESPONSE_ACTION_KEY", "CALL_RECEIVE_ACTION");
            receiveCallAction.putExtra("ACTION_TYPE", "RECEIVE_CALL");
            receiveCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            receiveCallAction.putExtra(Constants.APPOINTMENT_ID,notificationObj.getAppointmentId());
            receiveCallAction.putExtra("doctor_name",notificationObj.getCallerName());
            receiveCallAction.setAction("RECEIVE_CALL");

         /*   Intent cancelCallAction = new Intent(getApplicationContext(), PendingIntent.class);
            cancelCallAction.putExtra("CALL_RESPONSE_ACTION_KEY", "CALL_CANCEL_ACTION");
            cancelCallAction.putExtra("ACTION_TYPE", "CANCEL_CALL");
            cancelCallAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            cancelCallAction.setAction("CANCEL_CALL");

          */

            Intent callDialogAction = new Intent(getApplicationContext(), PendingIntent.class);
            callDialogAction.putExtra("ACTION_TYPE", "DIALOG_CALL");
            callDialogAction.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);
            receiveCallAction.putExtra(Constants.APPOINTMENT_ID,notificationObj.getAppointmentId());
            receiveCallAction.putExtra("doctor_name",notificationObj.getCallerName());
            callDialogAction.setAction("DIALOG_CALL");

            PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
           // PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent callDialogPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1202, callDialogAction, PendingIntent.FLAG_UPDATE_CURRENT);

            createChannel();
            NotificationCompat.Builder notificationBuilder = null;
            if (data != null) {
                // Uri ringUri= Settings.System.DEFAULT_RINGTONE_URI;
                notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(notificationObj.getCallerName()+ "is calling")
                        .setContentText("Consultation Call")
                        .setSmallIcon(R.mipmap.ic_launcher_rounded)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .addAction(R.drawable.ic_call, "Answer", receiveCallPendingIntent)
                        .setAutoCancel(true)
                        //.setSound(ringUri)
                        .setFullScreenIntent(callDialogPendingIntent, true);

            }

            Notification incomingCallNotification = null;
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build();
            }
            startForeground(NOTIFICATION_ID, incomingCallNotification);


        } catch (Exception e) {
            Log.d("call_notif_log","Error e2 "+e.toString());
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();// release your media player here audioManager.abandonAudioFocus(afChangeListener);
        releaseMediaPlayer();
        releaseVibration();
        Log.d("call_notif_log","on destroy");
    }

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Uri ringUri = Settings.System.DEFAULT_RINGTONE_URI;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Call Notifications");
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
           /* channel.setSound(ringUri,
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());*/
                Objects.requireNonNull(getApplicationContext().getSystemService(NotificationManager.class)).createNotificationChannel(channel);

            } catch (Exception e) {
                Log.d("call_notif_log","Error e3 "+e.toString());
                e.printStackTrace();
            }
        }
    }

    public void releaseVibration() {
        try {
            if (mvibrator != null) {
                if (mvibrator.hasVibrator()) {
                    mvibrator.cancel();
                }
                mvibrator = null;
            }
        } catch (Exception e) {
            Log.d("call_notif_log","Error e4 "+e.toString());
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer = null;
            }

        } catch (Exception e) {
            Log.d("call_notif_log","media player err e5 "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }
}