package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VFeedbackActivity extends AppCompatActivity {

    private EditText feedbackEditText;
    private RatingBar ratingBar;
    private Button submitButton;
    private ImageView backButton;
    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vfeedback);

        // Initialize views
        feedbackEditText = findViewById(R.id.feedbackEditText);
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.hamburgerButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);

        // Handle the hamburger button click to open/close the drawer
        hamburgerButton.setOnClickListener(v -> {
            if (drawerLayout != null) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            } else {
                Toast.makeText(VFeedbackActivity.this, "Drawer layout not found", Toast.LENGTH_SHORT).show();
            }
        });
        // Setup NavigationView and load username/profile image
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);
        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, YourBookingsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_bookings) {
                startActivity(new Intent(this, YourBookingsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_feedback) {
                startActivity(new Intent(this, VFeedbackActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (id == R.id.nav_logout) {
                showLogoutDialog();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // Load username from SharedPreferences or Firebase
        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String storedUsername = prefs.getString("username", "");

        if (storedUsername == null || storedUsername.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                storedUsername = user.getDisplayName();
                if (storedUsername == null || storedUsername.isEmpty()) {
                    storedUsername = user.getEmail(); // Fallback to email if display name is null
                }
            }
        }

        // Set the username text
        usernameTextView.setText((storedUsername != null && !storedUsername.isEmpty()) ? storedUsername : "Guest");

        // Load profile image using Glide
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(profileImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .into(profileImageView);
        }

        // Submit button logic
        submitButton.setOnClickListener(v -> {
            String feedback = feedbackEditText.getText().toString().trim();
            float rating = ratingBar.getRating();

            // Validation
            if (feedback.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rating == 0) {
                Toast.makeText(this, "Please rate your experience", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate submission (e.g., Firebase)
            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();

            // Disable button to prevent resubmission
            submitButton.setEnabled(false);
            finish();
        });
    }
    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();

                    SharedPreferences.Editor editor = getSharedPreferences("VedukaPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(VFeedbackActivity.this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                    drawerLayout.closeDrawer(GravityCompat.START);
                })
                .show();
    }
}

