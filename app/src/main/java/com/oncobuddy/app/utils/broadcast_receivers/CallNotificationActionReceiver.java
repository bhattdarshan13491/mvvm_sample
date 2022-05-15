package com.oncobuddy.app.utils.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oncobuddy.app.utils.Constants;
import com.oncobuddy.app.utils.services.HeadsUpNotificationService;
import com.oncobuddy.app.views.activities.VideoByPatientActivity;

public class CallNotificationActionReceiver extends BroadcastReceiver {

    Context mContext;
    private String appointmentId = "1";
    private String doctorName = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("call_notif_log", "0 received");
        this.mContext = context;
        if (intent != null && intent.getExtras() != null) {
            Log.d("call_notif_log", "1");
            String action = "";
            action = intent.getStringExtra("ACTION_TYPE");
            appointmentId = intent.getStringExtra(Constants.APPOINTMENT_ID);
            doctorName = intent.getStringExtra("doctor_name");

            if (action != null && !action.equalsIgnoreCase("")) {
                performClickAction(context, action);
            }

            // Close the notification after the click action is performed.
            Intent iclose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(iclose);
            context.stopService(new Intent(context, HeadsUpNotificationService.class));
            Log.d("call_notif_log", "2");
        }


    }

    private void performClickAction(Context context, String action) {
        if (action.equalsIgnoreCase("RECEIVE_CALL")) {
            if (checkAppPermissions()) {
                Intent intentCallReceive = new Intent(mContext, VideoByPatientActivity.class);
                intentCallReceive.putExtra(Constants.APPOINTMENT_ID, appointmentId);
                intentCallReceive.putExtra("doctor_name",doctorName);
                intentCallReceive.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intentCallReceive);
            } else {
                Intent intent = new Intent(mContext, VideoByPatientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(Constants.APPOINTMENT_ID, "12");
                intent.putExtra("doctor_name","Aaditya");
                mContext.startActivity(intent);
            }
        } else if (action.equalsIgnoreCase("DIALOG_CALL")) {
            // show ringing activity when phone is locked
            Intent intent = new Intent(mContext, VideoByPatientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        } else {
            context.stopService(new Intent(context, HeadsUpNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }

    private Boolean checkAppPermissions() {
        return hasReadPermissions() && hasWritePermissions() && hasCameraPermissions() && hasAudioPermissions();
    }

    private boolean hasAudioPermissions() {
        return true;      //(ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasReadPermissions() {
        return true;
        //return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return true;
        //return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasCameraPermissions() {
        return true;
        //return (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
}