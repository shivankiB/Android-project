package com.example.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Notification received");

        // Retrieve data from the intent
        String eventId = intent.getStringExtra("eventId");
        Log.d("NotificationReceiver", "Event ID: " + eventId);

        // Start the list activity
        Intent listIntent = new Intent(context, Upcoming.class);
        listIntent.putExtra("eventId", eventId);
        listIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag since BroadcastReceiver doesn't have an associated UI
        context.startActivity(listIntent);
    }
}
