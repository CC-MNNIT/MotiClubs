package com.example.notificationapp.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.notificationapp.R;
import com.example.notificationapp.utils.Constants;

public class ClubActivity extends AppCompatActivity {

    private TextView mClubNameTV, mClubDescTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        setReferences();
        setListener();
        setValues();
    }

    private void setValues() {
        String clubName = getIntent().getStringExtra(Constants.CLUB_NAME),
                clubDesc = getIntent().getStringExtra(Constants.CLUB_DESC),
                clubID = getIntent().getStringExtra(Constants.CLUB_ID);
        mClubNameTV.setText(clubName);
        mClubDescTV.setText(clubDesc);
    }

    private void setListener() {

    }

    private void setReferences() {
        mClubNameTV = findViewById(R.id.club_name);
        mClubDescTV = findViewById(R.id.desc_tv);
    }
}