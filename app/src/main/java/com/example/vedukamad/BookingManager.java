package com.example.vedukamad;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingManager {
    private static final String PREF_NAME = "booking_preferences";
    private static final String BOOKINGS_KEY = "bookings";
    private static BookingManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private BookingManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized BookingManager getInstance(Context context) {
        if (instance == null) {
            instance = new BookingManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveBooking(Booking booking) {
        List<Booking> bookings = getBookings();
        bookings.add(booking);
        saveBookingsList(bookings);

        // Log the saved booking
        Log.d("BookingManager", "Booking saved: " + booking.toString());
    }

    public List<Booking> getBookings() {
        String json = sharedPreferences.getString(BOOKINGS_KEY, null);
        if (json == null) {
            Log.d("BookingManager", "No bookings found.");
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<Booking>>() {}.getType();
            List<Booking> bookings = gson.fromJson(json, type);
            Log.d("BookingManager", "Bookings loaded: " + bookings.size() + " bookings found.");
            return bookings;
        } catch (Exception e) {
            Log.e("BookingManager", "Error loading bookings", e);
            return new ArrayList<>();
        }
    }

    private void saveBookingsList(List<Booking> bookings) {
        String json = gson.toJson(bookings);
        sharedPreferences.edit().putString(BOOKINGS_KEY, json).apply();

        // Log the saved bookings
        Log.d("BookingManager", "Bookings list saved: " + bookings.size() + " bookings.");
    }

    public void updateBooking(Booking booking, int position) {
        List<Booking> bookings = getBookings();
        if (position >= 0 && position < bookings.size()) {
            bookings.set(position, booking);
            saveBookingsList(bookings);
        }
    }

    /**
     * Update a booking based on matching event date and planner name
     */
    public void updateBookingByDateAndPlanner(Booking booking) {
        List<Booking> bookings = getBookings();
        boolean found = false;

        for (int i = 0; i < bookings.size(); i++) {
            Booking existingBooking = bookings.get(i);
            if (existingBooking.getEventDate().equals(booking.getEventDate()) &&
                    existingBooking.getPlannerName().equals(booking.getPlannerName())) {
                bookings.set(i, booking);
                found = true;
                break;
            }
        }

        if (found) {
            saveBookingsList(bookings);
            Log.d("BookingManager", "Booking updated: " + booking.toString());
        } else {
            Log.d("BookingManager", "No matching booking found to update");
        }
    }

    public void clearAllBookings() {
        sharedPreferences.edit().remove(BOOKINGS_KEY).apply();
    }

    /**
     * Get all pending approval bookings
     */
    public List<Booking> getPendingApprovalBookings() {
        List<Booking> allBookings = getBookings();
        List<Booking> pendingBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (!booking.isApproved() && !booking.isCompleted()) {
                pendingBookings.add(booking);
            }
        }

        return pendingBookings;
    }

    /**
     * Get all approved but not completed bookings
     */
    public List<Booking> getCurrentBookings() {
        List<Booking> allBookings = getBookings();
        List<Booking> currentBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.isApproved() && !booking.isCompleted()) {
                currentBookings.add(booking);
            }
        }

        return currentBookings;
    }

    /**
     * Get all completed bookings
     */
    public List<Booking> getCompletedBookings() {
        List<Booking> allBookings = getBookings();
        List<Booking> completedBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            if (booking.isCompleted()) {
                completedBookings.add(booking);
            }
        }

        return completedBookings;
    }

    /**
     * Get bookings created in the current month
     */
    public List<Booking> getCurrentMonthBookings() {
        List<Booking> allBookings = getBookings();
        List<Booking> monthBookings = new ArrayList<>();

        // Get current month and year
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        for (Booking booking : allBookings) {
            try {
                String dateStr = booking.getEventDate().split(" at ")[0];
                Date date = dateFormat.parse(dateStr);
                Calendar bookingCal = Calendar.getInstance();
                bookingCal.setTime(date);

                if (bookingCal.get(Calendar.MONTH) == currentMonth &&
                        bookingCal.get(Calendar.YEAR) == currentYear) {
                    monthBookings.add(booking);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return monthBookings;
    }

    /**
     * Get all upcoming bookings sorted by date (closest first)
     */
    public List<Booking> getUpcomingBookingsSorted() {
        List<Booking> allBookings = getBookings();
        List<Booking> upcomingBookings = new ArrayList<>();

        // Filter upcoming bookings (not completed)
        for (Booking booking : allBookings) {
            if (!booking.isCompleted() && !booking.isEventInPast()) {
                upcomingBookings.add(booking);
            }
        }

        // Sort by date
        Collections.sort(upcomingBookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking b1, Booking b2) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);
                    Date date1 = dateFormat.parse(b1.getEventDate());
                    Date date2 = dateFormat.parse(b2.getEventDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        return upcomingBookings;
    }

    /**
     * Approve a pending booking
     */
    public void approveBooking(Booking booking) {
        booking.setApproved(true);
        updateBookingByDateAndPlanner(booking);
    }

    /**
     * Mark a booking as completed
     */
    public void completeBooking(Booking booking) {
        booking.setCompleted(true);
        updateBookingByDateAndPlanner(booking);
    }

    /**
     * Calculate total earnings for all bookings
     */
    public int calculateTotalEarnings() {
        List<Booking> allBookings = getBookings();
        int total = 0;

        for (Booking booking : allBookings) {
            total += booking.getTotalPrice();
        }

        return total;
    }

    /**
     * Calculate earnings for the current month
     */
    public int calculateCurrentMonthEarnings() {
        List<Booking> monthBookings = getCurrentMonthBookings();
        int total = 0;

        for (Booking booking : monthBookings) {
            total += booking.getTotalPrice();
        }

        return total;
    }
}