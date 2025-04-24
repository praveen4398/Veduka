package com.example.vedukamad;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventDetailActivity extends AppCompatActivity {

    private RecyclerView plannerRecyclerView;
    private PlannerAdapter plannerAdapter;
    private ArrayList<Planner> plannerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_event_detail);

        TextView titleTextView = findViewById(R.id.eventTitle); // Optional title
        plannerRecyclerView = findViewById(R.id.plannerRecyclerView);

        String eventType = getIntent().getStringExtra("event_type");
        titleTextView.setText(eventType != null ? eventType.toUpperCase() : "Event");

        // Dummy planners — Replace with actual logic later
        plannerList = new ArrayList<>();
        if ("Marriage".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Vivah Planners", "Jaipur, India", 4.5f, R.drawable.marriage1));
            plannerList.add(new Planner("Blissful Beginnings", "Jaipur, India", 4.5f, R.drawable.marriage2));
        } else if ("Birthday".equalsIgnoreCase(eventType)) {
            plannerList.add(new Planner("Happy Birthdays", "Mumbai, India", 4.7f, R.drawable.marriage1));
            plannerList.add(new Planner("Party Popperz", "Delhi, India", 4.6f, R.drawable.marriage2));
        }

        plannerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        plannerAdapter = new PlannerAdapter(this, plannerList);
        plannerRecyclerView.setAdapter(plannerAdapter);
    }
}
