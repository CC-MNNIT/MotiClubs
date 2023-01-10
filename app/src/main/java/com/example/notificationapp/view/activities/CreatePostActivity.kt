package com.example.notificationapp.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.data.network.API
import com.example.notificationapp.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private lateinit var mClubID: String
    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setValues()
        setListener()
    }

    private fun setListener() {
        binding.sendPost.setOnClickListener {
            val message: String = binding.etMessage.text.toString()
            API.sendPost(UserInstance.getAuthToken(this), mClubID, message, {
                Toast.makeText(this, "Post Sent", Toast.LENGTH_SHORT).show()
                finish()
            }) { Toast.makeText(this, "$it: Unable to post", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setValues() {
        val clubID = intent.getStringExtra(Constants.CLUB_ID)
        if (clubID == null) {
            Toast.makeText(this, "Error: Club ID NULL", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        mClubID = clubID
    }
}