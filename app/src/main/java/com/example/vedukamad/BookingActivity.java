package com.example.vedukamad;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {
    private static final String TAG = "BookingActivity";

    private int basicPackagePrice = 60000;
    private int premiumPackagePrice = 120000;
    private int musicAddonPrice;
    private int showsAddonPrice;
    private int cateringAddonPrice;

    private TextView tvBasePrice, tvAddonsPrice, tvTotalPrice;
    private RadioGroup packageRadioGroup;
    private CheckBox cbMusic, cbShows, cbCatering;
    private DatePicker datePicker;
    private TimePicker timePicker;

    private TextInputEditText editName, editPhone, editEmail, editAddress;

    private String plannerName, plannerLocation;
    private float plannerRating;
    private int plannerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Get planner data
        plannerName = getIntent().getStringExtra("planner_name");
        plannerLocation = getIntent().getStringExtra("planner_location");
        plannerRating = getIntent().getFloatExtra("planner_rating", 0.0f);
        plannerImage = getIntent().getIntExtra("planner_image", R.drawable.ic_launcher_background);

        // Addon prices from intent
        musicAddonPrice = getIntent().getIntExtra("music_addon_price", 15000);
        showsAddonPrice = getIntent().getIntExtra("shows_addon_price", 25000);
        cateringAddonPrice = getIntent().getIntExtra("catering_addon_price", 20000);

        // Input fields
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editEmail = findViewById(R.id.edit_email);
        editAddress = findViewById(R.id.edit_address);
        timePicker = findViewById(R.id.time_picker);
        datePicker = findViewById(R.id.date_picker);

        // Price text views
        tvBasePrice = findViewById(R.id.text_base_price);
        tvAddonsPrice = findViewById(R.id.text_addons_price);
        tvTotalPrice = findViewById(R.id.text_total_price);

        // Selection views
        packageRadioGroup = findViewById(R.id.package_radio_group);
        cbMusic = findViewById(R.id.checkbox_music);
        cbShows = findViewById(R.id.checkbox_shows);
        cbCatering = findViewById(R.id.checkbox_catering);

        // Planner details
        TextView tvPlannerName = findViewById(R.id.booking_planner_name);
        TextView tvPlannerLocation = findViewById(R.id.booking_planner_location);
        TextView tvPlannerRating = findViewById(R.id.booking_planner_rating);
        ImageView ivPlannerImage = findViewById(R.id.booking_planner_image);
        tvPlannerName.setText(plannerName);
        tvPlannerLocation.setText("Location: " + plannerLocation);
        tvPlannerRating.setText("Rating: " + plannerRating);
        ivPlannerImage.setImageResource(plannerImage);

        // Style pickers
        datePicker.setBackgroundColor(getResources().getColor(android.R.color.white));
        timePicker.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Listeners
        packageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updatePriceDisplays());
        cbMusic.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceDisplays());
        cbShows.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceDisplays());
        cbCatering.setOnCheckedChangeListener((buttonView, isChecked) -> updatePriceDisplays());

        // Load dynamic prices from Firestore
        loadDynamicPricing();

        // Button
        Button btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
        btnConfirmBooking.setOnClickListener(v -> {
            if (validateInputs()) {
                saveBooking();
                startActivity(new Intent(this, BookingConfirmedActivity.class));

                new android.os.Handler().postDelayed(() -> {
                    startActivity(new Intent(this, YourBookingsActivity.class));
                    finish();
                }, 1500);
            }
        });
    }

    private void loadDynamicPricing() {
        FirebaseFirestore.getInstance().collection("pricing")
                .document("current")
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        try {
                            basicPackagePrice = document.getLong("basic").intValue();
                            premiumPackagePrice = document.getLong("premium").intValue();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing dynamic prices", e);
                        }
                        updatePriceDisplays(); // Update UI after price fetch
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load pricing", e));
    }

    private void saveBooking() {
        String packageType = packageRadioGroup.getCheckedRadioButtonId() == R.id.radio_premium_package
                ? "Premium Package" : "Basic Package";

        int totalPrice = calculateTotalPrice();
        String eventDate = formatEventDate();

        Booking newBooking = new Booking(
                plannerName, plannerLocation, plannerRating, plannerImage,
                packageType, totalPrice, eventDate
        );

        BookingManager.getInstance(this).saveBooking(newBooking);
        Log.d(TAG, "Booking saved: " + plannerName + " - " + eventDate + " - ₹" + totalPrice);
    }

    private String formatEventDate() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        return String.format(Locale.getDefault(),
                "%d %s %d at %s", day, monthNames[month - 1], year, formattedTime);
    }

    private int calculateTotalPrice() {
        int basePrice = (packageRadioGroup.getCheckedRadioButtonId() == R.id.radio_premium_package)
                ? premiumPackagePrice : basicPackagePrice;
        int addonsPrice = 0;
        if (cbMusic.isChecked()) addonsPrice += musicAddonPrice;
        if (cbShows.isChecked()) addonsPrice += showsAddonPrice;
        if (cbCatering.isChecked()) addonsPrice += cateringAddonPrice;

        return basePrice + addonsPrice;
    }

    private boolean validateInputs() {
        if (editName.getText().toString().trim().isEmpty()) {
            editName.setError("Please enter your name");
            editName.requestFocus();
            return false;
        }

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

        if (editAddress.getText().toString().trim().isEmpty()) {
            editAddress.setError("Please enter your address");
            editAddress.requestFocus();
            return false;
        }

        return true;
    }

    private void updatePriceDisplays() {
        int totalPrice = calculateTotalPrice();
        int basePrice = (packageRadioGroup.getCheckedRadioButtonId() == R.id.radio_premium_package)
                ? premiumPackagePrice : basicPackagePrice;
        int addonsPrice = totalPrice - basePrice;

        RadioButton rbBasic = findViewById(R.id.radio_basic_package);
        RadioButton rbPremium = findViewById(R.id.radio_premium_package);
        rbBasic.setText("Basic Package - ₹" + formatPrice(basicPackagePrice) + " (Venue selection, vendor management, and ceremony planning)");
        rbPremium.setText("Premium Package - ₹" + formatPrice(premiumPackagePrice) + " (Everything in the basic package plus luxury decor, theme-based planning, and extensive guest services)");

        cbMusic.setText("Music - Live band/DJ services (₹" + formatPrice(musicAddonPrice) + ")");
        cbShows.setText("Shows - Cultural performances/Entertainment (₹" + formatPrice(showsAddonPrice) + ")");
        cbCatering.setText("Catering - Custom menu and service (₹" + formatPrice(cateringAddonPrice) + ")");

        tvBasePrice.setText("Base Package: ₹" + formatPrice(basePrice));
        tvAddonsPrice.setText("Add-ons: ₹" + formatPrice(addonsPrice));
        tvTotalPrice.setText("Total: ₹" + formatPrice(totalPrice));
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(new Locale("en", "IN")).format(price);
    }
}
