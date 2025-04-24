package com.example.vedukamad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private TextView logoText;
    private ImageView vLogo;
    private Handler handler = new Handler();
    private String fullText = "VEDUKA";
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoText = findViewById(R.id.logo_text);
        vLogo = findViewById(R.id.v_logo);

        // Start revealing text after short delay
        handler.postDelayed(this::revealLetters, 600);
    }

    private void revealLetters() {
        if (index <= fullText.length()) {
            logoText.setText(fullText.substring(0, index));
            index++;
            handler.postDelayed(this::revealLetters, 300);
        } else {
            startExpandAnimation();
        }
    }

    private void startExpandAnimation() {
        Animation expand = AnimationUtils.loadAnimation(this, R.anim.zoom_expand);
        vLogo.startAnimation(expand);

        expand.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                View root = findViewById(android.R.id.content);
                root.animate().alpha(0f).setDuration(300).withEndAction(() -> {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    SharedPreferences prefs = getSharedPreferences("VedukaPrefs", MODE_PRIVATE);
                    String role = prefs.getString("userRole", "none");

                    Intent intent;
                    if (currentUser != null) {
                        if ("organizer".equals(role)) {
                            intent = new Intent(SplashActivity.this, OrganizerDashboardActivity.class);
                        } else if ("user".equals(role)) {
                            intent = new Intent(SplashActivity.this, HomePageActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, UserRoleActivity.class);
                        }
                    } else {
                        intent = new Intent(SplashActivity.this, IntroActivity.class);
                    }

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
}
