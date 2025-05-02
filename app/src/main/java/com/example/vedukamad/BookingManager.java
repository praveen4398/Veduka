package com.example.vedukamad;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public void clearAllBookings() {
        sharedPreferences.edit().remove(BOOKINGS_KEY).apply();
    }
}
