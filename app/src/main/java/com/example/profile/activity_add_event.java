package com.example.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class activity_add_event extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;

    private DatabaseReference mDatabaseRef;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button addEventButton = findViewById(R.id.addEventButton);

        selectImageButton.setOnClickListener(v -> {
            // Open image selection activity
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        });

        addEventButton.setOnClickListener(v -> {
            // Get input values
            String committeeName = ((EditText) findViewById(R.id.committeeNameEditText)).getText().toString().trim();
            String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString().trim();
            String description = ((EditText) findViewById(R.id.descriptionEditText)).getText().toString().trim();

            // Check if input fields are not empty and an image is selected
            if (!committeeName.isEmpty() && !title.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                // Add event to database
                addEvent(committeeName, title, description);
            } else {
                Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to add an event to the database
    private void addEvent(String committeeName, String title, String description) {
        // Generate a unique key for the event
        String eventId = mDatabaseRef.child("events").push().getKey();

        // Upload image to Firebase Storage and get download URL
        assert eventId != null;
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("event_images").child(eventId);
        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save event details to database
                        Event event = new Event(committeeName, title, description, uri.toString());
                        mDatabaseRef.child("events").child(eventId).setValue(event)
                                .addOnSuccessListener(aVoid -> {
                                    // Event added successfully
                                    Toast.makeText(activity_add_event.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                                    finish(); // Finish activity
                                })
                                .addOnFailureListener(e -> {
                                    // Failed to add event
                                    Toast.makeText(activity_add_event.this, "Failed to add event.", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Failed to upload image
                    Toast.makeText(activity_add_event.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Image selected by user
            selectedImageUri = data.getData();
        }
    }
}
