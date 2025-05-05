package com.example.vedukamad;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditBookingActivity extends AppCompatActivity {

    private static final String TAG = "EditBookingActivity";

    private EditText editName, editPhone, editEmail, editAddress, editPackage, editDateTime, editAddons;
    private Button btnSave;
    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_booking);

        // Initialize UI components
        initializeViews();

        // Set text color for all EditText fields
        setEditTextColors();

        // Get booking from intent
        try {
            if (getIntent().hasExtra("booking")) {
                booking = (Booking) getIntent().getSerializableExtra("booking");
                Log.d(TAG, "Received booking: " + (booking != null ? booking.toString() : "null"));

                if (booking != null) {
                    populateFields();
                } else {
                    Log.e(TAG, "Booking object is null");
                    Toast.makeText(this, "Error: Booking data not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "No booking extra in intent");
                Toast.makeText(this, "Error: No booking data provided", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting booking from intent", e);
            Toast.makeText(this, "Error loading booking details: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        // Set up save button
        setupSaveButton();
    }

    private void initializeViews() {
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editAddress = findViewById(R.id.edit_address);
        editPackage = findViewById(R.id.edit_package);
        editDateTime = findViewById(R.id.edit_date_time);
        editAddons = findViewById(R.id.edit_addons);
        btnSave = findViewById(R.id.btn_save);
    }

    private void setEditTextColors() {
        // Set text color to black for all EditText fields
        editName.setTextColor(Color.BLACK);
        editPhone.setTextColor(Color.BLACK);
        editEmail.setTextColor(Color.BLACK);
        editAddress.setTextColor(Color.BLACK);
        editPackage.setTextColor(Color.BLACK);
        editDateTime.setTextColor(Color.BLACK);
        editAddons.setTextColor(Color.BLACK);

        // You can also set hint text color if needed
        editName.setHintTextColor(Color.GRAY);
        editPhone.setHintTextColor(Color.GRAY);
        editEmail.setHintTextColor(Color.GRAY);
        editAddress.setHintTextColor(Color.GRAY);
        editPackage.setHintTextColor(Color.GRAY);
        editDateTime.setHintTextColor(Color.GRAY);
        editAddons.setHintTextColor(Color.GRAY);
    }

    private void populateFields() {
        // Set data from booking to EditText fields
        try {
            editName.setText(booking.getPlannerName());
            editAddress.setText(booking.getPlannerLocation());
            editPackage.setText(booking.getPackageType());
            editDateTime.setText(booking.getEventDate());

            // Check if the booking has these additional fields
            if (booking.getPhoneNumber() != null) {
                editPhone.setText(booking.getPhoneNumber());
            }

            if (booking.getEmail() != null) {
                editEmail.setText(booking.getEmail());
            }

            if (booking.getAddOns() != null) {
                editAddons.setText(booking.getAddOns());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields", e);
            Toast.makeText(this, "Error loading booking details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                updateBooking();
            }
        });
    }

    private boolean validateInputs() {
        // Validate required fields
        if (TextUtils.isEmpty(editName.getText().toString().trim())) {
            editName.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(editDateTime.getText().toString().trim())) {
            editDateTime.setError("Event date and time is required");
            return false;
        }

        // Add other validations as needed

        return true;
    }

    private void updateBooking() {
        try {
            if (booking != null) {
                // Collect edited data
                String name = editName.getText().toString().trim();
                String phone = editPhone.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String address = editAddress.getText().toString().trim();
                String pkg = editPackage.getText().toString().trim();
                String dateTime = editDateTime.getText().toString().trim();
                String addons = editAddons.getText().toString().trim();

                // Update booking object
                booking.setPlannerName(name);
                booking.setPlannerLocation(address);
                booking.setPackageType(pkg);
                booking.setEventDate(dateTime);

                // Update additional fields if they exist in Booking class
                try {
                    booking.setPhoneNumber(phone);
                } catch (NoSuchMethodError e) {
                    Log.w(TAG, "setPhoneNumber not available in Booking class");
                }

                try {
                    booking.setEmail(email);
                } catch (NoSuchMethodError e) {
                    Log.w(TAG, "setEmail not available in Booking class");
                }

                try {
                    booking.setAddOns(addons);
                } catch (NoSuchMethodError e) {
                    Log.w(TAG, "setAddOns not available in Booking class");
                }

                // TODO: Save updated booking to database or backend here

                Toast.makeText(this, "Booking updated successfully!", Toast.LENGTH_SHORT).show();

                // Return the updated booking to the calling activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("booking", booking);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Error: Booking data not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating booking", e);
            Toast.makeText(this, "Error updating booking: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}