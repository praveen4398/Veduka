package com.example.vedukamad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class OrganizerApprovalsPendingActivity extends AppCompatActivity {

    private LinearLayout pendingBookingsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_approvals_pednings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Approval Pending");
        }

        pendingBookingsContainer = findViewById(R.id.pending_bookings_container);

        loadPendingBookings();
    }

    private void loadPendingBookings() {
        BookingManager manager = BookingManager.getInstance(this);
        List<Booking> pendingBookings = manager.getPendingApprovalBookings();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Booking booking : pendingBookings) {
            View card = inflater.inflate(R.layout.item_pending_booking, pendingBookingsContainer, false);

            // Assume your item_booking_card.xml has these views:
            ((TextView) card.findViewById(R.id.client_name)).setText(booking.getPlannerName());
            ((TextView) card.findViewById(R.id.event_type)).setText(booking.getPackageType());
            ((TextView) card.findViewById(R.id.event_date_time)).setText(booking.getEventDate());


            // Approve button
            Button approveBtn = card.findViewById(R.id.approve_button);
            approveBtn.setOnClickListener(v -> {
                manager.approveBooking(booking);
                pendingBookingsContainer.removeView(card);
            });

            pendingBookingsContainer.addView(card);
        }

        if (pendingBookings.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No pending approvals.");
            emptyText.setPadding(32, 32, 32, 32);
            pendingBookingsContainer.addView(emptyText);
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
