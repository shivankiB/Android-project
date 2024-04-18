package com.example.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Upcoming extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("events");

        RecyclerView eventRecyclerView = findViewById(R.id.eventRecyclerView);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchEventsAndPopulateRecyclerView(eventRecyclerView);
    }

    private void fetchEventsAndPopulateRecyclerView(RecyclerView eventRecyclerView) {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> eventList = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String committeeName = eventSnapshot.child("committeeName").getValue(String.class);
                    String title = eventSnapshot.child("title").getValue(String.class);
                    String description = eventSnapshot.child("description").getValue(String.class);
                    String imageUrl = eventSnapshot.child("imageUrl").getValue(String.class);

                    // Create Event object
                    Event event = new Event(committeeName, title, description, imageUrl);
                    eventList.add(event);
                }
                // Populate RecyclerView with eventList
                EventAdapter eventAdapter = new EventAdapter(eventList, mDatabaseRef);
                eventRecyclerView.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

}
