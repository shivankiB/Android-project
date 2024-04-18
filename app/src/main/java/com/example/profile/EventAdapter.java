package com.example.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Event> mEventList;
    private final DatabaseReference mUserListRef; // Reference to the user's "my_list" node

    public EventAdapter(List<Event> eventList, DatabaseReference userListRef) {
        mEventList = eventList;
        mUserListRef = userListRef;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = mEventList.get(position);
        // Bind event data to views
        holder.titleTextView.setText(event.getTitle());
        holder.committeeTextView.setText(event.getCommitteeName());
        holder.descriptionTextView.setText(event.getDescription());

        // Load image using Picasso
        Picasso.get().load(event.getImageUrl()).into(holder.eventImageView);

        holder.eventImageView.setOnClickListener(v -> {
            // Toggle description visibility
            if (holder.descriptionTextView.getVisibility() == View.VISIBLE) {
                holder.descriptionTextView.setVisibility(View.GONE);
            } else {
                holder.descriptionTextView.setVisibility(View.VISIBLE);
            }
        });

        // Set click listener for "Add to My List" button
        holder.addToListButton.setOnClickListener(v -> {
            // Add event details to "my_list" node in the user's UID
            addEventToMyList(event);

            // Notify the user
            Toast.makeText(holder.itemView.getContext(), "Event added to My List", Toast.LENGTH_SHORT).show();
        });
    }

    private void addEventToMyList(Event event) {
        // Get the UID of the current user
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Navigate to the "users" node and then to the UID node of the current user
        DatabaseReference userNodeRef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        // Save event details under the UID node of the current user
        String eventId = userNodeRef.child("my_list").push().getKey(); // Generate a unique key for the event
        assert eventId != null;
        userNodeRef.child("my_list").child(eventId).setValue(event);
    }


    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView titleTextView, committeeTextView, descriptionTextView;
        Button addToListButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.eventImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            committeeTextView = itemView.findViewById(R.id.committeeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            addToListButton = itemView.findViewById(R.id.addToListButton);
        }
    }
}
