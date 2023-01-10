package com.example.notificationapp.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.data.adapters.PostListAdapter
import com.example.notificationapp.data.network.ClubSubscriptionModel
import com.example.notificationapp.data.network.PostResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.ActivityClubBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClubActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ClubActivity"
    }

    private lateinit var binding: ActivityClubBinding
    private lateinit var postListAdapter: PostListAdapter

    private lateinit var mClubID: String
    private var mClubSubscribed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postCardView.setBackgroundResource(R.drawable.shape_white_club)
        binding.clubPostRecyclerView.layoutManager = LinearLayoutManager(this)
        setValues()

        binding.fab.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra(Constants.CLUB_ID, mClubID)
            startActivity(intent)
            finish()
        })
    }

    private fun getClubPosts(clubID: String) {
        val preferences = getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject()
            .getClubPosts(preferences.getString(Constants.TOKEN, ""), clubID)
            .enqueue(object : Callback<List<PostResponse>?> {
                override fun onResponse(
                    call: Call<List<PostResponse>?>,
                    response: Response<List<PostResponse>?>
                ) {
                    val posts = response.body()!!
                    postListAdapter = PostListAdapter(posts, this@ClubActivity)
                    binding.clubPostRecyclerView.adapter = postListAdapter
                }

                override fun onFailure(call: Call<List<PostResponse>?>, t: Throwable) {

                }

            })

    }

    private fun setValues() {
        val clubName = intent.getStringExtra(Constants.CLUB_NAME)
        val clubDesc = intent.getStringExtra(Constants.CLUB_DESC)
        val clubID = intent.getStringExtra(Constants.CLUB_ID)
        if (clubID == null) {
            Toast.makeText(this, "Error: Club ID NULL", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setListener()
        mClubID = clubID
        mClubSubscribed = UserInstance.isSubscribedTo(clubID)
        getClubPosts(clubID)

        binding.clubName.text = clubName
        binding.descTv.text = clubDesc

        binding.subscribeBtn.text = getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
    }

    private fun setListener() {
        binding.subscribeBtn.setOnClickListener {
            if (mClubSubscribed) unsubscribe() else subscribe()
        }


    }

    private fun subscribe() {
        RetrofitAccessObject.getRetrofitAccessObject().subscribeToClub(UserInstance.getAuthToken(this), ClubSubscriptionModel(mClubID))
            .enqueue(object : Callback<ClubSubscriptionModel?> {
                override fun onResponse(call: Call<ClubSubscriptionModel?>, response: Response<ClubSubscriptionModel?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        Toast.makeText(this@ClubActivity, "${response.code()} Could not subscribe. Please try again", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    mClubSubscribed = true
                    binding.subscribeBtn.text = getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
                    UserInstance.fetchUserInstance(this@ClubActivity, {}) {}
                }

                override fun onFailure(call: Call<ClubSubscriptionModel?>, t: Throwable) {}
            })
    }

    private fun unsubscribe() {
        RetrofitAccessObject.getRetrofitAccessObject().unsubscribeToClub(UserInstance.getAuthToken(this), ClubSubscriptionModel(mClubID))
            .enqueue(object : Callback<ClubSubscriptionModel?> {
                override fun onResponse(call: Call<ClubSubscriptionModel?>, response: Response<ClubSubscriptionModel?>) {
                    val body = response.body()
                    if (!response.isSuccessful || body == null) {
                        Toast.makeText(this@ClubActivity, "${response.code()} Could not unsubscribe. Please try again", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }
                    mClubSubscribed = false
                    binding.subscribeBtn.text = getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
                    UserInstance.fetchUserInstance(this@ClubActivity, {}) {}
                }

                override fun onFailure(call: Call<ClubSubscriptionModel?>, t: Throwable) {}
            })
    }

}