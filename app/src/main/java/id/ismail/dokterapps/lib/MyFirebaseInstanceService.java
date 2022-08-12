package id.ismail.dokterapps.lib;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import id.ismail.dokterapps.PasienActivity;
import id.ismail.dokterapps.R;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (!remoteMessage.getData().isEmpty()) {
            Map<String, String> data = remoteMessage.getData();
            String body = data.get("body");
            String title = data.get("title");
            showNotification(title, body);
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent;
        notificationIntent = new Intent(this, PasienActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(this, 1 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);


        String NOTIF_ID = "id.ismail.dokterapps";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_ID, "Notifikasi Pasien Hadir",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription(body);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIF_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentInfo("Informasi")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
