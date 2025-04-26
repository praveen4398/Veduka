package com.example.vedukamad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    // Price constants
    private int basicPackagePrice;
    private int premiumPackagePrice;
    private int musicAddonPrice;
    private int showsAddonPrice;
    private int cateringAddonPrice;

    private TextView tvBasePrice;
    private TextView tvAddonsPrice;
    private TextView tvTotalPrice;
    private RadioGroup packageRadioGroup;
    private CheckBox cbMusic;
    private CheckBox cbShows;
    private CheckBox cbCatering;

    // User detail fields
    private TextInputEditText editName;
    private TextInputEditText editPhone;
    private TextInputEditText editEmail;
    private TextInputEditText editAddress;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get data from intent
        String plannerName = getIntent().getStringExtra("planner_name");
        String plannerLocation = getIntent().getStringExtra("planner_location");
        float plannerRating = getIntent().getFloatExtra("planner_rating", 0.0f);
        int plannerImage = getIntent().getIntExtra("planner_image", R.drawable.ic_launcher_background);

        // Get planner-specific pricing (default values if not provided)
        basicPackagePrice = getIntent().getIntExtra("basic_package_price", 60000);
        premiumPackagePrice = getIntent().getIntExtra("premium_package_price", 120000);
        musicAddonPrice = getIntent().getIntExtra("music_addon_price", 15000);
        showsAddonPrice = getIntent().getIntExtra("shows_addon_price", 25000);
        cateringAddonPrice = getIntent().getIntExtra("catering_addon_price", 20000);

        // Initialize user details fields
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editAddress = findViewById(R.id.edit_address);
        timePicker = findViewById(R.id.time_picker);

        // Set data to views
        TextView tvPlannerName = findViewById(R.id.booking_planner_name);
        TextView tvPlannerLocation = findViewById(R.id.booking_planner_location);
        TextView tvPlannerRating = findViewById(R.id.booking_planner_rating);
        ImageView ivPlannerImage = findViewById(R.id.booking_planner_image);
        Button btnConfirmBooking = findViewById(R.id.btn_confirm_booking);

        // Initialize price summary views
        tvBasePrice = findViewById(R.id.text_base_price);
        tvAddonsPrice = findViewById(R.id.text_addons_price);
        tvTotalPrice = findViewById(R.id.text_total_price);

        // Initialize selection views
        packageRadioGroup = findViewById(R.id.package_radio_group);
        cbMusic = findViewById(R.id.checkbox_music);
        cbShows = findViewById(R.id.checkbox_shows);
        cbCatering = findViewById(R.id.checkbox_catering);

        // Update radio button texts with actual prices
        RadioButton rbBasic = findViewById(R.id.radio_basic_package);
        RadioButton rbPremium = findViewById(R.id.radio_premium_package);
        rbBasic.setText("Basic Package - ₹" + formatPrice(basicPackagePrice) + " (Venue selection, vendor management, and ceremony planning)");
        rbPremium.setText("Premium Package - ₹" + formatPrice(premiumPackagePrice) + " (Everything in the basic package plus luxury decor, theme-based planning, and extensive guest services)");

        // Update checkbox texts with actual prices
        cbMusic.setText("Music - Live band/DJ services (₹" + formatPrice(musicAddonPrice) + ")");
        cbShows.setText("Shows - Cultural performances/Entertainment (₹" + formatPrice(showsAddonPrice) + ")");
        cbCatering.setText("Catering - Custom menu and service (₹" + formatPrice(cateringAddonPrice) + ")");

        // Style DatePicker
        DatePicker datePicker = findViewById(R.id.date_picker);
        datePicker.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Style TimePicker
        timePicker.setBackgroundColor(getResources().getColor(android.R.color.white));

        tvPlannerName.setText(plannerName);
        tvPlannerLocation.setText("Location: " + plannerLocation);
        tvPlannerRating.setText("Rating: " + plannerRating);
        ivPlannerImage.setImageResource(plannerImage);

        // Setup listeners for price updates
        packageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updatePriceSummary());

        cbMusic.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceSummary());
        cbShows.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceSummary());
        cbCatering.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceSummary());

        // Initial price summary
        updatePriceSummary();

        // Handle confirm booking button click
        btnConfirmBooking.setOnClickListener(v -> {
            // Validate input fields
            if (validateInputs()) {
                // Show confirmation message
                Toast.makeText(this, "Booking confirmed with " + plannerName, Toast.LENGTH_LONG).show();

                // Here you would typically save the booking to a database

                // Close the activity after a short delay
                new android.os.Handler().postDelayed(this::finish, 1500);
            }
        });
    }

    private boolean validateInputs() {
        // Check if name is entered
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError("Please enter your name");
            editName.requestFocus();
            return false;
        }

        // Check if phone number is entered and valid
        String phone = editPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            editPhone.setError("Please enter your phone number");
            editPhone.requestFocus();
            return false;
        } else if (phone.length() < 10) {
            editPhone.setError("Please enter a valid phone number");
            editPhone.requestFocus();
            return false;
        }

        // Check if email is entered and valid
        String email = editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            editEmail.setError("Please enter your email");
            editEmail.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid email address");
            editEmail.requestFocus();
            return false;
        }

        // Check if address is entered
        if (editAddress.getText().toString().trim().isEmpty()) {
            editAddress.setError("Please enter your address");
            editAddress.requestFocus();
            return false;
        }

        // All validations passed
        return true;
    }

    private void updatePriceSummary() {
        int basePrice;
        int addonsPrice = 0;

        // Calculate base price based on selected package
        if (packageRadioGroup.getCheckedRadioButtonId() == R.id.radio_premium_package) {
            basePrice = premiumPackagePrice;
        } else {
            basePrice = basicPackagePrice;
        }

        // Calculate add-ons price
        if (cbMusic.isChecked()) {
            addonsPrice += musicAddonPrice;
        }
        if (cbShows.isChecked()) {
            addonsPrice += showsAddonPrice;
        }
        if (cbCatering.isChecked()) {
            addonsPrice += cateringAddonPrice;
        }

        // Calculate total
        int totalPrice = basePrice + addonsPrice;

        // Update UI
        tvBasePrice.setText("Base Package: ₹" + formatPrice(basePrice));
        tvAddonsPrice.setText("Add-ons: ₹" + formatPrice(addonsPrice));
        tvTotalPrice.setText("Total: ₹" + formatPrice(totalPrice));
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(new Locale("en", "IN")).format(price);
    }
}