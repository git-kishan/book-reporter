package com.notebook.cvxt001122.bookbank;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class OnSubmitDayReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String bookName=intent.getStringExtra("bookname");
        Intent intent1=new Intent(context,HandleNotificationService.class);
        intent1.putExtra("bookname", bookName);
        context.startService(intent1);
    }
}
