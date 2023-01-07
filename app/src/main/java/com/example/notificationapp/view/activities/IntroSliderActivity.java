package com.example.notificationapp.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notificationapp.R;
import com.example.notificationapp.adapters.IntroSliderAdapter;
import com.example.notificationapp.data.network.model.IntroSlide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntroSliderActivity extends AppCompatActivity {

    // Initializing required variables
    private IntroSliderAdapter mIntroSliderAdapter;
    private ViewPager2 mIntroSliderViewPager;
    private final List<IntroSlide> mIntroSlidesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        // hiding action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // adding data to intro slider data set
        mIntroSlidesList.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));
        mIntroSlidesList.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));
        mIntroSlidesList.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));

        // creating adapter object
        mIntroSliderAdapter = new IntroSliderAdapter(mIntroSlidesList);

        // setting adapter to the view
        mIntroSliderViewPager = (ViewPager2) findViewById(R.id.introSliderViewPager);
        mIntroSliderViewPager.setAdapter(mIntroSliderAdapter);

        MaterialButton mNextBtn = findViewById(R.id.next_btn);
        mNextBtn.setOnClickListener(view -> {
            if (mIntroSliderViewPager.getCurrentItem() + 1 < mIntroSliderAdapter.getItemCount()) {
                mIntroSliderViewPager.setCurrentItem(mIntroSliderViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });
    }
}