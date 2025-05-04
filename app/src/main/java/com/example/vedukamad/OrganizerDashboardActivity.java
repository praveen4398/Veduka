package com.example.vedukamad;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OrganizerDashboardActivity extends AppCompatActivity {
    private static final int REQUEST_MAP = 1;

    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;
    private TextView locationTextView;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        locationTextView = findViewById(R.id.location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Hamburger button opens/closes drawer
        hamburgerButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Navigation drawer setup
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String storedUsername = prefs.getString("username", "");
        if (storedUsername == null || storedUsername.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                storedUsername = user.getDisplayName();
                if (storedUsername == null || storedUsername.isEmpty()) {
                    storedUsername = user.getEmail();
                }
            }
        }
        usernameTextView.setText(
                storedUsername != null && !storedUsername.isEmpty() ? storedUsername : "Guest"
        );

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

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bookings)
                startActivity(new Intent(this, YourBookingsActivity.class));
            else if (id == R.id.nav_settings)
                startActivity(new Intent(this, SettingsActivity.class));
            else if (id == R.id.nav_feedback)
                startActivity(new Intent(this, VFeedbackActivity.class));
            else if (id == R.id.nav_about)
                startActivity(new Intent(this, AboutActivity.class));
            else if (id == R.id.nav_logout)
                showLogoutDialog();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Location logic
        getCurrentLocation();

        locationTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, REQUEST_MAP);
        });

        // ... (rest of your dashboard logic: stats, calendar, etc.)
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_MAP
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String city = getCityNameFromCoordinates(location.getLatitude(), location.getLongitude());
                        locationTextView.setText(city != null ? city : "Location unavailable");
                    } else {
                        locationTextView.setText("Location unavailable");
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MAP && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("latitude", 0.0);
            double lng = data.getDoubleExtra("longitude", 0.0);
            String city = getCityNameFromCoordinates(lat, lng);
            if (city != null && !city.isEmpty()) {
                locationTextView.setText(city);
            } else {
                Toast.makeText(this, "City not found for selected location", Toast.LENGTH_SHORT).show();
                locationTextView.setText("Location not found");
            }
        }
    }

    private String getCityNameFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
            if (list != null && !list.isEmpty()) {
                Address addr = list.get(0);
                String city = addr.getLocality();
                if (city == null) city = addr.getAdminArea();
                return city;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences.Editor editor = getSharedPreferences("VedukaPrefs", MODE_PRIVATE).edit();
                    editor.clear().apply();
                    Intent i = new Intent(OrganizerDashboardActivity.this, IntroActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                })
                .setNegativeButton("No", (d, w) -> {
                    d.dismiss();
                    drawerLayout.closeDrawer(GravityCompat.START);
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
