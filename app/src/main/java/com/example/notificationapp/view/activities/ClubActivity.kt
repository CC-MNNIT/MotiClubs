package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationapp.R
import com.example.notificationapp.adapters.PostListAdapter
import com.example.notificationapp.api.API
import com.example.notificationapp.app.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.databinding.ActivityClubBinding

class ClubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClubBinding
    private lateinit var postListAdapter: PostListAdapter

    private lateinit var mClubID: String
    private lateinit var mClubName: String

    private var mClubSubscribed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClubBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setValues()
        setListener()

        binding.postCardView.setBackgroundResource(R.drawable.shape_white_club)
        binding.clubPostRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getClubPosts(clubID: String) {
        API.getClubPosts(UserInstance.getAuthToken(this), clubID, {
            postListAdapter = PostListAdapter(mClubName, it, this@ClubActivity)
            binding.clubPostRecyclerView.adapter = postListAdapter
        }) { Toast.makeText(this, "$it: Unable to fetch posts", Toast.LENGTH_SHORT).show() }
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
        mClubID = clubID
        if (clubName != null) mClubName = clubName

        mClubSubscribed = UserInstance.isSubscribedTo(clubID)
        getClubPosts(clubID)

        binding.clubName.text = clubName
        binding.clubDesc.text = clubDesc

        binding.subscribeBtn.text =
            getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
    }

    private fun setListener() {
        binding.subscribeBtn.setOnClickListener {
            if (mClubSubscribed) unsubscribe() else subscribe()
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java).apply {
                putExtra(Constants.CLUB_ID, mClubID)
                putExtra(Constants.CLUB_NAME, mClubName)
            })
            finish()
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.fab.isVisible = UserInstance.isAdmin()
    }

    private fun subscribe() {
        API.subscribeToClub(UserInstance.getAuthToken(this), mClubID, {
            mClubSubscribed = true
            binding.subscribeBtn.text =
                getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
            Toast.makeText(this, "Successfully Subscribed", Toast.LENGTH_SHORT).show()
            UserInstance.fetchUserInstance(this@ClubActivity, {}) {}
        }) { Toast.makeText(this, "$it: Could not subscribe. Please try again", Toast.LENGTH_SHORT).show() }
    }

    private fun unsubscribe() {
        API.unsubscribeToClub(UserInstance.getAuthToken(this), mClubID, {
            mClubSubscribed = false
            binding.subscribeBtn.text =
                getString(if (mClubSubscribed) R.string.unsubscribe else R.string.subscribe)
            Toast.makeText(this, "Successfully Unsubscribed", Toast.LENGTH_SHORT).show()
            UserInstance.fetchUserInstance(this@ClubActivity, {}) {}
        }) { Toast.makeText(this, "$it: Could not unsubscribe. Please try again", Toast.LENGTH_SHORT).show() }
    }
}