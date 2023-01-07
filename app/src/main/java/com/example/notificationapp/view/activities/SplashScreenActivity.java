package com.example.notificationapp.view.activities;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notificationapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        TextView text = (TextView) findViewById(R.id.text);
        ShapeableImageView image = (ShapeableImageView) findViewById(androidx.appcompat.R.id.image);
        image.animate().translationY(-1600F).setDuration(1000).setStartDelay(3000);
        text.animate().translationY(1400F).setDuration(1000).setStartDelay(3000);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mAuth.getCurrentUser() != null) {
                if (mAuth.getCurrentUser().isEmailVerified()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    mAuth.signOut();
                    makeText(getApplicationContext(), "Please Verify Your Email.", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), IntroSliderActivity.class);
                startActivity(intent);
            }
            finish();
        }, 4000);
    }
}