package com.example.vedukamad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookings;

    public BookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);

        // Set the planner name
        holder.plannerName.setText(booking.getPlannerName());

        // Set location
        holder.plannerLocation.setText("Location: " + booking.getPlannerLocation());

        // Set rating if available
        if (booking.getPlannerRating() > 0) {
            holder.plannerRating.setVisibility(View.VISIBLE);
            holder.plannerRating.setText("★ " + booking.getPlannerRating() + "/5");
        } else {
            holder.plannerRating.setVisibility(View.GONE);
        }

        // Set image
        if (booking.getPlannerImage() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(booking.getPlannerImage())
                    .placeholder(R.drawable.ic_profile)
                    .into(holder.plannerImage);
        } else {
            holder.plannerImage.setImageResource(R.drawable.ic_profile); // Default image
        }

        // Set status button
        if (booking.isCompleted()) {
            holder.statusButton.setText("Completed");
            holder.statusButton.setBackgroundTintList(
                    holder.itemView.getContext().getColorStateList(R.color.gray));
        } else {
            holder.statusButton.setText("Upcoming");
            holder.statusButton.setBackgroundTintList(
                    holder.itemView.getContext().getColorStateList(R.color.purple_500));
        }

        // Add click listener to the entire item
        holder.itemView.setOnClickListener(v -> {
            // You can add intent to open booking details here
            // For now, just log a click
            android.util.Log.d("BookingAdapter", "Clicked on booking: " + booking.getPlannerName());
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView plannerImage;
        TextView plannerName;
        TextView plannerLocation;
        TextView plannerRating;
        Button statusButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            plannerImage = itemView.findViewById(R.id.booking_planner_image);
            plannerName = itemView.findViewById(R.id.booking_planner_name);
            plannerLocation = itemView.findViewById(R.id.booking_planner_location);
            plannerRating = itemView.findViewById(R.id.booking_planner_rating);
            statusButton = itemView.findViewById(R.id.booking_status_button);
        }
    }
}