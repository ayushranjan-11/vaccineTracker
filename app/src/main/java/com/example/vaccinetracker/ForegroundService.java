package com.example.vaccinetracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationChannel();

        Intent intent1=new Intent(this,MainActivity.class);

        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent1,0);
        Notification notification=new NotificationCompat.Builder(this,"VaccineTracker")
                .setContentTitle("Vaccine Tracking")
                .setContentText("Application is running background for realtime response")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentIntent(pendingIntent).build();

        startForeground(1,notification);

        return START_STICKY;
    }

    private void notificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel=new NotificationChannel(
                    "VaccineTracker","Checking Vaccine", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
