package com.example.vedukamad;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrganizerEventsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_events);

        // Initialize the events list or UI elements to display events
        TextView eventsText = findViewById(R.id.events_text);
        eventsText.setText("Upcoming Events:"); // Update this with real event data
    }
}
