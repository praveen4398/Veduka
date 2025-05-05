package com.example.vedukamad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingDetailsActivity extends AppCompatActivity {

    private static final String TAG = "BookingDetailsActivity";

    private ImageView plannerImage;
    private TextView plannerName, plannerLocation, plannerRating,
            plannerStatus, packageType, totalPrice, eventDate;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        // Initialize UI components
        initializeViews();

        // Get booking data from intent
        Booking booking = null;
        try {
            booking = (Booking) getIntent().getSerializableExtra("booking");
        } catch (Exception e) {
            Log.e(TAG, "Error getting booking from intent", e);
            Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (booking != null) {
            // Display booking details
            displayBookingDetails(booking);

            // Set up appropriate action button based on event date
            setupActionButton(booking);
        } else {
            Log.e(TAG, "Booking object is null");
            Toast.makeText(this, "Error: Booking data not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        plannerImage = findViewById(R.id.detail_planner_image);
        plannerName = findViewById(R.id.detail_planner_name);
        plannerLocation = findViewById(R.id.detail_planner_location);
        plannerRating = findViewById(R.id.detail_planner_rating);
        plannerStatus = findViewById(R.id.detail_planner_status);
        packageType = findViewById(R.id.detail_package_type);
        totalPrice = findViewById(R.id.detail_total_price);
        eventDate = findViewById(R.id.detail_event_date);
        actionButton = findViewById(R.id.edit_cancel_button);
    }

    private void displayBookingDetails(Booking booking) {
        plannerName.setText(booking.getPlannerName());
        plannerLocation.setText("Location: " + booking.getPlannerLocation());
        plannerRating.setText("Rating: " + booking.getPlannerRating() + "/5");
        packageType.setText("Package: " + booking.getPackageType());
        totalPrice.setText("Total Price: ₹" + booking.getTotalPrice());
        eventDate.setText("Event Date: " + booking.getEventDate());

        // Load planner image
        if (booking.getPlannerImage() != 0) {
            Glide.with(this).load(booking.getPlannerImage()).into(plannerImage);
        } else {
            plannerImage.setImageResource(R.drawable.ic_profile);
        }
    }

    private void setupActionButton(Booking booking) {
        String dateString = booking.getEventDate();
        Log.d(TAG, "Event date from booking: " + dateString);

        // Check if the date format is correct
        if (dateString == null || dateString.isEmpty()) {
            Log.e(TAG, "Event date is null or empty");
            handleDateParseError();
            return;
        }

        // Convert the event date (String) to Date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date bookingEventDate = sdf.parse(dateString);
            Date currentDate = new Date();

            Log.d(TAG, "Booking date: " + bookingEventDate);
            Log.d(TAG, "Current date: " + currentDate);

            // Check if the event date has passed
            if (bookingEventDate != null && bookingEventDate.before(currentDate)) {
                setupFeedbackButton(booking);
            } else {
                setupEditCancelButton(booking);
            }

        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date: " + dateString, e);
            handleDateParseError();
        }
    }

    private void setupFeedbackButton(Booking booking) {
        // If the event has passed
        plannerStatus.setText("Status: Completed");
        actionButton.setText("Give Feedback");

        actionButton.setOnClickListener(v -> {
            Log.d(TAG, "Feedback button clicked");
            try {
                Intent intent = new Intent(BookingDetailsActivity.this, FeedbackActivity.class);
                intent.putExtra("booking", booking);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching FeedbackActivity", e);
                Toast.makeText(BookingDetailsActivity.this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupEditCancelButton(Booking booking) {
        // If the event is upcoming
        plannerStatus.setText("Status: Upcoming");
        actionButton.setText("Edit / Cancel");

        actionButton.setOnClickListener(v -> {
            Log.d(TAG, "Edit/Cancel button clicked");
            Toast.makeText(BookingDetailsActivity.this, "Navigating to edit/cancel page", Toast.LENGTH_SHORT).show();

            try {
                // Make sure the class is correctly named and exists
                Intent intent = new Intent(BookingDetailsActivity.this, EditCancelBookingActivity.class);

                // Check if the booking object is valid before adding it as an extra
                if (booking != null) {
                    Log.d(TAG, "Adding booking to intent: " + booking);
                    intent.putExtra("booking", booking);
                } else {
                    Log.e(TAG, "Booking is null before adding to intent");
                }

                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching EditCancelBookingActivity", e);
                Toast.makeText(BookingDetailsActivity.this,
                        "Error launching edit page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleDateParseError() {
        // Handle the case where date parsing fails
        plannerStatus.setText("Status: Unknown");
        actionButton.setText("Edit/Cancel");
        Toast.makeText(this, "Error processing event date", Toast.LENGTH_SHORT).show();

        // Default to edit/cancel for safety
        actionButton.setOnClickListener(v -> {
            Log.d(TAG, "Default button clicked after date parse error");
            try {
                Intent intent = new Intent(BookingDetailsActivity.this, EditCancelBookingActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching default activity", e);
                Toast.makeText(BookingDetailsActivity.this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}