package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView plannerRecyclerView;
    private PlannerAdapter plannerAdapter;
    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;
    private TextView eventTitle;
    private EditText searchPlanners;
    private TextView locationTextView;
    private FusedLocationProviderClient fusedLocationClient;
    private ArrayList<Planner> allPlanners = new ArrayList<>();

    // Static local map for demo — Replace with Firebase fetch in future
    private static final Map<String, List<Planner>> plannerDataMap = new HashMap<>();
    static {
        List<Planner> marriagePlanners = new ArrayList<>();
        marriagePlanners.add(new Planner("Vivah Planners", "Jaipur, India", 4.5f, R.drawable.marriage1));
        marriagePlanners.add(new Planner("Blissful Beginnings", "Jaipur, India", 4.5f, R.drawable.marriage2));

        List<Planner> birthdayPlanners = new ArrayList<>();
        birthdayPlanners.add(new Planner("Happy Birthdays", "Mumbai, India", 4.7f, R.drawable.marriage1));
        birthdayPlanners.add(new Planner("Party Popperz", "Delhi, India", 4.6f, R.drawable.marriage2));

        List<Planner> exhibitionPlanners = new ArrayList<>();
        exhibitionPlanners.add(new Planner("Art Gallery", "Kolkata, India", 4.8f, R.drawable.marriage1));
        exhibitionPlanners.add(new Planner("Exhibition Masters", "Chennai, India", 4.5f, R.drawable.marriage2));

        List<Planner> entertainmentPlanners = new ArrayList<>();
        entertainmentPlanners.add(new Planner("Fun Zone", "Bangalore, India", 4.9f, R.drawable.marriage1));
        entertainmentPlanners.add(new Planner("Event Stars", "Hyderabad, India", 4.6f, R.drawable.marriage2));

        List<Planner> defaultPlanners = new ArrayList<>();
        defaultPlanners.add(new Planner("Event Experts", "Delhi, India", 4.3f, R.drawable.marriage1));
        defaultPlanners.add(new Planner("Special Occasions", "Hyderabad, India", 4.4f, R.drawable.marriage2));

        plannerDataMap.put("marriage", marriagePlanners);
        plannerDataMap.put("birthday", birthdayPlanners);
        plannerDataMap.put("exhibition", exhibitionPlanners);
        plannerDataMap.put("entertainment", entertainmentPlanners);
        plannerDataMap.put("default", defaultPlanners);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        eventTitle = findViewById(R.id.eventTitle);
        searchPlanners = findViewById(R.id.searchPlanners);
        locationTextView = findViewById(R.id.location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Get event type and show it
        String eventType = getIntent().getStringExtra("event_type");
        if (eventType != null && !eventType.isEmpty()) {
            eventTitle.setText(eventType.toUpperCase());
        }

        // Hamburger menu setup
        hamburgerButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Load Navigation Header Info
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView profileImageView = headerView.findViewById(R.id.profile_image);

        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String storedUsername = prefs.getString("username", "");
        if (storedUsername == null || storedUsername.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                storedUsername = user.getDisplayName() != null ? user.getDisplayName() : user.getEmail();
            }
        }
        usernameTextView.setText((storedUsername != null && !storedUsername.isEmpty()) ? storedUsername : "Guest");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.ic_profile).into(profileImageView);
        } else {
            Glide.with(this).load(R.drawable.ic_profile).into(profileImageView);
        }

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

        // Set up RecyclerView
        plannerRecyclerView = findViewById(R.id.plannerRecyclerView);
        plannerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        String key = eventType != null ? eventType.toLowerCase() : "default";
        allPlanners = new ArrayList<>(plannerDataMap.getOrDefault(key, plannerDataMap.get("default")));

        plannerAdapter = new PlannerAdapter(this, new ArrayList<>(allPlanners));
        plannerRecyclerView.setAdapter(plannerAdapter);

        // Dynamic search filtering
        searchPlanners.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Planner> filteredList = new ArrayList<>();
                for (Planner planner : allPlanners) {
                    if (planner.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredList.add(planner);
                    }
                }
                plannerAdapter = new PlannerAdapter(EventDetailActivity.this, filteredList);
                plannerRecyclerView.setAdapter(plannerAdapter);
            }
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
                    startActivity(new Intent(EventDetailActivity.this, IntroActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
