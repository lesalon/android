package com.lesalon.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.lesalon.Constants;
import com.lesalon.MainActivity;
import com.lesalon.R;

/**
 * Created by kanishk on 2/20/16.
 */
public class ChatListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("GCM-receiver","GCM Message Received!--> " + data.toString());
    }

    private void setNotification(Bundle extras) {
        if (extras==null) return;
        Log.d("GCM-notif",extras.toString());
        String chatRoom = extras.getString(Constants.GCM_CHAT_ROOM);
        String notifBigTex  = "Poke from " + extras.getString(Constants.GCM_POKE_FROM);
        String notifContent = "In chat room " + chatRoom;

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.CHAT_ROOM, chatRoom);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setLargeIcon(icon)
                        .setSmallIcon(android.R.drawable.star_on)
                        .setContentTitle(notifBigTex)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notifBigTex))
                        .setContentText(notifContent)
                        .setAutoCancel(true);

//        mBuilder.setContentIntent(contentIntent);
        Notification pnNotif = mBuilder.build();
        mNotificationManager.notify(0, pnNotif);
    }
}
