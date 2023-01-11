package com.example.notificationapp.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.R
import com.example.notificationapp.api.API
import com.example.notificationapp.api.PostResponse
import com.example.notificationapp.app.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.app.toTimeString
import com.example.notificationapp.view.activities.PostActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PostListAdapter(
    private val mClubName: String,
    private val mPosts: List<PostResponse>,
    private val mContext: Context
) : RecyclerView.Adapter<PostListAdapter.CustomVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomVH(View.inflate(parent.context, R.layout.post_list_item, null))

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        holder.bindView(mPosts[position])
    }

    override fun getItemCount(): Int = mPosts.size

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.admin_name)
        private val description: AppCompatTextView = itemView.findViewById(R.id.textDescription)
        private val background: MaterialCardView = itemView.findViewById(R.id.item_background)
        private val profilePic: ImageView = itemView.findViewById(R.id.admin_profile_pic)
        private val dateTime: AppCompatTextView = itemView.findViewById(R.id.post_time)

        private var avatarUrl: String = ""

        fun bindView(postResponse: PostResponse) {
            description.text = postResponse.message
            dateTime.text = postResponse.time.toTimeString()
            API.getUserDetails(UserInstance.getAuthToken(mContext), postResponse.adminEmail, {
                name.text = it.name
                if (it.avatar.isEmpty()) return@getUserDetails

                avatarUrl = it.avatar
                Picasso.get().load(it.avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profilePic, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        Picasso.get().load(it.avatar).into(profilePic)
                    }
                })
            }) {}
            background.setOnClickListener {
                mContext.startActivity(Intent(mContext, PostActivity::class.java).apply {
                    putExtra(Constants.ADMIN_NAME, name.text)
                    putExtra(Constants.TIME, dateTime.text)
                    putExtra(Constants.MESSAGE, description.text)
                    putExtra(Constants.AVATAR, avatarUrl)
                    putExtra(Constants.CLUB_NAME, mClubName)
                })
            }
        }
    }
}