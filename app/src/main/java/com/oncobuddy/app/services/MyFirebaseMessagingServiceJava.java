package com.oncobuddy.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.oncobuddy.app.FourBaseCareApp;
import com.oncobuddy.app.R;
import com.oncobuddy.app.models.pojo.ForumNotification;
import com.oncobuddy.app.models.pojo.NotificationObj;
import com.oncobuddy.app.models.pojo.login_response.LoginDetails;
import com.oncobuddy.app.utils.Constants;
import com.oncobuddy.app.views.activities.PatientLandingActivity;
import com.oncobuddy.app.views.activities.VideoByPatientActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.oncobuddy.app.views.activities.VideoViewerActivity;
import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingServiceJava extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    private static final String CALL_SID_KEY = "CALL_SID";
    private static final String VOICE_CHANNEL = "default";
    private NotificationManager notificationManager;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("call_service_check", "1");

        Log.d("notification_data_log", "remote msg " + remoteMessage.toString());
        Log.d("notification_data_log", "remote notif " + remoteMessage.getNotification());
        Log.d("notification_data_log", "data " + remoteMessage.getData());
        Log.d("notification_data_log", "data size " + remoteMessage.getData().size());
        Log.d("notification_data_log", "notification  " + remoteMessage.getNotification());

        if(remoteMessage.getData() != null && remoteMessage.getData().get("body") != null){
            if(remoteMessage.getData().get("body").equals("UPLOADING") || remoteMessage.getData().get("body").equals("PROCESSING")) {
                Log.d("notification_data_log", "Uploading notification caught");
                Intent intent = new Intent("action_upload_status");
                intent.putExtra("progress_status", ""+remoteMessage.getData().get("body"));
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
            else{
                sendNotificationOld(remoteMessage.getData().get("body"), "Notification");
            }

        }



    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        FourBaseCareApp.savePreferenceDataString(Constants.PREF_FCM_TOKEN,token);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendForumNotification(String messageBody) {
        Log.d("call_service_check", "2");
        Log.d("call_service_check", "2.1 Message body "+messageBody);

        Log.d("notification_data_log","Forum notification");
            Gson forumGson = new Gson();
            ForumNotification notificationObj  = forumGson.fromJson(messageBody, ForumNotification.class);
            Log.d("notification_data_log","Notification type "+notificationObj.getPostType());
            String notificationTitle = "";
            if(notificationObj.getPostType().equals("SINGLE_VIDEO")){
                notificationTitle = "Checkout this new video";
            }else{
                notificationTitle = "Checkout this new blog post";
            }

            Intent notifyIntent = null;
            if(notificationObj.getPostType().equals("SINGLE_VIDEO")){
                notifyIntent = new Intent(this, VideoViewerActivity.class);
                notifyIntent.putExtra(Constants.SOURCE,"notification");
                notifyIntent.putExtra(Constants.VIDEO_ID,""+notificationObj.getPostId());
            }else{
                notifyIntent = new Intent(this, PatientLandingActivity.class);
                notifyIntent.putExtra(Constants.SOURCE,"notification");
                notifyIntent.putExtra("notification_type","new_blog");
                notifyIntent.putExtra(Constants.BLOG_ID,""+notificationObj.getPostId());
            }

            Objects.requireNonNull(notifyIntent).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(getApplicationContext(),0, notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            String channelId = getString(R.string.default_notification_channel_id);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher_rounded)
                            .setContentTitle(notificationTitle)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentText(""+notificationObj.getTitle())
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        getString(R.string.default_notification_channel_id),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            // create random object
            Random ran = new Random();

            // generating integer
            int nxt = ran.nextInt();

            notificationManager.notify(nxt, notificationBuilder.build());

    }

    private void sendNotificationOld(String messageBody, String title) {
        Log.d("call_service_check", "2");
        Log.d("call_service_check", "2.1 Message body "+messageBody);

        Gson gson = new Gson();
        String userJson = FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "");
        LoginDetails userObj  = gson.fromJson(userJson, LoginDetails.class);

        if (messageBody.contains("CALL_START")) {
            Log.d("call_service_check", "2.2 Message body contains call start");
            Log.d("call_service_check", "2.3 user role "+userObj.getRole());
            if (userObj.getRole().equalsIgnoreCase(Constants.ROLE_PATIENT) || userObj.getRole().equalsIgnoreCase(Constants.ROLE_PATIENT_CARE_GIVER)) {
                Log.d("call_service_check", "3");
                sendCallInviteToActivity(messageBody);
                //showCallingNotification(messageBody);
            }
        }

        else if(messageBody.contains("SINGLE_VIDEO") || messageBody.contains("BLOG")){
            sendForumNotification(messageBody);
        }
        else {
            Intent notifyIntent = new Intent(this, PatientLandingActivity.class);
            if(messageBody.contains("callback request")){
                notifyIntent.putExtra("notification_type","request_callback");
            }
            Objects.requireNonNull(notifyIntent).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(getApplicationContext(),0, notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            String channelId = getString(R.string.default_notification_channel_id);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            .setSmallIcon(R.mipmap.ic_launcher_rounded)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentTitle(title)
                            .setContentText(messageBody)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId,
                        getString(R.string.default_notification_channel_id),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            // create random object
            Random ran = new Random();

            // generating integer
            int nxt = ran.nextInt();

            if(!messageBody.contains("CALL_END")) {
                notificationManager.notify(nxt, notificationBuilder.build());
            }

        }
    }

    private void sendCallInviteToActivity(String messageBody) {
        try {
            Log.d("call_service_check", "0 ");
            Log.d("call_service_check", "Message body "+messageBody);

            Gson gson = new Gson();
            NotificationObj notificationObj  = gson.fromJson(messageBody, NotificationObj.class);


            Intent intent = new Intent(this, VideoByPatientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("class_name", "fcm");
            if(notificationObj != null && notificationObj.getCallerName() != null)
                intent.putExtra("doctor_name", notificationObj.getCallerName());
            if(notificationObj != null && notificationObj.getAppointmentId() != null)
                intent.putExtra(Constants.APPOINTMENT_ID, notificationObj.getAppointmentId());
            getApplicationContext().startActivity(intent);



            //startMyOwnForeground(getResources().getString(R.string.app_name), String.format("Doctor %s is Calling...", notificationObj.getCallerName()), "call_connect");

               /* Intent serviceIntent = new Intent(getApplicationContext(), HeadsUpNotificationService.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("message_body", messageBody);
                mBundle.putString("call_type","call");
                serviceIntent.putExtras(mBundle);
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                Log.d("call_notif_log","0");*/

        } catch (Exception e) {
            Log.d("call_service_check", "callinvite Err "+e.toString());
            e.printStackTrace();
            Intent intent = new Intent(this, PatientLandingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            Log.e(TAG, "Send call to invite "+e.toString());

        }

    }



}