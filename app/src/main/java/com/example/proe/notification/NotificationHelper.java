package com.example.proe.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.proe.MainBuyerActivity;
import com.example.proe.MainUserActivity;
import com.example.proe.R;

import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {


  public static void sendNotification(Context context, String title, String message) {
    Intent intent;

    if (SharedPreferences.getUserType(context).equals("Buyer")) {
      intent = new Intent(context, MainBuyerActivity.class);
    } else {
      intent = new Intent(context, MainUserActivity.class);
    }

    intent = intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);


    String channelId = context.getString(R.string.default_notification_channel_id);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVibrate(new long[]{500, 1000, 500})
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(channelId,
          "Channel human readable title",
          NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(channel);
    }
    int timeMillis = (int) System.currentTimeMillis();
    notificationManager.notify(timeMillis, notificationBuilder.build());
  }

  public static void viewAnimationShake(Context context, View viewShake) {
    final Animation animShake = AnimationUtils.loadAnimation(context, R.anim.shake);
    viewShake.startAnimation(animShake);
  }
}
