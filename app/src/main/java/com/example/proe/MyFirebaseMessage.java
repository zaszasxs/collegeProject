package com.example.proe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessage extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID"; //required for android 0 and above

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();

            String notificationType = remoteMessage.getData().get("notificationType");
            if (notificationType.equals("NewOrder")){
                String BuyerUid = remoteMessage.getData().get("BuyerUid");
                String Uid = remoteMessage.getData().get("Uid");
                String OrderID = remoteMessage.getData().get("OrderID");
                String notificationTitle = remoteMessage.getData().get("notificationTitle");
                String notificationDescription = remoteMessage.getData().get("notificationDescription");
                if (firebaseUser != null && firebaseAuth.getUid().equals(Uid)){

                    showNotification(OrderID,Uid,BuyerUid,notificationTitle,notificationDescription,notificationType);
                }

            }
            if (notificationType.equals("OrderStatusChanged")){
                String BuyerUid = remoteMessage.getData().get("BuyerUid");
                String Uid = remoteMessage.getData().get("Uid");
                String OrderID = remoteMessage.getData().get("OrderID");
                String notificationTitle = remoteMessage.getData().get("notificationTitle");
                String notificationDescription = remoteMessage.getData().get("notificationMessage");

                if (firebaseUser != null && firebaseAuth.getUid().equals(BuyerUid)){

                    showNotification(OrderID,Uid,BuyerUid,notificationTitle,notificationDescription,notificationType);
                }
            }
    }

    private void showNotification(String OrderID, String Uid, String BuyerUid, String notificationTitle, String notificationDescription ,String notificationType){

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        Intent intent = null;
        if (notificationType.equals("NewOrder")){
            //open OrderDetailBuyerActivity
            intent = new Intent(this, OrderDetailBuyerActivity.class);
            intent.putExtra("OrderID",OrderID);
            intent.putExtra("OrderBy",BuyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
        else if (notificationType.equals("OrderStatusChanged")){
            intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("OrderID",OrderID);
            intent.putExtra("OrderTo",Uid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Large icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.buag);
        Uri notificationSounUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuild = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuild.setSmallIcon(R.drawable.buag)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSounUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, notificationBuild.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if (notificationManager != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
