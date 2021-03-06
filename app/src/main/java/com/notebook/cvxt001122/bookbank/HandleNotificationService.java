package com.notebook.cvxt001122.bookbank;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class HandleNotificationService extends IntentService {

    private static final String CHANNEL_ID="channel_id";
    private String bookName;
    public HandleNotificationService(){
        super("HandleNotificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        bookName=intent.getStringExtra("bookname");
        if(bookName!=null)
        showNotification();
    }
    private void showNotification(){

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle("book returning notification");
        builder.setSmallIcon(R.drawable.librarysmall);
        builder.setContentText("one day left to return "+bookName);
        Uri uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("one day left to submit "+bookName));
        builder.setWhen(Calendar.getInstance().getTimeInMillis());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.librarybig ));
        builder.setAutoCancel(true);
        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, intent,0 );
        TaskStackBuilder stackBuilder=TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        builder.setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(this);
        managerCompat.notify((int) System.currentTimeMillis(),builder.build() );

    }
}
