package com.example.vedukamad;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlannerAdapter extends RecyclerView.Adapter<PlannerAdapter.PlannerViewHolder> {

    private Context context;
    private List<Planner> planners;

    public PlannerAdapter(Context context, List<Planner> planners) {
        this.context = context;
        this.planners = planners;
    }

    @NonNull
    @Override
    public PlannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.planner_card, parent, false);
        return new PlannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerViewHolder holder, int position) {
        Planner planner = planners.get(position);
        holder.name.setText(planner.getName());
        holder.location.setText("Location: " + planner.getLocation());
        holder.rating.setText("Rating: " + planner.getRating());
        holder.image.setImageResource(planner.getImageRes());

        // Set click listener for the Book Now button
        holder.bookNowButton.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(context, "Booking with " + planner.getName(), Toast.LENGTH_SHORT).show();

            // Start the BookingActivity
            Intent bookingIntent = new Intent(context, BookingActivity.class);
            bookingIntent.putExtra("planner_name", planner.getName());
            bookingIntent.putExtra("planner_location", planner.getLocation());
            bookingIntent.putExtra("planner_rating", planner.getRating());
            bookingIntent.putExtra("planner_image", planner.getImageRes());
            context.startActivity(bookingIntent);
        });
    }

    @Override
    public int getItemCount() {
        return planners.size();
    }

    static class PlannerViewHolder extends RecyclerView.ViewHolder {
        TextView name, location, rating;
        ImageView image;
        Button bookNowButton;

        public PlannerViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.plannerName);
            location = itemView.findViewById(R.id.plannerLocation);
            rating = itemView.findViewById(R.id.plannerRating);
            image = itemView.findViewById(R.id.plannerImage);
            bookNowButton = itemView.findViewById(R.id.bookNowButton);
        }
    }
}