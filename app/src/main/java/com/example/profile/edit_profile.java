package com.example.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class edit_profile extends AppCompatActivity {
    EditText editName, editEmail, editPhone, editPassword;
    Button saveButton;
    DatabaseReference reference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.saveButton);

        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get user ID
            String userId = currentUser.getUid();
            // Get reference to the user's data in the database
            reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            // Retrieve user data
            retrieveUserData();
        }

        saveButton.setOnClickListener(view -> updateUserData());
    }


    private void retrieveUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
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
                            editName.setText(name);
                            editEmail.setText(email);
                            editPhone.setText(phone);
                            editPassword.setText(password);
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

    private void updateUserData() {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Regular expression to validate Indian phone numbers
        String phoneRegex = "^[6-9]\\d{9}$"; // Assumes 10-digit Indian phone numbers starting with digits 6-9

        // Check if any field is empty
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(edit_profile.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone number format
        if (!phone.matches(phoneRegex)) {
            Toast.makeText(edit_profile.this, "Please enter a valid Indian phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get reference to the current user's node
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

            // Update user data in the database
            userRef.child("name").setValue(name);
            userRef.child("email").setValue(email);
            userRef.child("phone").setValue(phone);
            userRef.child("password").setValue(password);

            // Display success message
            Toast.makeText(edit_profile.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
        }
    }



}