package com.oncobuddy.app.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

public class RingtonePlayingService extends Service
{
    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long pattern[] = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        vibrator.vibrate(pattern, 1);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        ringtone.stop();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }
}