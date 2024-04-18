package com.example.profile;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "event_notifications";
    private static final String CHANNEL_NAME = "Event Notifications";

    private static final String TAG = "MainActivity";
    private static final String PASSWORD = "Tixino_2024";


    // Notification ID counter
    private int notificationId = 0;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addEventButton = findViewById(R.id.addEventButton);
        Switch notificationSwitch = findViewById(R.id.notificationSwitch);

        Button ProfileButton = findViewById(R.id.ProfileButton);
        ProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,profile.class));
            }
        });

        Button UpcomingButton = findViewById(R.id.UpcomingButton);
        UpcomingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Upcoming.class));
            }
        });

        Button PastButton = findViewById(R.id.PastButton);
        PastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,past.class));
            }
        });

        Button MylistButton = findViewById(R.id.MylistButton);
        MylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,myList.class));
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeleteAccount.class));
            }
        });

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference("events");

        addEventButton.setOnClickListener(v -> {
            // First, show the password verification dialog
            showPasswordDialog(eventsRef);
        });

        // Set a listener for the switch
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If switch is checked, add an event and send notification
                // Get the title here
                addEventAndSendNotification(eventsRef);
            }
        });
    }

    // Function to show password verification dialog
    private void showPasswordDialog(DatabaseReference eventsRef) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password_verification, null);
        builder.setView(dialogView);

        // Get reference to EditText field in the dialog
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);

        // Set up a button to verify the password
        builder.setPositiveButton("Verify", (dialog, which) -> {
            // Get the input password
            String password = passwordEditText.getText().toString();

            // Check if password matches the hardcoded password
            if (password.equals(PASSWORD)) {
                // If password is correct, proceed to show add event dialog
                showAddEventDialog();
            } else {
                // If password is incorrect, show a toast indicating the error
                Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.create().show();
    }

    // Function to show add event dialog
    private void showAddEventDialog() {
        // Create an Intent to start the add event activity
        Intent intent = new Intent(MainActivity.this, activity_add_event.class);
        startActivity(intent);
    }


    // Function to add an event and send a notification
    private void addEventAndSendNotification(DatabaseReference eventsRef) {
        // Attach a ValueEventListener to fetch the latest event title from the database
        eventsRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the title from the dataSnapshot
                    String title = snapshot.child("title").getValue(String.class);

                    // Push the event to the Firebase Database
                    Event event = new Event(null, title, null, null, null);
                    DatabaseReference newEventRef = eventsRef.push();
                    newEventRef.setValue(event);

                    // Create notification channel
                    createNotificationChannel();

                    // Send notification with the event title
                    sendNotification(title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e(TAG, "Database error: " + databaseError.getMessage());
            }
        });
    }

    private void sendNotification(String title) {
        // Customize notification content
        String notificationMessage = "Event: " + title + " is happening soon."; // Use the event title here

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title) // Set the notification title as the event title
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.tixino) // Replace ic_notification with your notification icon
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId++, builder.build());
        }
    }
    // Function to create a pending intent for the notification action
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, Upcoming.class);
        intent.setAction("OPEN_LIST_ACTIVITY");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Notification Channel for Event Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
