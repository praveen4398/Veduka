package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class YourBookingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "YourBookingsActivity";
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private TextView emptyView;
    private BookingManager bookingManager;
    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_bookings);

        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        recyclerView = findViewById(R.id.booking_recycler_view);
        emptyView = findViewById(R.id.empty_bookings_view);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Handle the hamburger button click
        hamburgerButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Setup NavigationView and header
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        setupNavigationHeader(navigationView);

        // Initialize BookingManager and load bookings
        bookingManager = BookingManager.getInstance(this);
        loadBookings();
    }

    private void setupNavigationHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

        // Load username
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

        usernameTextView.setText((storedUsername != null && !storedUsername.isEmpty()) ? storedUsername : "Guest");

        // Load profile image
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(profileImageView);
        } else {
            Glide.with(this).load(R.drawable.ic_profile).into(profileImageView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }

    private void loadBookings() {
        List<Booking> bookingList = bookingManager.getBookings();
        Log.d(TAG, "Loading bookings. Count: " + bookingList.size());

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Booking booking : bookingList) {
            try {
                long bookingEventTime = sdf.parse(booking.getEventDate()).getTime();
                if (bookingEventTime < currentTime) {
                    booking.setCompleted(true);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (bookingList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            Log.d(TAG, "No bookings found, showing empty view");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter = new BookingAdapter(bookingList);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "Created new adapter with " + bookingList.size() + " bookings");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_bookings) {
            // Already on this screen
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
                    Intent intent = new Intent(YourBookingsActivity.this, IntroActivity.class);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
