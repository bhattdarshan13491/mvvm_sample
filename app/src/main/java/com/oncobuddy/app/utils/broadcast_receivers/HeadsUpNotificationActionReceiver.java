package com.oncobuddy.app.utils.broadcast_receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.oncobuddy.app.utils.Constants;
import com.oncobuddy.app.utils.services.HeadsUpNotificationService;
import com.oncobuddy.app.views.activities.VideoByPatientActivity;


public class HeadsUpNotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            String action = intent.getStringExtra(Constants.CALL_RESPONSE_ACTION_KEY);
            Bundle data = intent.getBundleExtra(Constants.FCM_DATA_KEY);

            if (action != null) {
                performClickAction(context, action, data);
            }

           // Close the notification after the click action is performed.

            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
            context.stopService(new Intent(context, HeadsUpNotificationService.class));
        }
    }

    private void performClickAction(Context context, String action, Bundle data) {

        if (action.equals(Constants.CALL_RECEIVE_ACTION)) {
            Intent openIntent = null;
            try {
                openIntent = new Intent(context, VideoByPatientActivity.class);
                openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (action.equals(Constants.CALL_CANCEL_ACTION)) {
            context.stopService(new Intent(context, HeadsUpNotificationService.class));
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }
}