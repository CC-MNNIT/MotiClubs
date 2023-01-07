package com.example.notificationapp.view.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.utils.Constants

class ClubActivity : AppCompatActivity() {

    private lateinit var mClubNameTV: TextView
    private lateinit var mClubDescTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club)
        setReferences()
        setListener()
        setValues()
    }

    private fun setValues() {
        val clubName = intent.getStringExtra(Constants.CLUB_NAME)
        val clubDesc = intent.getStringExtra(Constants.CLUB_DESC)
        val clubID = intent.getStringExtra(Constants.CLUB_ID)

        mClubNameTV.text = clubName
        mClubDescTV.text = clubDesc
    }

    private fun setListener() {}
    private fun setReferences() {
        mClubNameTV = findViewById(R.id.club_name)
        mClubDescTV = findViewById(R.id.desc_tv)
    }
}