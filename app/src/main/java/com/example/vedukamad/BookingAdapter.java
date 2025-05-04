package com.example.vedukamad;

import android.content.Intent;
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

        holder.plannerName.setText(booking.getPlannerName());
        holder.plannerLocation.setText("Location: " + booking.getPlannerLocation());

        if (booking.getPlannerRating() > 0) {
            holder.plannerRating.setVisibility(View.VISIBLE);
            holder.plannerRating.setText("★ " + booking.getPlannerRating() + "/5");
        } else {
            holder.plannerRating.setVisibility(View.GONE);
        }

        if (booking.getPlannerImage() != 0) {
            Glide.with(holder.itemView.getContext())
                    .load(booking.getPlannerImage())
                    .placeholder(R.drawable.ic_profile)
                    .into(holder.plannerImage);
        } else {
            holder.plannerImage.setImageResource(R.drawable.ic_profile);
        }

        if (booking.isCompleted()) {
            holder.statusButton.setText("Completed");
            holder.statusButton.setBackgroundTintList(
                    holder.itemView.getContext().getColorStateList(R.color.gray));
        } else {
            holder.statusButton.setText("Upcoming");
            holder.statusButton.setBackgroundTintList(
                    holder.itemView.getContext().getColorStateList(R.color.purple_500));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), BookingDetailsActivity.class);
            intent.putExtra("booking", booking);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
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
