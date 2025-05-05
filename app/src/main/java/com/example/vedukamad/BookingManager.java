package com.example.vedukamad;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    private final MutableLiveData<List<Booking>> bookingsLiveData = new MutableLiveData<>();

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

    public LiveData<List<Booking>> getBookingsLiveData() {
        return bookingsLiveData;
    }

    public void refreshBookings() {
        updateLiveData();
    }

    private void updateLiveData() {
        bookingsLiveData.postValue(getBookings());
    }

    public void saveBooking(Booking booking) {
        List<Booking> bookings = getBookings();
        bookings.add(booking);
        saveBookingsList(bookings);
        Log.d("BookingManager", "Booking saved: " + booking);
        updateLiveData();
    }

    public List<Booking> getBookings() {
        String json = sharedPreferences.getString(BOOKINGS_KEY, null);
        if (json == null) {
            Log.d("BookingManager", "No bookings found.");
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<Booking>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e("BookingManager", "Error loading bookings", e);
            return new ArrayList<>();
        }
    }

    private void saveBookingsList(List<Booking> bookings) {
        String json = gson.toJson(bookings);
        sharedPreferences.edit().putString(BOOKINGS_KEY, json).apply();
        Log.d("BookingManager", "Bookings list saved: " + bookings.size() + " bookings.");
    }

    public void updateBooking(Booking booking, int position) {
        List<Booking> bookings = getBookings();
        if (position >= 0 && position < bookings.size()) {
            bookings.set(position, booking);
            saveBookingsList(bookings);
            updateLiveData();
        }
    }

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
            Log.d("BookingManager", "Booking updated: " + booking);
            updateLiveData();
        } else {
            Log.d("BookingManager", "No matching booking found to update");
        }
    }

    public void clearAllBookings() {
        sharedPreferences.edit().remove(BOOKINGS_KEY).apply();
        updateLiveData();
    }

    public List<Booking> getPendingApprovalBookings() {
        List<Booking> pendingBookings = new ArrayList<>();
        for (Booking booking : getBookings()) {
            if (!booking.isApproved() && !booking.isCompleted()) {
                pendingBookings.add(booking);
            }
        }
        return pendingBookings;
    }

    public List<Booking> getCurrentBookings() {
        List<Booking> currentBookings = new ArrayList<>();
        for (Booking booking : getBookings()) {
            if (booking.isApproved() && !booking.isCompleted()) {
                currentBookings.add(booking);
            }
        }
        return currentBookings;
    }

    public List<Booking> getCompletedBookings() {
        List<Booking> completedBookings = new ArrayList<>();
        for (Booking booking : getBookings()) {
            if (booking.isCompleted()) {
                completedBookings.add(booking);
            }
        }
        return completedBookings;
    }

    public List<Booking> getCurrentMonthBookings() {
        List<Booking> monthBookings = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);

        for (Booking booking : getBookings()) {
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

    public List<Booking> getUpcomingBookingsSorted() {
        List<Booking> upcomingBookings = new ArrayList<>();
        for (Booking booking : getBookings()) {
            if (!booking.isCompleted() && !booking.isEventInPast()) {
                upcomingBookings.add(booking);
            }
        }

        Collections.sort(upcomingBookings, (b1, b2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy 'at' HH:mm", Locale.ENGLISH);
                Date date1 = dateFormat.parse(b1.getEventDate());
                Date date2 = dateFormat.parse(b2.getEventDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                return 0;
            }
        });

        return upcomingBookings;
    }

    public void approveBooking(Booking booking) {
        booking.setApproved(true);
        updateBookingByDateAndPlanner(booking);
    }

    public void completeBooking(Booking booking) {
        booking.setCompleted(true);
        updateBookingByDateAndPlanner(booking);
    }

    public int calculateTotalEarnings() {
        int total = 0;
        for (Booking booking : getBookings()) {
            total += booking.getTotalPrice();
        }
        return total;
    }

    public int calculateCurrentMonthEarnings() {
        int total = 0;
        for (Booking booking : getCurrentMonthBookings()) {
            total += booking.getTotalPrice();
        }
        return total;
    }

    public int getPendingApprovalCount() {
        return getPendingApprovalBookings().size();
    }

    public int getCurrentBookingsCount() {
        return getCurrentBookings().size();
    }
}
