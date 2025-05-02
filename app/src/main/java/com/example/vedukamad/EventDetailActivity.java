package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView plannerRecyclerView;
    private PlannerAdapter plannerAdapter;
    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;
    private TextView eventTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Find views
        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        eventTitle = findViewById(R.id.eventTitle);

        // Get event type from intent
        String eventType = getIntent().getStringExtra("event_type");
        if (eventType != null && !eventType.isEmpty()) {
            eventTitle.setText(eventType.toUpperCase());
        }

        // Handle the hamburger button click to open/close the drawer
        hamburgerButton.setOnClickListener(v -> {
            if (drawerLayout != null) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            } else {
                Toast.makeText(EventDetailActivity.this, "Drawer layout not found", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup NavigationView and load username/profile image
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

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

        // Setup RecyclerView for planners
        plannerRecyclerView = findViewById(R.id.plannerRecyclerView);

        // Set fixed size to false to allow proper scrolling behavior
        plannerRecyclerView.setHasFixedSize(false);

        // Use GridLayoutManager with 2 columns - preserving the original layout of event boxes
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        plannerRecyclerView.setLayoutManager(gridLayoutManager);

        // Create planner list
        ArrayList<Planner> plannerList = new ArrayList<>();

        if ("Marriage".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Vivah Planners", "Jaipur, India", 4.5f, R.drawable.marriage1));
            plannerList.add(new Planner("Blissful Beginnings", "Jaipur, India", 4.5f, R.drawable.marriage2));
            // You can add more items in the future
        } else if ("Birthday".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Happy Birthdays", "Mumbai, India", 4.7f, R.drawable.marriage1));
            plannerList.add(new Planner("Party Popperz", "Delhi, India", 4.6f, R.drawable.marriage2));
            // You can add more items in the future
        } else if ("Exhibition".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Art Gallery", "Kolkata, India", 4.8f, R.drawable.marriage1));
            plannerList.add(new Planner("Exhibition Masters", "Chennai, India", 4.5f, R.drawable.marriage2));
            // You can add more items in the future
        } else if ("Entertainment".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Fun Zone", "Bangalore, India", 4.9f, R.drawable.marriage1));
            plannerList.add(new Planner("Event Stars", "Hyderabad, India", 4.6f, R.drawable.marriage2));
            // You can add more items in the future
        } else {
            // Add default planners or handle other event types
            plannerList.add(new Planner("Event Experts", "Delhi, India", 4.3f, R.drawable.marriage1));
            plannerList.add(new Planner("Special Occasions", "Hyderabad, India", 4.4f, R.drawable.marriage2));
        }

        plannerAdapter = new PlannerAdapter(this, plannerList);
        plannerRecyclerView.setAdapter(plannerAdapter);

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bookings) {
                // Start the bookings activity
                Intent intent = new Intent(this, YourBookingsActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            // Handle logout action
            if (id == R.id.nav_logout) {
                showLogoutDialog(); // Show logout confirmation dialog
                return true;
            }

            // Handle other navigation items if needed
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open when back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut(); // Logout Firebase user

                    // Clear SharedPreferences if needed
                    SharedPreferences.Editor editor = getSharedPreferences("VedukaPrefs", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    // Go to IntroActivity (login screen or splash screen)
                    Intent intent = new Intent(EventDetailActivity.this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Simply close the dialog and stay on the same screen
                    drawerLayout.closeDrawer(GravityCompat.START); // Close drawer after dismissing dialog
                })
                .show();
    }
}