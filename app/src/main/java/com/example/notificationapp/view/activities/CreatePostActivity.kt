package com.example.notificationapp.view.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.data.network.PostModel
import com.example.notificationapp.data.network.PostResponse
import com.example.notificationapp.data.network.UserModel
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.ActivityCreatePostBinding
import com.example.notificationapp.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePostActivity : AppCompatActivity() {
    private var mClubID: String? = null
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
        Log.d("create","setListner called")

        binding.sendPost.setOnClickListener(View.OnClickListener {
            val message: String = binding.etMessage.text.toString()
            Log.d("create","send buttton clicked")

            sendPost(PostModel(message))
        })

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
    private fun sendPost(postModel : PostModel){
        Log.d("create","send post function calling")

        val preferences : SharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCE, MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject().sendPost(preferences.getString(Constants.TOKEN,""), mClubID,postModel)
            .enqueue(object : Callback<PostResponse?> {
                override fun onResponse(call: Call<PostResponse?>, response: Response<PostResponse?>) {
                    if (response.body() != null) {
                        Log.d("create","sent successfulyy")
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Sent Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.etMessage.text = null
                    }else{
                        Log.d("create","Code: " + response.code().toString())

                    }
                }

                override fun onFailure(call: Call<PostResponse?>, t: Throwable) {
                    binding.sendPost.isEnabled = true
                    Log.d("create","error occured")

                }
            })

    }
}