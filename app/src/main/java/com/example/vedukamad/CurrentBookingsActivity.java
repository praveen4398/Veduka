package com.example.vedukamad;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CurrentBookingsActivity extends AppCompatActivity {

    private LinearLayout currentBookingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_current_bookings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Current Bookings");
        }

        currentBookingsContainer = findViewById(R.id.current_bookings_container);

        loadCurrentBookings();
    }

    private void loadCurrentBookings() {
        BookingManager manager = BookingManager.getInstance(this);
        List<Booking> currentBookings = manager.getCurrentBookings();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Booking booking : currentBookings) {
            View card = inflater.inflate(R.layout.item_pending_booking, currentBookingsContainer, false);

            // Set data from Booking model
            ((TextView) card.findViewById(R.id.client_name)).setText(booking.getPlannerName());
            ((TextView) card.findViewById(R.id.event_type)).setText(booking.getPackageType());
            ((TextView) card.findViewById(R.id.event_date_time)).setText(booking.getEventDate());

            // Hide the approve button for current bookings
            Button approveBtn = card.findViewById(R.id.approve_button);
            approveBtn.setVisibility(View.GONE);

            currentBookingsContainer.addView(card);
        }

        if (currentBookings.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No current bookings.");
            emptyText.setTextColor(Color.WHITE);
            emptyText.setPadding(32, 32, 32, 32);
            currentBookingsContainer.addView(emptyText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
