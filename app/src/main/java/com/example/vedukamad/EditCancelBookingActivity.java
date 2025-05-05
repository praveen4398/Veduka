package com.example.vedukamad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditCancelBookingActivity extends AppCompatActivity {

    private Button btnEdit, btnCancel;
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cancel_booking);

        btnEdit = findViewById(R.id.btn_edit_booking);
        btnCancel = findViewById(R.id.btn_cancel_booking);

        booking = (Booking) getIntent().getSerializableExtra("booking");

        btnEdit.setOnClickListener(v -> {
            // Launch an EditBookingActivity or show an edit dialog
            Intent intent = new Intent(this, EditBookingActivity.class);
            intent.putExtra("booking", booking);
            startActivity(intent);
        });

        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel this booking? This action cannot be undone.")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        // TODO: Cancel booking in your database/backend
                        // bookingManager.cancelBooking(booking);
                        Toast.makeText(this, "Booking cancelled.", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
