package com.example.notificationapp.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notificationapp.Constants
import com.example.notificationapp.R
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.data.adapters.PostListAdapter
import com.example.notificationapp.data.network.API
import com.example.notificationapp.databinding.ActivityClubBinding

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
        setValues()

        binding.postCardView.setBackgroundResource(R.drawable.shape_white_club)
        binding.clubPostRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.fab.isVisible = UserInstance.isAdmin()
        binding.fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            intent.putExtra(Constants.CLUB_ID, mClubID)
            startActivity(intent)
            finish()
        }
    }

    private fun getClubPosts(clubID: String) {
        API.getClubPosts(UserInstance.getAuthToken(this), clubID, {
            postListAdapter = PostListAdapter(it, this@ClubActivity)
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
        setListener()
        mClubID = clubID
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