package com.example.notificationapp.data.adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notificationapp.R
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.data.network.API
import com.example.notificationapp.data.network.PostResponse
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class PostListAdapter(private val mPosts: List<PostResponse>, private val mContext: Context) :
    RecyclerView.Adapter<PostListAdapter.CustomVH>() {

    companion object {
        private const val TAG = "PostListAdapter"
    }

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

        fun bindView(postResponse: PostResponse) {
            Log.d(TAG, "bindView: $postResponse")
            description.text = postResponse.message
            dateTime.text = convertLongToTime(postResponse.time)

            API.getUserDetails(UserInstance.getAuthToken(mContext), postResponse.adminEmail, {
                name.text = it.name
                if (it.avatar.isEmpty()) return@getUserDetails
                Picasso.get().load(it.avatar).networkPolicy(NetworkPolicy.OFFLINE).into(profilePic, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {}

                    override fun onError(e: Exception?) {
                        Picasso.get().load(it.avatar).into(profilePic)
                    }
                })
            }) {}
//            background.setOnClickListener {
//                val intent = Intent(mContext, ClubActivity::class.java)
////                intent.putExtra(Constants.CLUB_NAME, mPosts[adapterPosition].name)
////                intent.putExtra(Constants.CLUB_ID, mPosts[adapterPosition].id)
////                intent.putExtra(Constants.CLUB_DESC, mClubs[adapterPosition].description)
//                mContext.startActivity(intent)
//            }
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
}