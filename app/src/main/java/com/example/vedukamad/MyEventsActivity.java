package com.example.vedukamad;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView titleView;
    private MyEventsAdapter adapter;
    private BookingManager bookingManager;
    private LiveData<List<Booking>> bookingsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        recyclerView = findViewById(R.id.my_events_recycler);
        titleView = findViewById(R.id.my_events_title);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyEventsAdapter();
        recyclerView.setAdapter(adapter);

        bookingManager = BookingManager.getInstance(this);
        bookingsLiveData = bookingManager.getBookingsLiveData();

        bookingsLiveData.observe(this, bookings -> {
            adapter.setEvents(bookings);
            titleView.setText("My Events (" + bookings.size() + ")");
        });
    }
}
