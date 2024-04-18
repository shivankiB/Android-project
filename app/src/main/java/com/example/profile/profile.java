package com.example.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class profile extends AppCompatActivity {

    // Define a constant to identify the request code for starting edit_profile activity
    private static final int EDIT_PROFILE_REQUEST = 1;

    TextView profile_name, profile_email, profile_phone, profile_password, textViewMail;
    Button editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        textViewMail = findViewById(R.id.textViewMail);
        ImageView imageView = findViewById(R.id.imageView6);
        profile_name = findViewById(R.id.textView3);
        profile_email = findViewById(R.id.textView9);
        profile_phone = findViewById(R.id.textView11);
        profile_password = findViewById(R.id.textView13);
        editProfileButton = findViewById(R.id.button5);

        // Set Text and Image for demonstration
        imageView.setImageResource(R.drawable.icon);

        // Fetch user information from Firebase
        retrieveUserData();

        // Handle edit profile button click
        // Inside your profile activity
        // Inside your profile activity
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(profile.this, edit_profile.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST) {
            // Check if the result is OK and update the UI
            if (resultCode == RESULT_OK) {
                // Fetch updated user information from Firebase
                retrieveUserData();
            }
        }
    }


    private void retrieveUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            textViewMail.setText(userEmail);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
            Query query = reference.orderByChild("email").equalTo(userEmail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            String phone = dataSnapshot.child("phone").getValue(String.class);
                            String password = dataSnapshot.child("password").getValue(String.class);

                            // Set retrieved data to TextViews
                            profile_name.setText(name);
                            profile_email.setText(email);
                            profile_phone.setText(phone);
                            profile_password.setText(password);
                        }
                    }  // Handle case when user data doesn't exist
                    // You can show a message or take appropriate action

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }
}

