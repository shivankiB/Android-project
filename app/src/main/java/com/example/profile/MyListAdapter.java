package com.example.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListViewHolder> {

    private final List<Event> myList;
    private final DatabaseReference myListRef;
    private final MyListClickListener mListener;

    public interface MyListClickListener {
        void onRemoveClicked(Event event, DatabaseReference myListRef);
    }
    public MyListAdapter(List<Event> myList, DatabaseReference myListRef, com.example.profile.myList list) {
        this.myList = myList;
        this.mListener = list;
        this.myListRef = myListRef;// No need for mListener in this adapter
    }

    @NonNull
    @Override
    public MyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_list_item, parent, false);
        return new MyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListViewHolder holder, int position) {
        Event event = myList.get(position);
        holder.mtitleTextView.setText(event.getTitle());
        holder.mcommitteeTextView.setText(event.getCommitteeName());
        holder.mdescriptionTextView.setText(event.getDescription());

        // Load and display image using Picasso
        Picasso.get().load(event.getImageUrl()).into(holder.meventImageView);

        holder.mRemoveButton.setOnClickListener(v -> {
            mListener.onRemoveClicked(event,myListRef); // Trigger the remove click event
        });
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public static class MyListViewHolder extends RecyclerView.ViewHolder {
        TextView mtitleTextView, mcommitteeTextView, mdescriptionTextView;
        ImageView meventImageView;
        Button mRemoveButton;

        public MyListViewHolder(@NonNull View itemView) {
            super(itemView);
            mtitleTextView = itemView.findViewById(R.id.mtitleTextView);
            mcommitteeTextView = itemView.findViewById(R.id.mcommitteeTextView);
            mdescriptionTextView = itemView.findViewById(R.id.mdescriptionTextView);
            meventImageView = itemView.findViewById(R.id.meventImageView);
            mRemoveButton = itemView.findViewById(R.id.mRemoveButton);
        }
    }
}
