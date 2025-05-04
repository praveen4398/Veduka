package com.example.vedukamad;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class SettingsActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 100;
    private static final String CHANNEL_ID = "veduka_notifications";

    SwitchCompat darkModeSwitch;
    SwitchCompat notificationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        notificationSwitch = findViewById(R.id.switch_notifications);

        // DARK MODE
        boolean isNightMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        darkModeSwitch.setChecked(isNightMode);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            recreate();
        });

        // NOTIFICATIONS
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestNotificationPermissionAndSend();
            }
        });

        // Setup click listeners for menu items
        LinearLayout helpLayout = findViewById(R.id.layout_help);
        LinearLayout privacyLayout = findViewById(R.id.layout_privacy);
        LinearLayout profileLayout = findViewById(R.id.layout_profile);  // Added profile layout

        helpLayout.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
        });

        privacyLayout.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, PrivacyActivity.class));
        });

        // Add click listener for profile section
        profileLayout.setOnClickListener(v -> {
            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
        });
    }

    private void requestNotificationPermissionAndSend() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires runtime permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                sendNotification();
            }
        } else {
            // For Android 12 or lower
            sendNotification();
        }
    }

    private void sendNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications) // Make sure this icon exists
                .setContentTitle("Veduka")
                .setContentText("Notifications are enabled!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = ContextCompat.getSystemService(this, NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
        Toast.makeText(this, "Notification sent", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Veduka Notifications";
            String description = "Channel for Veduka app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                notificationSwitch.setChecked(false); // uncheck the toggle
            }
        }
    }
}