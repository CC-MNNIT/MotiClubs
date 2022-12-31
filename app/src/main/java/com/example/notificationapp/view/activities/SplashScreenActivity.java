package com.example.notificationapp.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.example.notificationapp.R;
import com.google.android.material.imageview.ShapeableImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        TextView text = (TextView)findViewById(R.id.text);
        ShapeableImageView image = (ShapeableImageView)findViewById(androidx.appcompat.R.id.image);

        image.animate().translationY(-1600F).setDuration(1000).setStartDelay(3000);
        text.animate().translationY(1400F).setDuration(1000).setStartDelay(3000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), IntroSliderActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);


    }
}