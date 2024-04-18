package com.example.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class myList extends AppCompatActivity implements MyListAdapter.MyListClickListener {

    private RecyclerView myListRecyclerView;
    private List<Event> myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        myListRecyclerView = findViewById(R.id.myListRecyclerView);
        myListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myList = new ArrayList<>();

        // Fetch event details from Firebase
        fetchMyList();
    }

    private void fetchMyList() {
        // Get the UID of the current user
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Navigate to the "users" node and then to the UID node of the current user
        DatabaseReference userNodeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        // Retrieve event details from the "my_list" node under the UID node of the current user
        DatabaseReference myListRef = userNodeRef.child("my_list");
        myListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve event details
                    String committeeName = eventSnapshot.child("committeeName").getValue(String.class);
                    String title = eventSnapshot.child("title").getValue(String.class);
                    String description = eventSnapshot.child("description").getValue(String.class);
                    String imageUrl = eventSnapshot.child("imageUrl").getValue(String.class);

                    Event event = new Event(committeeName, title, description, imageUrl);
                    event.setEventId(eventSnapshot.getKey()); // Set the event ID
                    myList.add(event);
                }
                // Update RecyclerView with fetched event details
                MyListAdapter myListAdapter = new MyListAdapter(myList, myListRef, myList.this);
                myListRecyclerView.setAdapter(myListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("MyListActivity", "Database Error: " + databaseError.getMessage());
                Toast.makeText(myList.this, "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveClicked(Event event, DatabaseReference myListRef) {
        // Remove the event from Firebase Realtime Database
        myListRef.child(event.getEventId()).removeValue().addOnSuccessListener(aVoid -> {
            // Removal successful
            Toast.makeText(this, "Event removed", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Error handling
            Toast.makeText(this, "Failed to remove event", Toast.LENGTH_SHORT).show();
            Log.e("MyListActivity", "Failed to remove event: " + event.getEventId(), e);
        });
    }
}
