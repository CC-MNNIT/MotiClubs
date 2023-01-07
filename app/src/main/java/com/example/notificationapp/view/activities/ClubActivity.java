package com.example.notificationapp.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.notificationapp.R;
import com.example.notificationapp.utils.Constants;

public class ClubActivity extends AppCompatActivity {

    private TextView club_name, club_desc;
    String club_name_val, club_desc_val, club_id_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        setReferences();
        setListener();
        setValues();
    }

    private void setValues() {
        club_name_val = getIntent().getStringExtra(Constants.CLUB_NAME);
        club_desc_val = getIntent().getStringExtra(Constants.CLUB_DESC);
        club_id_val = getIntent().getStringExtra(Constants.CLUB_ID);
        club_name.setText(club_name_val);
        club_desc.setText(club_desc_val);
    }

    private void setListener() {

    }

    private void setReferences() {
        club_name = findViewById(R.id.club_name);
        club_desc = findViewById(R.id.desc_tv);
    }
}