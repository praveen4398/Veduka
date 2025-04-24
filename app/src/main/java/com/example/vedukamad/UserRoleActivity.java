package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UserRoleActivity extends AppCompatActivity {

    Button userBtn, organizerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_role);

        userBtn = findViewById(R.id.userBtn);
        organizerBtn = findViewById(R.id.organizerBtn);

        userBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
            prefs.edit().putString("userRole", "user").apply();

            Toast.makeText(UserRoleActivity.this, "Welcome User", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() ->
                    startActivity(new Intent(UserRoleActivity.this, SignInActivity.class)), 1000);
        });

        organizerBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
            prefs.edit().putString("userRole", "organizer").apply();

            Toast.makeText(UserRoleActivity.this, "Welcome Organizer", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() ->
                    startActivity(new Intent(UserRoleActivity.this, SignInActivity.class)), 1000);
        });

    }
}
