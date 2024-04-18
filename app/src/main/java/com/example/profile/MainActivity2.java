package com.example.profile;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity2 extends AppCompatActivity {
    private static final String CHANNEL_ID = "event_notifications";
    private static final String CHANNEL_NAME = "Event Notifications";

    private static final String TAG = "MainActivity";
    private int notificationId = 0;
    String title;

    private static final String PASSWORD = "Tixino_2024";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState   );
        setContentView(R.layout.activity_main2); // Set the content view




        Button myListButton = findViewById(R.id.MylistButton);
        Button ProfileButton = findViewById(R.id.ProfileButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button upcomingButton = findViewById(R.id.UpcomingButton);
        Button pastButton = findViewById(R.id.PastButton);
        FloatingActionButton addEventButton = findViewById(R.id.addEventButton);
        Switch notificationSwitch = findViewById(R.id.notificationSwitch);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference("events");
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordDialog(eventsRef);
                // Perform your action here when addEventButton is clicked
            }

            private void showPasswordDialog(DatabaseReference eventsRef) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);
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
                        Toast.makeText(MainActivity2.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                // Show the dialog
                builder.create().show();
            }

            // Function to show add event dialog
            private void showAddEventDialog() {
                // Create an Intent to start the add event activity
                Intent intent = new Intent(MainActivity2.this, activity_add_event.class);
                startActivity(intent);
            }
        });

        ProfileButton.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this,profile.class)));

        pastButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this,past.class));
            // Perform your action here when pastButton is clicked
        });

        upcomingButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this,Upcoming.class));
            // Perform your action here when upcomingButton is clicked
        });

        myListButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this,myList.class));
            // Perform your action here when myListButton is clicked
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If switch is checked, add an event and send notification
                // Get the title here
                addEventAndSendNotification(eventsRef);
            }
        });


        deleteButton.setOnClickListener(v -> {
                        // Handle delete account button click here
                        startActivity(new Intent(MainActivity2.this, DeleteAccount.class));

        });


        // Find ImageViews
        ImageView csiPce_logoImageView = findViewById(R.id.csiPce_logo);
        ImageView tpcPceLogoImageView = findViewById(R.id.tpcpce_logo);
        ImageView srtPceLogoImageView = findViewById(R.id.srtpce_logo);
            ImageView ieeePceLogoImageView = findViewById(R.id.ieeepce_logo);
        ImageView nssPceLogoImageView = findViewById(R.id.nsspce_logo);
        ImageView pceLogoImageView = findViewById(R.id.pce_logo);

        // Set onClickListeners for each logo
        csiPce_logoImageView.setOnClickListener(v -> openWebsite("https://csi.pce.ac.in/"));
        tpcPceLogoImageView.setOnClickListener(v -> openWebsite("https://tpc.pce.ac.in/"));
        srtPceLogoImageView.setOnClickListener(v -> openWebsite("http://sparkpce.com/"));
        ieeePceLogoImageView.setOnClickListener(v -> openWebsite("https://linktr.ee/ieee_pce"));
        nssPceLogoImageView.setOnClickListener(v -> openWebsite("https://www.pce.ac.in/students/student-activities/student-associations/nss/"));
        pceLogoImageView.setOnClickListener(v -> openWebsite("https://www.pce.ac.in/"));
    }

    // Method to open a website using an Intent
    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

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
