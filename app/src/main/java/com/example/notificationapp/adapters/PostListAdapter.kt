package com.example.notificationapp.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.R
import com.example.notificationapp.api.API
import com.example.notificationapp.api.PostResponse
import com.example.notificationapp.app.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.app.getMkdFormatter
import com.example.notificationapp.app.toTimeString
import com.example.notificationapp.view.activities.CreatePostActivity
import com.example.notificationapp.view.activities.PostActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PostListAdapter(
    private val mClubName: String,
    private val mClubID: String,
    private var mPosts: List<PostResponse>,
    private val mContext: Context
) : RecyclerView.Adapter<PostListAdapter.CustomVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomVH(View.inflate(parent.context, R.layout.post_list_item, null))

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        holder.bindView(mPosts[position])
    }

    private fun updatePosts(posts: List<PostResponse>) {
        mPosts = posts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = mPosts.size

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.admin_name)
        private val description: AppCompatTextView = itemView.findViewById(R.id.textDescription)
        private val background: MaterialCardView = itemView.findViewById(R.id.item_background)
        private val profilePic: ImageView = itemView.findViewById(R.id.admin_profile_pic)
        private val dateTime: AppCompatTextView = itemView.findViewById(R.id.post_time)

        private val editLayout: LinearLayout = itemView.findViewById(R.id.edit_layout)
        private val updateBtn: MaterialCardView = itemView.findViewById(R.id.update_btn)
        private val deleteBtn: MaterialCardView = itemView.findViewById(R.id.delete_btn)

        private var avatarUrl: String = ""

        fun bindView(postResponse: PostResponse) {
            mContext.getMkdFormatter().setMarkdown(description, postResponse.message)
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
                    putExtra(Constants.MESSAGE, postResponse.message)
                    putExtra(Constants.AVATAR, avatarUrl)
                    putExtra(Constants.CLUB_NAME, mClubName)
                })
            }
            editLayout.isVisible = FirebaseAuth.getInstance().currentUser!!.email!! == postResponse.adminEmail

            updateBtn.setOnClickListener {
                mContext.startActivity(Intent(mContext, CreatePostActivity::class.java).apply {
                    putExtra(Constants.MESSAGE, postResponse.message)
                    putExtra(Constants.EDIT_MODE, true)
                    putExtra(Constants.POST_ID, postResponse.id)
                    putExtra(Constants.CLUB_ID, mClubID)
                    putExtra(Constants.CLUB_NAME, mClubName)
                })
            }
            deleteBtn.setOnClickListener {
                MaterialAlertDialogBuilder(mContext)
                    .setMessage("Are you sure you want to delete this post ?")
                    .setPositiveButton("Delete") { d, _ ->
                        API.deletePost(UserInstance.getAuthToken(mContext), postResponse.id, {
                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
                            d.dismiss()
                            API.getClubPosts(UserInstance.getAuthToken(mContext), mClubID, { updatePosts(it) }) {}
                        }) {
                            Toast.makeText(mContext, "$it: Couldn't delete post", Toast.LENGTH_SHORT).show()
                            d.dismiss()
                        }
                    }
                    .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
                    .show()
            }
        }
    }
}