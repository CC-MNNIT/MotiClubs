package com.example.notificationapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.Constants
import com.example.notificationapp.databinding.ActivityClubBinding

class ClubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListener()
        setValues()
    }

    private fun setValues() {
        val clubName = intent.getStringExtra(Constants.CLUB_NAME)
        val clubDesc = intent.getStringExtra(Constants.CLUB_DESC)
        val clubID = intent.getStringExtra(Constants.CLUB_ID)

        binding.clubName.text = clubName
        binding.descTv.text = clubDesc
    }

    private fun setListener() {}
}