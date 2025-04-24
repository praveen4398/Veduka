package com.example.vedukamad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class IntroActivity extends AppCompatActivity {

    Button getStartedBtn;
    ViewPager viewPager;
    LinearLayout dotsLayout;
    TextView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_activity);

        getStartedBtn = findViewById(R.id.getStartedBtn);
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);  // Make sure this ID exists in your XML

        // Set up adapter for image sliding
        IntroPagerAdapter adapter = new IntroPagerAdapter(this);
        viewPager.setAdapter(adapter);

        addDotsIndicator(0); // initial dots

        // Listen for page changes
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        getStartedBtn.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, UserRoleActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addDotsIndicator(int position) {
        int numSlides = 4; // update to match the number of slides in your adapter
        dots = new TextView[numSlides];
        dotsLayout.removeAllViews();

        for (int i = 0; i < numSlides; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;")); // bullet symbol
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}
