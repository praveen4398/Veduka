package com.example.vedukamad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText feedbackInput;
    private Button submitButton;
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ratingBar = findViewById(R.id.ratingBar);
        feedbackInput = findViewById(R.id.feedbackInput);
        submitButton = findViewById(R.id.submitButton);

        booking = (Booking) getIntent().getSerializableExtra("booking");

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String feedback = feedbackInput.getText().toString().trim();

            // You can save this to a backend or local database
            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity after submitting
        });
    }
}
