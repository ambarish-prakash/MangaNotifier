package com.praamb.manganotifier.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.praamb.manganotifier.R;

import java.util.Random;

public class NotificationUtil {

    private static String channelId;
    private static Random random = new Random();

    public static NotificationChannel createNotificationChannel(String channelId,String channel_name, String channel_desc) {
        NotificationUtil.channelId = channelId;
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_desc;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            return channel;
        }
        return null;
    }

    public static void notify(Context context, String name, String description, PendingIntent intent)
    {
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.smallic)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(random.nextInt(76365)+10, notification);
    }
}
