package com.example.lab5;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            createNotificationChannel(context);
            prepareAndSendNotification(context);
        }
    }

    private void createNotificationChannel(Context context) {
        CharSequence name = context.getString(R.string.nc_name);
        String description = context.getString(R.string.nc_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel("my_nc", name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel.setDescription(description);
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void prepareAndSendNotification(Context context) {
        Intent compassIntent = new Intent(context, MainActivity.class);
        compassIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingCompassIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        compassIntent,
                        PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_nc")
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("Compass Application")
                .setContentText("Open the Compass Application")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingCompassIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
