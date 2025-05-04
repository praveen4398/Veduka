package com.example.vedukamad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingDetailsActivity extends AppCompatActivity {

    private ImageView plannerImage;
    private TextView plannerName, plannerLocation, plannerRating,
            plannerStatus, packageType, totalPrice, eventDate;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        plannerImage = findViewById(R.id.detail_planner_image);
        plannerName = findViewById(R.id.detail_planner_name);
        plannerLocation = findViewById(R.id.detail_planner_location);
        plannerRating = findViewById(R.id.detail_planner_rating);
        plannerStatus = findViewById(R.id.detail_planner_status);
        packageType = findViewById(R.id.detail_package_type);
        totalPrice = findViewById(R.id.detail_total_price);
        eventDate = findViewById(R.id.detail_event_date);
        actionButton = findViewById(R.id.edit_cancel_button);

        Booking booking = (Booking) getIntent().getSerializableExtra("booking");

        if (booking != null) {
            plannerName.setText(booking.getPlannerName());
            plannerLocation.setText("Location: " + booking.getPlannerLocation());
            plannerRating.setText("Rating: " + booking.getPlannerRating() + "/5");
            packageType.setText("Package: " + booking.getPackageType());
            totalPrice.setText("Total Price: ₹" + booking.getTotalPrice());
            eventDate.setText("Event Date: " + booking.getEventDate());  // Display event date

            if (booking.getPlannerImage() != 0) {
                Glide.with(this).load(booking.getPlannerImage()).into(plannerImage);
            } else {
                plannerImage.setImageResource(R.drawable.ic_profile);
            }

            // Convert the event date (String) to Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date bookingEventDate = sdf.parse(booking.getEventDate());  // Parse string to Date
                Date currentDate = new Date();  // Get current date and time

                // Check if the event date has passed
                if (bookingEventDate != null && bookingEventDate.before(currentDate)) {
                    // If the event has passed
                    plannerStatus.setText("Status: Completed");
                    actionButton.setText("Give Feedback");

                    actionButton.setOnClickListener(v -> {
                        Intent intent = new Intent(this, FeedbackActivity.class);
                        intent.putExtra("booking", booking);
                        startActivity(intent);
                    });
                } else {
                    // If the event is upcoming
                    plannerStatus.setText("Status: Upcoming");
                    actionButton.setText("Edit / Cancel");

                    actionButton.setOnClickListener(v -> {
                        // TODO: Implement Edit/Cancel functionality
                    });
                }

            } catch (ParseException e) {
                e.printStackTrace();
                // Handle parse error (e.g., log or show an error message)
            }
        }
    }
}
