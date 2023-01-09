package com.example.notificationapp.view.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.data.adapters.ClubListAdapter
import com.example.notificationapp.data.network.ClubModel
import com.example.notificationapp.data.network.PostResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.ActivityClubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClubBinding
    private var posts: List<PostResponse> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.postCardView.setBackgroundResource(R.drawable.shape_white_club);
        setListener()
        setValues()
    }

    private fun getClubPosts(clubID: String) {
        val preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject()
            .getClubPosts(preferences.getString(Constants.TOKEN, ""),clubID)
            .enqueue(object : Callback<List<PostResponse>?> {
                override fun onResponse(
                    call: Call<List<PostResponse>?>,
                    response: Response<List<PostResponse>?>
                ) {
                    posts = response.body()!!

                }

                override fun onFailure(call: Call<List<PostResponse>?>, t: Throwable) {

                }

            })

    }

    private fun setValues() {
        val clubName = intent.getStringExtra(Constants.CLUB_NAME)
        val clubDesc = intent.getStringExtra(Constants.CLUB_DESC)
        val clubID = intent.getStringExtra(Constants.CLUB_ID)
        if (clubID != null) {
            getClubPosts(clubID)
        }

        binding.clubName.text = clubName
        binding.descTv.text = clubDesc
    }

    private fun setListener() {}
}