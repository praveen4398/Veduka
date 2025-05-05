package com.example.vedukamad;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private static final int REQUEST_MAP = 1;

    private DrawerLayout drawerLayout;
    private ImageView hamburgerButton;
    private TextView locationTextView;

    private FusedLocationProviderClient fusedLocationClient;

    private BookingManager bookingManager;
    private LiveData<List<Booking>> bookingsLiveData;

    private TextView approvalPendingCountTextView, currentBookingsCountTextView;
    private TextView totalEarningsMonthTextView, allTimeEarningsTextView;
    private TextView totalBookingsTextView, totalApprovalsTextView;
    private CalendarView calendarView;
    private LinearLayout eventsContainer;
    private TextView businessNameTextView, businessLocationTextView;
    private ShapeableImageView profileImageView;

    private final SimpleDateFormat eventDateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        initializeUIComponents();
        setupNavigation();
        setupLocationTracking();
        initializeBookingManager();
        setupCalendar();
        setProfileSection();
    }

    private void initializeUIComponents() {
        drawerLayout = findViewById(R.id.drawerLayout);
        hamburgerButton = findViewById(R.id.hamburgerButton);
        locationTextView = findViewById(R.id.location);
        approvalPendingCountTextView = findViewById(R.id.approval_pending_count);
        currentBookingsCountTextView = findViewById(R.id.current_bookings_count);
        totalEarningsMonthTextView = findViewById(R.id.total_earnings_month);
        allTimeEarningsTextView = findViewById(R.id.all_time_earnings);
        totalBookingsTextView = findViewById(R.id.total_bookings);
        totalApprovalsTextView = findViewById(R.id.total_approvals);
        calendarView = findViewById(R.id.calendarView);
        eventsContainer = findViewById(R.id.events_container);
        businessNameTextView = findViewById(R.id.business_name);
        businessLocationTextView = findViewById(R.id.business_location);
        profileImageView = findViewById(R.id.profile_image);

        hamburgerButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.approval_pending_section).setOnClickListener(v ->
                startActivity(new Intent(this, OrganizerApprovalsPendingActivity.class)));
        findViewById(R.id.current_bookings_section).setOnClickListener(v ->
                startActivity(new Intent(this, CurrentBookingsActivity.class)));
    }

    private void setProfileSection() {
        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String organizerName = prefs.getString("organizer_name", "");
        String organizerCity = prefs.getString("organizer_city", "");

        // Fallback to Firebase user data if not set in prefs
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if ((organizerName == null || organizerName.isEmpty()) && user != null) {
            organizerName = user.getDisplayName();
            if (organizerName == null || organizerName.isEmpty()) {
                organizerName = user.getEmail();
            }
        }

        // If city not set, fallback to current location (from top bar)
        if (organizerCity == null || organizerCity.isEmpty()) {
            organizerCity = locationTextView.getText().toString();
        }
        if (organizerName == null || organizerName.isEmpty()) organizerName = "Organizer";
        if (organizerCity == null || organizerCity.isEmpty()) organizerCity = "Your City, India";

        businessNameTextView.setText(organizerName);
        businessLocationTextView.setText("Location: " + organizerCity);

        // Profile image (from Firebase)
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
    }

    private void setupNavigation() {
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.username);
        ImageView navProfileImageView = headerView.findViewById(R.id.profile_image);

        SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
        String storedUsername = prefs.getString("username", "");
        if (storedUsername.isEmpty()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                storedUsername = user.getDisplayName();
                if (storedUsername == null || storedUsername.isEmpty()) {
                    storedUsername = user.getEmail();
                }
            }
        }
        usernameTextView.setText(!storedUsername.isEmpty() ? storedUsername : "Guest");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_profile)
                    .into(navProfileImageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_profile)
                    .into(navProfileImageView);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bookings)
                startActivity(new Intent(this, MyEventsActivity.class));
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
    }

    private void setupLocationTracking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();
        locationTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivityForResult(intent, REQUEST_MAP);
        });
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

    private void initializeBookingManager() {
        bookingManager = BookingManager.getInstance(this);
        bookingsLiveData = bookingManager.getBookingsLiveData();
        bookingsLiveData.observe(this, bookings -> updateDashboardStats());
    }

    private void setupCalendar() {
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth, 0, 0, 0);
                checkEventsForDate(selected);
            });
        }
    }

    private void checkEventsForDate(Calendar selectedDate) {
        List<Booking> bookings = bookingsLiveData.getValue();
        if (bookings == null) return;

        List<Booking> eventsOnDate = new ArrayList<>();
        for (Booking booking : bookings) {
            try {
                Date eventDate = eventDateFormat.parse(booking.getEventDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(eventDate);
                if (isSameDay(cal, selectedDate)) {
                    eventsOnDate.add(booking);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        showEventDialog(eventsOnDate, selectedDate);
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void showEventDialog(List<Booking> events, Calendar date) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Events on " + new SimpleDateFormat("MMM d, yyyy").format(date.getTime()));

        if (events.isEmpty()) {
            builder.setMessage("No events scheduled for this date.");
        } else {
            StringBuilder details = new StringBuilder();
            for (Booking event : events) {
                details.append("• ").append(event.getPackageType())
                        .append("\nTime: ").append(event.getEventTime())
                        .append("\nPrice: ₹").append(event.getTotalPrice())
                        .append("\n\n");
            }
            builder.setMessage(details.toString());
        }
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void updateDashboardStats() {
        List<Booking> bookings = bookingsLiveData.getValue();
        if (bookings == null) return;

        // Approval Pending and Current Bookings
        approvalPendingCountTextView.setText(String.valueOf(bookingManager.getPendingApprovalCount()));
        currentBookingsCountTextView.setText(String.valueOf(bookingManager.getCurrentBookingsCount()));

        // Total Bookings and Approvals
        int totalBookings = bookings.size();
        int totalApprovals = 0;
        for (Booking booking : bookings) {
            if (booking.isApproved()) totalApprovals++;
        }
        totalBookingsTextView.setText(String.valueOf(totalBookings));
        totalApprovalsTextView.setText(String.valueOf(totalApprovals));

        // Earnings
        long allTimeEarnings = 0;
        long thisMonthEarnings = 0;
        Calendar currentCalendar = Calendar.getInstance();

        for (Booking booking : bookings) {
            int amount = booking.getTotalPrice();
            allTimeEarnings += amount;
            try {
                Date eventDate = eventDateFormat.parse(booking.getEventDate());
                Calendar bookingCalendar = Calendar.getInstance();
                bookingCalendar.setTime(eventDate);
                if (bookingCalendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                        bookingCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)) {
                    thisMonthEarnings += amount;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        totalEarningsMonthTextView.setText(formatCurrency(thisMonthEarnings));
        allTimeEarningsTextView.setText(formatCurrency(allTimeEarnings));

        // Update upcoming events list
        updateUpcomingEvents();
    }

    private void updateUpcomingEvents() {
        eventsContainer.removeAllViews();
        List<Booking> bookings = bookingsLiveData.getValue();
        if (bookings == null) return;

        List<Booking> upcomingEvents = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        for (Booking booking : bookings) {
            try {
                Date eventDate = eventDateFormat.parse(booking.getEventDate());
                if (eventDate.after(now.getTime())) {
                    upcomingEvents.add(booking);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(upcomingEvents, new Comparator<Booking>() {
            @Override
            public int compare(Booking b1, Booking b2) {
                try {
                    Date d1 = eventDateFormat.parse(b1.getEventDate());
                    Date d2 = eventDateFormat.parse(b2.getEventDate());
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        for (Booking event : upcomingEvents) {
            addEventToLayout(event);
        }
    }

    private void addEventToLayout(Booking event) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textView.setText(String.format(Locale.getDefault(),
                "%s\nDate: %s\nTime: %s\nPrice: ₹%,d",
                event.getPackageType(),
                event.getFormattedDate(),
                event.getEventTime(),
                event.getTotalPrice()
        ));
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(14);
        eventsContainer.addView(textView);
    }

    private String formatCurrency(long amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        format.setMaximumFractionDigits(0);
        return format.format(amount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookingManager.refreshBookings();
        setProfileSection();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
}
