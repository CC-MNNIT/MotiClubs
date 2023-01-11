package com.example.notificationapp.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.R
import com.example.notificationapp.app.Constants
import com.example.notificationapp.databinding.ActivityPostBinding
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValues()

        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setValues() {
        binding.toolbar.title = intent.getStringExtra(Constants.CLUB_NAME) ?: "Club"
        binding.postTime.text = intent.getStringExtra(Constants.TIME) ?: "NA"
        binding.adminName.text = intent.getStringExtra(Constants.ADMIN_NAME) ?: "NA"
        binding.message.text = intent.getStringExtra(Constants.MESSAGE) ?: "NA"

        val adminAvatar = intent.getStringExtra(Constants.AVATAR) ?: "NA"
        if (adminAvatar.isEmpty()) return
        Picasso.get().load(adminAvatar).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_icon)
            .into(binding.adminProfilePic, object : com.squareup.picasso.Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Picasso.get().load(adminAvatar).placeholder(R.drawable.profile_icon).into(binding.adminProfilePic)
                }
            })
    }
}