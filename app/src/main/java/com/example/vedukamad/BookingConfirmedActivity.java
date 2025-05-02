package com.example.vedukamad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BookingConfirmedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmed);

        Button btnViewBooking = findViewById(R.id.btn_view_booking);
        btnViewBooking.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmedActivity.this, YourBookingsActivity.class);
            startActivity(intent);
            finish(); // Optional: prevents returning to confirmation page
        });
    }
}
