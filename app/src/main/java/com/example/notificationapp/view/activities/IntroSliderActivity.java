package com.example.notificationapp.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.notificationapp.R;
import com.example.notificationapp.adapters.IntroSliderAdapter;
import com.example.notificationapp.data.network.model.ClubModel;
import com.example.notificationapp.data.network.model.IntroSlide;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IntroSliderActivity extends AppCompatActivity {

    // Initializing required variables
    IntroSliderAdapter introSliderAdapter;
    ViewPager2 introSliderViewPager;
    Button next_btn;
    List<IntroSlide> introSlides = new ArrayList<IntroSlide>();
    IntroSlide introSlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        // hiding action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // adding data to intro slider data set
        introSlides.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));
        introSlides.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));
        introSlides.add(new IntroSlide(
                "welcome",
                "welcome",
                R.drawable.campus
        ));

        // creating adapter object
        introSliderAdapter = new IntroSliderAdapter(introSlides);

        // setting adapter to the view
        introSliderViewPager = (ViewPager2) findViewById(R.id.introSliderViewPager);
        introSliderViewPager.setAdapter(introSliderAdapter);

        next_btn = (Button) findViewById(R.id.next_btn);
        next_btn.setOnClickListener(view -> {
            if (introSliderViewPager.getCurrentItem() + 1 < introSliderAdapter.getItemCount()) {
                introSliderViewPager.setCurrentItem(introSliderViewPager.getCurrentItem() + 1);
            } else {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                finish();
            }
        });
    }
}