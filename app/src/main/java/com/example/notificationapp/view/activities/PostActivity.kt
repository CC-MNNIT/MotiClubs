package com.example.notificationapp.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.databinding.ActivityCreatePostBinding
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
    }

    private fun setValues() {
        val adminName = intent.getStringExtra(Constants.ADMIN_NAME)
        val adminAvatar = intent.getStringExtra(Constants.AVATAR)
        val time = intent.getStringExtra(Constants.TIME)
        val message = intent.getStringExtra(Constants.MESSAGE)
        Log.d("Hello : ", adminName + " "+ adminAvatar + " "+ time + " "+ message)
        binding.postTime.text = time
        binding.adminName.text = adminName
        binding.message.text = message
        if(adminAvatar!!.isNotEmpty())
            Picasso.get().load(adminAvatar).networkPolicy(NetworkPolicy.OFFLINE).into( binding.adminProfilePic, object : com.squareup.picasso.Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception?) {
                    Picasso.get().load(adminAvatar).into(binding.adminProfilePic)
                }
            })
    }
}