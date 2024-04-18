package com.example.profile;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {
    EditText regName, regEmail, regPassword, regPhone;
    Button buttonReg;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regName = findViewById(R.id.regName);
        regPhone = findViewById(R.id.regPhone);
        buttonReg = findViewById(R.id.buttonRegister);

        buttonReg.setOnClickListener(v -> {
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String name = regName.getText().toString();
            String email = regEmail.getText().toString();
            String phone = regPhone.getText().toString();
            String password = regPassword.getText().toString();

            // Regular expression to validate Indian phone numbers
            String phoneRegex = "^[6-9]\\d{9}$"; // Assumes 10-digit Indian phone numbers starting with digits 6-9

            // Check if any field is empty
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(register.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate phone number format
            if (!phone.matches(phoneRegex)) {
                Toast.makeText(register.this, "Please enter a valid Indian phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                HelperClass helperClass = new HelperClass(name, email, phone, password);
                                reference.child(userId).setValue(helperClass);
                            }

                            Toast.makeText(register.this, "Account created", Toast.LENGTH_SHORT).show();

                            // After successful registration, redirect to the login activity
                            Intent loginIntent = new Intent(register.this, login.class);
                            startActivity(loginIntent);
                            finish(); // Finish the current activity
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "Account creation failed", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                // Handle user collision exception
                                Toast.makeText(register.this, "The email address is already in use by another account", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(register.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

    }

    public void goToLoginPage(View view) {
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
}
